package com.funpixelnest.slots.engine

import kotlin.math.roundToLong

/**
 * Social-casino coin economy: balance in millions, a bet ladder that unlocks with level,
 * hourly and daily-streak bonuses, level-up rewards and progressive jackpots.
 */
object Economy {
    const val START_COINS = 1_000_000L

    /** Total bet per spin (all 243 ways); wins pay pay-units x total bet / ways. */
    val TOTAL_BETS = listOf(
        2_500, 5_000, 10_000, 25_000, 50_000,
        100_000, 250_000, 500_000, 1_000_000, 2_500_000, 5_000_000, 10_000_000, 25_000_000,
    )

    /** Bet steps open at level 1; one more every [LEVELS_PER_BET_STEP] levels. */
    const val FREE_BET_STEPS = 5
    const val LEVELS_PER_BET_STEP = 2

    fun unlockedBetSteps(level: Int): Int =
        (FREE_BET_STEPS + (level - 1) / LEVELS_PER_BET_STEP).coerceIn(1, TOTAL_BETS.size)

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

/** How a machine is opened in the lobby. */
sealed interface Unlock {
    data object Free : Unlock
    /** Opens automatically when the player reaches [level]. */
    data class AtLevel(val level: Int) : Unlock
    /** Opens after the player watches [videos] rewarded videos (or reaches [level], whichever is first). */
    data class WatchVideo(val videos: Int, val level: Int) : Unlock
}

object Unlocks {
    /** The first machines in lobby order are always open. */
    const val FREE_MACHINES = 2

    /** Player levels that open the level-locked machines, in lobby order. */
    val UNLOCK_LEVELS = listOf(5, 10, 15, 20, 30, 40, 50, 60, 70, 80, 90, 100)

    /**
     * After the free machines, each block of 7 has 3 watch-video machines (1st, 4th, 6th) and 4
     * level machines that open at [UNLOCK_LEVELS] in turn. A video machine also opens by itself
     * at the next level milestone.
     */
    private val SCHEDULE: List<Unlock> = buildList {
        val levels = ArrayDeque(UNLOCK_LEVELS)
        var i = 0
        while (levels.isNotEmpty()) {
            if (i % 7 in setOf(0, 3, 5)) add(Unlock.WatchVideo(videos = 1, level = levels.first()))
            else add(Unlock.AtLevel(levels.removeFirst()))
            i++
        }
    }

    /** Lobby order -> unlock rule. */
    fun rule(order: Int): Unlock {
        if (order < FREE_MACHINES) return Unlock.Free
        return SCHEDULE.getOrElse(order - FREE_MACHINES) { Unlock.WatchVideo(videos = 1, level = UNLOCK_LEVELS.last()) }
    }

    fun isOpen(order: Int, playerLevel: Int, videosWatched: Int): Boolean = when (val r = rule(order)) {
        Unlock.Free -> true
        is Unlock.AtLevel -> playerLevel >= r.level
        is Unlock.WatchVideo -> videosWatched >= r.videos || playerLevel >= r.level
    }

    /** Machines (by lobby order) that open exactly when reaching [newLevel]. */
    fun openedAtLevel(newLevel: Int, count: Int): List<Int> = (0 until count).filter { o ->
        when (val r = rule(o)) {
            is Unlock.AtLevel -> r.level == newLevel
            is Unlock.WatchVideo -> r.level == newLevel
            Unlock.Free -> false
        }
    }
}

/** First-launch, daily-wheel, piggy-bank and store rules. */
object Rewards {
    /** Coins given once on first launch, after the disclaimer. */
    const val WELCOME_BONUS = 1_000_000L

    /** Watching a video doubles a reward ("Collect x2"). */
    const val VIDEO_MULTIPLIER = 2

    /** Daily wheel prizes (coins at level 1, weight); scaled by [wheelScale]. */
    val DAILY_WHEEL = listOf(
        50_000L to 22, 100_000L to 20, 75_000L to 20, 250_000L to 10,
        150_000L to 14, 500_000L to 6, 60_000L to 22, 1_000_000L to 2,
    )

    fun wheelScale(level: Int): Double = 1 + 0.05 * (level - 1)
    fun wheelPrize(index: Int, level: Int): Long = (DAILY_WHEEL[index].first * wheelScale(level)).toLong()
    fun spinDailyWheel(random: kotlin.random.Random): Int = random.weighted(DAILY_WHEEL.mapIndexed { i, p -> i to p.second })

    /** Piggy bank: every paid spin drops this share of the bet in, up to [piggyCap]. */
    const val PIGGY_SHARE = 0.03
    fun piggyCap(level: Int): Long = 2_000_000L + 500_000L * (level - 1)
    const val PIGGY_PRICE = "$2.99"
    /** Piggy can be broken once it holds at least this share of its cap. */
    const val PIGGY_MIN_FILL = 0.2
}

data class CoinPack(val id: String, val coins: Long, val baseCoins: Long, val price: String, val badge: String? = null) {
    val bonusPercent: Int get() = if (baseCoins <= 0) 0 else ((coins - baseCoins) * 100 / baseCoins).toInt()
}

object Store {
    val PACKS = listOf(
        CoinPack("coins_1", 1_000_000, 1_000_000, "$0.99"),
        CoinPack("coins_2", 2_600_000, 2_000_000, "$1.99"),
        CoinPack("coins_3", 7_500_000, 5_000_000, "$4.99", "MOST POPULAR"),
        CoinPack("coins_4", 18_000_000, 10_000_000, "$9.99"),
        CoinPack("coins_5", 50_000_000, 25_000_000, "$19.99", "BEST VALUE"),
        CoinPack("coins_6", 120_000_000, 50_000_000, "$39.99"),
    )
    val WELCOME_OFFER = CoinPack("welcome_offer", 10_000_000, 2_500_000, "$4.99", "WELCOME OFFER")
}
