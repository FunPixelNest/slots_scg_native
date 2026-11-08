package com.funpixelnest.slots.engine

import kotlin.random.Random

/** Player data shared by every machine (Unity: SlotPlayer). */
interface PlayerStore {
    var coins: Long
    var level: Int
    /** 0..100 percent towards the next level. */
    var levelProgress: Float
    fun lineBet(themeId: String): Int?
    fun setLineBet(themeId: String, bet: Int)
    fun jackpotPool(themeId: String, tier: JackpotTier): Long
    fun setJackpotPool(themeId: String, tier: JackpotTier, value: Long)
    /** Saved per-machine counters (e.g. the diamond meter). */
    fun counter(key: String): Long = 0L
    fun setCounter(key: String, value: Long) {}
}

class InMemoryPlayerStore(
    override var coins: Long = Economy.START_COINS,
    override var level: Int = 1,
    override var levelProgress: Float = 0f,
) : PlayerStore {
    private val bets = HashMap<String, Int>()
    private val pools = HashMap<String, Long>()
    override fun lineBet(themeId: String) = bets[themeId]
    override fun setLineBet(themeId: String, bet: Int) { bets[themeId] = bet }
    override fun jackpotPool(themeId: String, tier: JackpotTier) = pools["$themeId/$tier"] ?: 0L
    override fun setJackpotPool(themeId: String, tier: JackpotTier, value: Long) { pools["$themeId/$tier"] = value }
    private val counters = HashMap<String, Long>()
    override fun counter(key: String) = counters[key] ?: 0L
    override fun setCounter(key: String, value: Long) { counters[key] = value }
}

/** Kept for callers of the original Unity defaults. */
object PlayerRules {
    const val START_COINS = Economy.START_COINS
}

enum class WinTier { NONE, NORMAL, BIG, MEGA, HUGE }

data class SpinResult(
    val outcome: SpinOutcome,
    val totalBet: Long,
    val wasFreeSpin: Boolean,
    /** Line + scatter win (without jackpot). */
    val winCoins: Long,
    val freeSpinsWon: Int,
    val tier: WinTier,
    val jackpot: JackpotWin?,
    val levelUp: Int?,
    val levelUpReward: Long,
    /** XP this spin earned (shown as "+N XP" in the HUD). */
    val xpGained: Int = 0,
    /** What this machine's feature did on the spin. */
    val effects: FeatureEffects = FeatureEffects(),
    /** Symbols the reels actually stopped on, before features changed them. */
    val baseGrid: Grid = outcome.grid,
) {
    val totalWin: Long get() = winCoins + (jackpot?.coins ?: 0L)
}

/**
 * Bet, balance, free spins, jackpots and level progress for one machine
 * (Unity: SlotControls + the win/lose states of SlotController, plus the coin economy).
 */
