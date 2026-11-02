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
}

class InMemoryPlayerStore(
    override var coins: Long = PlayerRules.START_COINS,
    override var level: Int = 1,
    override var levelProgress: Float = 0f,
) : PlayerStore {
    private val bets = HashMap<String, Int>()
    override fun lineBet(themeId: String) = bets[themeId]
    override fun setLineBet(themeId: String, bet: Int) { bets[themeId] = bet }
}

object PlayerRules {
    /** Unity default (SlotPlayer.defCoinsCount in the scenes). */
    const val START_COINS = 5000L
    const val LEVEL_UP_REWARD = 3000L
    /** Unity SlotController defaults. */
    const val LOSE_SPIN_PROGRESS = 0.5f
    const val WIN_LINE_PROGRESS = 2.0f
    /** Total win (in coins) that triggers the big-win popup (Unity SlotPlayer.minWin). */
    const val BIG_WIN = 5000L
}

enum class WinTier { NONE, NORMAL, BIG, MEGA, HUGE }

data class SpinResult(
    val outcome: SpinOutcome,
    val totalBet: Long,
    val wasFreeSpin: Boolean,
    val winCoins: Long,
    val freeSpinsWon: Int,
    val tier: WinTier,
    val levelUp: Int?,
    val levelUpReward: Long,
)

/**
 * Bet, balance, free spins and level progress for one machine
 * (Unity: SlotControls + the win/lose states of SlotController).
 */
class GameSession(
    val machine: SlotMachine,
    private val store: PlayerStore,
    private val random: Random = Random.Default,
) {
    private val cfg = machine.config
    val linesCount: Int get() = machine.lines.size

    var lineBet: Int = (store.lineBet(cfg.id) ?: cfg.bet.defaultLineBet).coerceIn(1, cfg.bet.maxLineBet)
        private set
    var freeSpins: Int = 0
        private set
    val coins: Long get() = store.coins
    val level: Int get() = store.level
    val levelProgress: Float get() = store.levelProgress
    val totalBet: Long get() = lineBet.toLong() * linesCount

    fun changeLineBet(delta: Int) = setLineBet(lineBet + delta)
    fun maxBet() = setLineBet(cfg.bet.maxLineBet)

    fun setLineBet(value: Int) {
        if (freeSpins > 0) return // bet is locked during free games
        lineBet = value.coerceIn(1, cfg.bet.maxLineBet)
        store.setLineBet(cfg.id, lineBet)
    }

    fun canSpin(): Boolean = freeSpins > 0 || store.coins >= totalBet

    fun spin(): Result<SpinResult> {
        val free = freeSpins > 0
        if (!free && store.coins < totalBet) return Result.failure(NotEnoughCoinsException())
        if (free) freeSpins-- else store.coins -= totalBet

        val outcome = machine.spin(random)
        val winCoins = outcome.payUnits * lineBet
        var spinsWon = outcome.freeSpins * outcome.freeSpinsMultiplier
        if (cfg.bet.freeSpinsScaleWithBet) spinsWon *= lineBet
        store.coins += winCoins
        freeSpins += spinsWon

        val progress = if (outcome.isWin) PlayerRules.WIN_LINE_PROGRESS * outcome.lineWins.size.coerceAtLeast(1) * lineBet
        else PlayerRules.LOSE_SPIN_PROGRESS * lineBet
        val (levelUp, reward) = addProgress(progress)

        val tier = when {
            winCoins <= 0 -> WinTier.NONE
            winCoins >= totalBet * 50 -> WinTier.HUGE
            winCoins >= totalBet * 20 -> WinTier.MEGA
            winCoins >= PlayerRules.BIG_WIN || winCoins >= totalBet * 8 -> WinTier.BIG
            else -> WinTier.NORMAL
        }
        return Result.success(SpinResult(outcome, totalBet, free, winCoins, spinsWon, tier, levelUp, reward))
    }

    /** Level needs grow with level, so higher levels take longer. */
    private fun addProgress(points: Float): Pair<Int?, Long> {
        var p = store.levelProgress + points * 100f / pointsForLevel(store.level)
        var up: Int? = null
        var reward = 0L
        while (p >= 100f) {
            p -= 100f
            store.level += 1
            up = store.level
            reward += PlayerRules.LEVEL_UP_REWARD
        }
        store.levelProgress = p
        store.coins += reward
        return up to reward
    }

    fun addCoins(amount: Long) { store.coins += amount }

    companion object {
        fun pointsForLevel(level: Int): Float = 100f + 50f * (level - 1)
    }
}

class NotEnoughCoinsException : IllegalStateException("Not enough coins")
