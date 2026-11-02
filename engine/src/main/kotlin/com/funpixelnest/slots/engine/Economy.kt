package com.funpixelnest.slots.engine

import kotlin.math.roundToLong

/**
 * Social-casino coin economy: balance in millions, a bet ladder that unlocks with level,
 * hourly and daily-streak bonuses, level-up rewards and progressive jackpots.
 */
object Economy {
    const val START_COINS = 1_000_000L

    /** Coins per line; total bet = line bet x 243 ways (2.4K ... 24.3M). */
    val LINE_BETS = listOf(10, 20, 40, 100, 200, 400, 1_000, 2_000, 4_000, 10_000, 20_000, 40_000, 100_000)

    /** Bet steps open at level 1; one more every [LEVELS_PER_BET_STEP] levels. */
    const val FREE_BET_STEPS = 4
    const val LEVELS_PER_BET_STEP = 2

    fun unlockedBetSteps(level: Int): Int =
        (FREE_BET_STEPS + (level - 1) / LEVELS_PER_BET_STEP).coerceIn(1, LINE_BETS.size)

    /** Level at which bet step [index] opens. */
    fun levelForBetStep(index: Int): Int =
        if (index < FREE_BET_STEPS) 1 else 1 + (index - FREE_BET_STEPS + 1) * LEVELS_PER_BET_STEP

    /** XP needed to go from [level] to the next one. */
    fun xpForLevel(level: Int): Float = 20f + 10f * (level - 1)

    fun levelUpReward(newLevel: Int): Long = 50_000L * newLevel

    // ------------------------------------------------------------ timed bonuses

    const val HOUR_MS = 60 * 60 * 1000L
    const val DAY_MS = 24 * HOUR_MS
    const val HOURLY_INTERVAL_MS = HOUR_MS

    fun hourlyBonus(level: Int): Long = 50_000L + 10_000L * (level - 1)

    private val DAILY = listOf(100_000L, 150_000L, 200_000L, 300_000L, 400_000L, 600_000L, 1_000_000L)
    const val DAILY_DAYS = 7

    /** Day 1..7 reward, growing 5% per level. */
    fun dailyBonus(day: Int, level: Int): Long =
        (DAILY[(day - 1).coerceIn(0, DAILY_DAYS - 1)] * (1 + 0.05 * (level - 1))).roundToLong()

    /**
     * Which streak day the next daily claim would be, or null if today's is already taken.
     * Claiming within 48h of the last claim continues the streak, otherwise it restarts.
     */
    fun nextDailyDay(lastClaimMs: Long, lastDay: Int, nowMs: Long): Int? {
        if (lastClaimMs <= 0L) return 1
        val since = nowMs - lastClaimMs
        return when {
            since < DAY_MS -> null
            since < 2 * DAY_MS -> if (lastDay >= DAILY_DAYS) 1 else lastDay + 1
            else -> 1
        }
    }

    // ------------------------------------------------------------ win tiers

    fun winTier(winCoins: Long, totalBet: Long): WinTier = when {
        winCoins <= 0 -> WinTier.NONE
        winCoins >= totalBet * 50 -> WinTier.HUGE
        winCoins >= totalBet * 25 -> WinTier.MEGA
        winCoins >= totalBet * 10 -> WinTier.BIG
        else -> WinTier.NORMAL
    }
}

/**
 * Progressive jackpots shown above every machine. A jackpot is worth [betMultiple] x total bet
 * plus its pool, which every paid spin feeds with [poolShare] of the bet. One roll per paid spin.
 */
enum class JackpotTier(val label: String, val betMultiple: Int, val odds: Int, val poolShare: Double) {
    MINI("MINI", 10, 250, 0.004),
    MINOR("MINOR", 40, 1_500, 0.003),
    MAJOR("MAJOR", 200, 10_000, 0.002),
    GRAND("GRAND", 1_000, 100_000, 0.001),
}

data class JackpotWin(val tier: JackpotTier, val coins: Long)