class GameSession(
    val machine: SlotMachine,
    private val store: PlayerStore,
    private val random: Random = Random.Default,
) {
    private val cfg = machine.config
    private val bets = Economy.TOTAL_BETS
    val linesCount: Int get() = machine.lines.size

    /** Index into [Economy.TOTAL_BETS]. */
    var betIndex: Int = (store.lineBet(cfg.id)?.let { saved -> bets.indexOfLast { it <= saved } } ?: 0)
        .coerceIn(0, Economy.unlockedBetSteps(store.level) - 1)
        private set
    var freeSpins: Int = 0
        private set

    val coins: Long get() = store.coins
    val level: Int get() = store.level
    val levelProgress: Float get() = store.levelProgress
    val totalBet: Long get() = bets[betIndex].toLong()

    /** Coins paid for [payUnits] (pay table units are per way-bet = total bet / ways). */
    fun coinsFor(payUnits: Long): Long = payUnits * totalBet / linesCount
    val unlockedSteps: Int get() = Economy.unlockedBetSteps(store.level)
    val isMaxBet: Boolean get() = betIndex == unlockedSteps - 1

    /** Level that opens the next bet step, or null when the ladder is fully open. */
    val nextBetUnlockLevel: Int? get() =
        if (unlockedSteps >= bets.size) null else Economy.levelForBetStep(unlockedSteps)

    fun changeLineBet(delta: Int) = setBetIndex(betIndex + delta)
    fun maxBet() = setBetIndex(unlockedSteps - 1)

    fun setBetIndex(index: Int) {
        if (freeSpins > 0) return // bet is locked during free games
        betIndex = index.coerceIn(0, unlockedSteps - 1)
        store.setLineBet(cfg.id, bets[betIndex])
    }

    fun jackpotValue(tier: JackpotTier): Long = totalBet * tier.betMultiple + store.jackpotPool(cfg.id, tier)

    fun canSpin(): Boolean = freeSpins > 0 || store.coins >= totalBet

    fun spin(): Result<SpinResult> {
        val free = freeSpins > 0
        if (!free && store.coins < totalBet) return Result.failure(NotEnoughCoinsException())
        if (free) freeSpins-- else store.coins -= totalBet

        val stops = cfg.reelStrips.map { random.nextInt(it.size) }
        val baseGrid = machine.gridAt(stops)
        val (grid, fx) = applyFeatures(baseGrid, free)
        val outcome = machine.evaluateGrid(stops, grid)
        var effects = fx
        val scale = Features.payScale(feature)
        var winCoins = (coinsFor(outcome.payUnits) * scale).toLong()
        var extraSpins = 0
        if (feature == Feature.MULTIPLIER && winCoins > 0 && random.nextDouble() < Features.MULTIPLIER_CHANCE) {
            val m = random.weighted(Features.MULTIPLIERS)
            winCoins *= m
            effects = effects.copy(multiplier = m)
        }
        when (feature) {
            Feature.CASCADE -> if (outcome.lineWins.isNotEmpty()) {
                val steps = cascade(stops, grid, outcome, scale)
                winCoins = steps.sumOf { it.coins }
                effects = effects.copy(cascades = steps)
            }
            Feature.FREE_SPINS_MULTIPLIER -> if (free) {
                val m = Features.FREE_MULTIPLIERS[freeMultStep]
                winCoins *= m
                effects = effects.copy(freeMultiplier = m)
                if (winCoins > 0) freeMultStep = (freeMultStep + 1).coerceAtMost(Features.FREE_MULTIPLIERS.lastIndex)
            }
            Feature.COLLECT_METER -> {
                if (free && superFree) {
                    winCoins *= Features.SUPER_MULTIPLIER
                    effects = effects.copy(freeMultiplier = Features.SUPER_MULTIPLIER)
                }
                val n = when {
                    random.nextDouble() < Features.DOUBLE_DIAMOND_CHANCE -> 2
                    random.nextDouble() < Features.DIAMOND_CHANCE -> 1
                    else -> 0
                }
                val cells = allCells().filter { grid[it.reel][it.row] != cfg.scatterId }.shuffled(random).take(n).toSet()
                var fill = meter + cells.size
                var filled = false
                if (fill >= Features.METER_TARGET) {
                    fill -= Features.METER_TARGET
                    filled = true
                    extraSpins = Features.SUPER_SPINS
                    superFree = true
                }
                store.setCounter(meterKey, fill.toLong())
                effects = effects.copy(diamonds = cells, meterFilled = filled)
            }
            Feature.COIN_COLLECTOR -> {
                val coins = (0 until machine.reelCount - 1).flatMap { r -> (0 until machine.rowCount).map { Cell(r, it) } }
                    .filter { grid[it.reel][it.row] != cfg.scatterId && random.nextDouble() < Features.COIN_CHANCE }
                    .associateWith { totalBet * random.weighted(Features.COIN_VALUES) / 10 }
                val collector = if (random.nextDouble() < Features.COLLECTOR_CHANCE) Cell(machine.reelCount - 1, random.nextInt(machine.rowCount)) else null
                val collected = if (collector != null) coins.values.sum() else 0L
                winCoins += collected
                effects = effects.copy(coins = coins, collector = collector, collected = collected)
            }
            else -> Unit
        }
        val scatters = baseGrid.sumOf { col -> col.count { it == cfg.scatterId } }
        if (feature?.isBonusGame == true && cfg.scatterId >= 0 && scatters >= Features.BONUS_SCATTERS) {
            effects = effects.copy(bonus = feature)
        }
        var spinsWon = outcome.freeSpins * outcome.freeSpinsMultiplier
        if (cfg.bet.freeSpinsScaleWithBet) spinsWon *= betIndex + 1
        spinsWon += extraSpins
        freeSpins += spinsWon

        // only jackpot machines have the progressive Grand/Major/Minor/Mini
        val jackpot = if (free || feature?.hasJackpots != true) null else rollJackpot()
        store.coins += winCoins + (jackpot?.coins ?: 0L)

        val xp = xpForSpin(outcome.isWin)
        val (levelUp, reward) = addXp(xp.toFloat())

        if (free && freeSpins == 0) { // free games over
            stickyCells.clear()
            freeMultStep = 0
            superFree = false
        }

        val tier = Economy.winTier(winCoins, totalBet)
        return Result.success(
            SpinResult(outcome, totalBet, free, winCoins, spinsWon, tier, jackpot, levelUp, reward, xp, effects, baseGrid),
        )
    }

    private fun rollJackpot(): JackpotWin? {
        JackpotTier.entries.forEach { t ->
            val pool = store.jackpotPool(cfg.id, t)
            store.setJackpotPool(cfg.id, t, pool + (totalBet * t.poolShare).toLong())
        }
        val hit = JackpotTier.entries.reversed().firstOrNull { random.nextInt(it.odds) == 0 } ?: return null
        val win = JackpotWin(hit, jackpotValue(hit))
        store.setJackpotPool(cfg.id, hit, 0L)
        return win
    }

    /** Every spin earns XP: more at higher bets, double on a win. */
    fun xpForSpin(win: Boolean): Int = (betIndex + 1) * if (win) 2 else 1

    private fun addXp(points: Float): Pair<Int?, Long> {
        var p = store.levelProgress + points * 100f / Economy.xpForLevel(store.level)
        var up: Int? = null
        var reward = 0L
        while (p >= 100f) {
            p -= 100f
            store.level += 1
            up = store.level
            reward += Economy.levelUpReward(store.level)
        }
        store.levelProgress = p
        store.coins += reward
        return up to reward
    }

    fun addCoins(amount: Long) { store.coins += amount }

    // ------------------------------------------------------------------ features

    /** This machine's special feature, if any. */
    val feature: Feature? = Features.forTheme(cfg.id)

    /** Wilds held by Sticky Wilds during the current free games. */
    val stickyCells: MutableSet<Cell> = LinkedHashSet()

    /** Free Spins Multiplier: index into [Features.FREE_MULTIPLIERS] for the next free spin. */
    var freeMultStep: Int = 0
        private set

    /** Multiplier the next free spin will use (1 outside free spins). */
    val nextFreeMultiplier: Int get() = when {
        feature == Feature.FREE_SPINS_MULTIPLIER && freeSpins > 0 -> Features.FREE_MULTIPLIERS[freeMultStep]
        feature == Feature.COLLECT_METER && superFree && freeSpins > 0 -> Features.SUPER_MULTIPLIER
        else -> 1
    }

    /** Diamond Collection: the current free spins are Super Free Spins. */
    var superFree: Boolean = false
        private set

    private val meterKey get() = "meter_${cfg.id}"

    /** Diamonds collected towards [Features.METER_TARGET]. */
    val meter: Int get() = store.counter(meterKey).toInt()

    private fun allCells(): List<Cell> = (0 until machine.reelCount).flatMap { r -> (0 until machine.rowCount).map { Cell(r, it) } }

    /** Random symbol for a cell refilled by a cascade (never a scatter, so cascades cannot start bonuses). */
    private fun refill(reel: Int): Int {
        val strip = cfg.reelStrips[reel]
        repeat(20) { val s = strip[random.nextInt(strip.size)]; if (s != cfg.scatterId) return s }
        return strip.first { it != cfg.scatterId }
    }

    /** Cascading Reels: pay, burst the winning symbols, drop new ones in, repeat while there are line wins. */
    private fun cascade(stops: List<Int>, start: Grid, first: SpinOutcome, scale: Double): List<CascadeStep> {
        val steps = ArrayList<CascadeStep>()
        var g = start
        var out = first
        var dropped = emptySet<Cell>()
        var i = 0
        while (out.lineWins.isNotEmpty() && i < Features.MAX_CASCADES) {
            val m = Features.CASCADE_MULTIPLIERS[minOf(i, Features.CASCADE_MULTIPLIERS.lastIndex)]
            val units = if (i == 0) out.payUnits else out.linePay.toLong() * out.payMultiplier
            val cells = out.lineWins.flatMap { it.cells }.toSet()
            steps += CascadeStep(g, cells, (coinsFor(units) * scale).toLong() * m, m, dropped)
            val newDropped = HashSet<Cell>()
            g = g.mapIndexed { r, col ->
                val keep = col.filterIndexed { row, _ -> Cell(r, row) !in cells }
                val add = col.size - keep.size
                repeat(add) { newDropped += Cell(r, it) }
                List(add) { refill(r) } + keep
            }
            dropped = newDropped
            out = machine.evaluateGrid(stops, g)
            i++
        }
        steps += CascadeStep(g, emptySet(), 0L, 1, dropped) // settled grid
        return steps
    }

    private fun applyFeatures(base: Grid, free: Boolean): Pair<Grid, FeatureEffects> {
        val wild = cfg.wildId
        if (feature == null || wild < 0) return base to FeatureEffects()
        val g = base.map { it.toMutableList() }
        var fx = FeatureEffects()
        when (feature) {
            Feature.RANDOM_WILDS -> if (random.nextDouble() < Features.RANDOM_WILDS_CHANCE) {
                val cells = (1 until machine.reelCount).flatMap { r -> (0 until machine.rowCount).map { Cell(r, it) } }
                    .filter { g[it.reel][it.row] != cfg.scatterId }
                    .shuffled(random).take(2 + random.nextInt(4)).toSet()
                cells.forEach { g[it.reel][it.row] = wild }
                fx = fx.copy(randomWilds = cells)
            }
            Feature.STICKY_WILDS -> if (free) {
                stickyCells.forEach { g[it.reel][it.row] = wild }
                g.forEachIndexed { r, col -> col.forEachIndexed { row, s -> if (s == wild) stickyCells += Cell(r, row) } }
                fx = fx.copy(stickyWilds = stickyCells.toSet())
            }
            Feature.EXPANDING_WILD -> if (random.nextDouble() < Features.EXPANDING_CHANCE) {
                val reels = (1 until machine.reelCount).filter { r -> g[r].contains(wild) }.toSet()
                reels.forEach { r -> for (row in g[r].indices) g[r][row] = wild }
                fx = fx.copy(expandedReels = reels)
            }
            else -> Unit
        }
        return g to fx
    }

    // ------------------------------------------------------------------ bonus games

    fun scatterCells(grid: Grid): List<Cell> = grid.flatMapIndexed { r, col ->
        col.mapIndexedNotNull { row, s -> if (s == cfg.scatterId) Cell(r, row) else null }
    }

    fun newHoldAndSpin(result: SpinResult): HoldAndSpinGame =
        HoldAndSpinGame(result.totalBet, ::jackpotValueAtBet, scatterCells(result.baseGrid), random)

    /** Hold & Spin started from four coins (for previews). */
    fun previewHoldAndSpin(totalBet: Long): HoldAndSpinGame =
        HoldAndSpinGame(totalBet, ::jackpotValueAtBet, listOf(Cell(0, 1), Cell(2, 0), Cell(3, 2), Cell(4, 1)), random)

    fun newWheel(result: SpinResult): BonusWheel = BonusWheel(result.totalBet, ::jackpotValueAtBet)
    fun newPick(result: SpinResult): PickBonus = PickBonus(result.totalBet, ::jackpotValueAtBet, random)

    /** Credit a finished bonus game; jackpots won inside it empty their pools. */
    fun awardBonus(coins: Long, jackpots: Collection<JackpotTier> = emptyList()) {
        jackpots.toSet().forEach { store.setJackpotPool(cfg.id, it, 0L) }
        store.coins += coins
    }

    private fun jackpotValueAtBet(t: JackpotTier): Long = jackpotValue(t)

    /** Plays a started bonus automatically (used by simulations and tests). */
    fun playBonusAuto(result: SpinResult): Long = when (result.effects.bonus) {
        Feature.HOLD_AND_SPIN -> newHoldAndSpin(result).playToEnd()
        Feature.BONUS_WHEEL -> newWheel(result).let { w -> w.prizeOf(w.spin(random)).coins }
        Feature.PICK_BONUS -> newPick(result).let { p -> repeat(PickBonus.PICKS) { i -> p.pick(i) }; p.total() }
        else -> 0L
    }
}

class NotEnoughCoinsException : IllegalStateException("Not enough coins")
