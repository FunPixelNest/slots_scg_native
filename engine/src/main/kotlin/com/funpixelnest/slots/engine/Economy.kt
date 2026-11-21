package com.funpixelnest.slots.engine

import kotlin.math.roundToLong

/**
 * Social-casino coin economy: balance in millions, a bet ladder that unlocks with level,
 * hourly and daily-streak bonuses, level-up rewards and progressive jackpots.
 */
object Economy {
    /*
     * Rule: coins burned = half of the free coins given, for a player betting the minimum of their
     * level (higher bets burn more). Free rewards are counted in "units" = the player's current
     * minimum bet, so they grow with the bets instead of piling up. Model and per-level numbers:
     * tools/economy/model.py.
     */
    const val START_COINS = 1_000_000L

    /** Total bet per spin (all 243 ways); wins pay pay-units x total bet / ways. */
    val TOTAL_BETS = listOf(
        10_000, 20_000, 50_000, 100_000, 200_000,
        500_000, 1_000_000, 2_000_000, 5_000_000, 10_000_000, 20_000_000, 50_000_000, 100_000_000,
    )

    /** How many bet steps are open at once: from the level's minimum bet up. */
    const val BET_WINDOW = 5

    /** Levels at which the minimum bet moves up one step (the window slides with it). */
    val MIN_BET_LEVELS = listOf(1, 5, 10, 15, 20, 30, 40, 50, 70)

    /** Lowest bet step allowed at [level]. */
    fun minBetStep(level: Int): Int = (MIN_BET_LEVELS.count { level >= it } - 1).coerceAtLeast(0)

    /** Bet steps 0 until this are unlocked (steps below [minBetStep] are no longer offered). */
    fun unlockedBetSteps(level: Int): Int = (minBetStep(level) + BET_WINDOW).coerceAtMost(TOTAL_BETS.size)

    /** Level at which bet step [index] opens. */
    fun levelForBetStep(index: Int): Int =
        (1..1000).firstOrNull { unlockedBetSteps(it) > index } ?: 1000

    /** Reward unit: the minimum bet at [level]. */
    fun unit(level: Int): Long = TOTAL_BETS[minBetStep(level)].toLong()

    /** [units] of the level's minimum bet, rounded to two significant figures (125,000 -> 130,000). */
    fun units(level: Int, units: Double): Long = nice(unit(level) * units)

    fun nice(v: Double): Long {
        if (v < 100) return v.roundToLong()
        var m = 1L
        while (v / m >= 100) m *= 10
        return (v / m).roundToLong() * m
    }

    /**
     * XP needed to go from [level] to the next one. Levels 1-4 are quick (about 20 spins at the
     * lowest bet to reach level 5); from level 5 a level takes about [level] minutes at the minimum bet.
     */
    fun xpForLevel(level: Int): Float = when (level) {
        1 -> 3f
        2 -> 5f
        3 -> 8f
        4 -> 12f
        else -> 9f * level
    }

    /** Level-up gift: 2 units, growing to 0.3 x level units (levels get slower). */
    fun levelUpReward(newLevel: Int): Long = units(newLevel, maxOf(2.0, newLevel * 0.3))

    // ------------------------------------------------------------ timed bonuses

    const val HOUR_MS = 60 * 60 * 1000L
    const val DAY_MS = 24 * HOUR_MS
    const val HOURLY_INTERVAL_MS = HOUR_MS

    fun hourlyBonus(level: Int): Long = units(level, 6.0)

    /** Free coins for a rewarded video, at most once per [VIDEO_COINS_INTERVAL_MS]. */
    fun videoCoins(level: Int): Long = units(level, 7.0)
    const val VIDEO_COINS_INTERVAL_MS = 30 * 60 * 1000L

    private val DAILY_UNITS = listOf(5.0, 8.0, 10.0, 12.0, 15.0, 20.0, 30.0)
    const val DAILY_DAYS = 7

    /** Day 1..7 reward in units of the level's minimum bet. */
    fun dailyBonus(day: Int, level: Int): Long = units(level, DAILY_UNITS[(day - 1).coerceIn(0, DAILY_DAYS - 1)])

    /** Out-of-coins refill; free once per [REFILL_COOLDOWN_MS], otherwise only with a video. */
    fun refill(level: Int): Long = units(level, 20.0)
    const val REFILL_COOLDOWN_MS = 4 * HOUR_MS

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
    /** Coins given once on first launch, after the disclaimer (on top of START_COINS). */
    const val WELCOME_BONUS = 500_000L

    /** Watching a video doubles a reward ("Collect x2"). */
    const val VIDEO_MULTIPLIER = 2

    /** Daily wheel prizes (units of the level's minimum bet, weight). */
    val DAILY_WHEEL = listOf(
        3.0 to 22, 5.0 to 20, 4.0 to 20, 12.0 to 10,
        8.0 to 14, 25.0 to 6, 3.0 to 22, 50.0 to 2,
    )

    fun wheelPrize(index: Int, level: Int): Long = Economy.units(level, DAILY_WHEEL[index].first)
    fun spinDailyWheel(random: kotlin.random.Random): Int = random.weighted(DAILY_WHEEL.mapIndexed { i, p -> i to p.second })

    /** Piggy bank: every paid spin drops this share of the bet in, up to [piggyCap]. */
    const val PIGGY_SHARE = 0.03
    fun piggyCap(level: Int): Long = Economy.units(level, 100.0)
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

    /** Pack sizes above are for level 1; they grow with the minimum bet so paid coins keep their value. */
    private fun scaled(p: CoinPack, level: Int): CoinPack {
        val f = Economy.unit(level).toDouble() / Economy.TOTAL_BETS[0]
        return p.copy(coins = Economy.nice(p.coins * f), baseCoins = Economy.nice(p.baseCoins * f))
    }

    fun packs(level: Int): List<CoinPack> = PACKS.map { scaled(it, level) }
    fun welcomeOffer(level: Int): CoinPack = scaled(WELCOME_OFFER, level)
}
