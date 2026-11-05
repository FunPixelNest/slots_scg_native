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
    private val bets = Economy.LINE_BETS
    val linesCount: Int get() = machine.lines.size

    /** Index into [Economy.LINE_BETS]. */
    var betIndex: Int = (store.lineBet(cfg.id)?.let { saved -> bets.indexOfLast { it <= saved } } ?: 0)
        .coerceIn(0, Economy.unlockedBetSteps(store.level) - 1)
        private set
    var freeSpins: Int = 0
        private set

    val lineBet: Int get() = bets[betIndex]
    val coins: Long get() = store.coins
    val level: Int get() = store.level
    val levelProgress: Float get() = store.levelProgress
    val totalBet: Long get() = lineBet.toLong() * linesCount
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
        store.setLineBet(cfg.id, lineBet)
    }

    fun jackpotValue(tier: JackpotTier): Long = totalBet * tier.betMultiple + store.jackpotPool(cfg.id, tier)

    fun canSpin(): Boolean = freeSpins > 0 || store.coins >= totalBet

    fun spin(): Result<SpinResult> {
        val free = freeSpins > 0
        if (!free && store.coins < totalBet) return Result.failure(NotEnoughCoinsException())
        if (free) freeSpins-- else store.coins -= totalBet

        val outcome = machine.spin(random)
        val winCoins = outcome.payUnits * lineBet
        var spinsWon = outcome.freeSpins * outcome.freeSpinsMultiplier
        if (cfg.bet.freeSpinsScaleWithBet) spinsWon *= betIndex + 1
        freeSpins += spinsWon

        val jackpot = if (free) null else rollJackpot()
        store.coins += winCoins + (jackpot?.coins ?: 0L)

        val xp = xpForSpin(outcome.isWin)
        val (levelUp, reward) = addXp(xp.toFloat())

        val tier = Economy.winTier(winCoins, totalBet)
        return Result.success(SpinResult(outcome, totalBet, free, winCoins, spinsWon, tier, jackpot, levelUp, reward, xp))
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
}

class NotEnoughCoinsException : IllegalStateException("Not enough coins")
