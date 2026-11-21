package com.funpixelnest.slots.engine

/** What a task or mission counts. */
enum class QuestStat { SPINS, WINS, COINS_WON, BIG_WINS, MAX_BET_SPINS, BONUS_GAMES, MACHINES, LEVEL }

/** [rewardUnits]: reward in units of the player's minimum bet (see [Economy.units]). */
data class Quest(val id: String, val title: String, val stat: QuestStat, val target: Long, val rewardUnits: Double) {
    fun reward(level: Int): Long = Economy.units(level, rewardUnits)
}

/**
 * Daily tasks reset every day (UTC midnight); missions are long-term goals counted over the
 * player's whole history. Each pays 1-20 units (minimum bets) once (with the x2 video option).
 */
object Quests {
    val DAILY_TASKS = listOf(
        Quest("t_spin50", "Spin 50 times", QuestStat.SPINS, 50, 1.0),
        Quest("t_win15", "Win on 15 spins", QuestStat.WINS, 15, 1.0),
        Quest("t_coins500k", "Win 500K coins", QuestStat.COINS_WON, 500_000, 2.0),
        Quest("t_maxbet10", "Spin 10 times at Max bet", QuestStat.MAX_BET_SPINS, 10, 2.0),
        Quest("t_bigwin1", "Land a Big Win", QuestStat.BIG_WINS, 1, 3.0),
    )

    val MISSIONS = listOf(
        Quest("m_machines3", "Play 3 different machines", QuestStat.MACHINES, 3, 5.0),
        Quest("m_level5", "Reach level 5", QuestStat.LEVEL, 5, 6.0),
        Quest("m_spins500", "Spin 500 times", QuestStat.SPINS, 500, 8.0),
        Quest("m_bonus5", "Trigger 5 bonus games", QuestStat.BONUS_GAMES, 5, 10.0),
        Quest("m_level10", "Reach level 10", QuestStat.LEVEL, 10, 12.0),
        Quest("m_big10", "Land 10 Big Wins", QuestStat.BIG_WINS, 10, 15.0),
        Quest("m_machines10", "Play 10 different machines", QuestStat.MACHINES, 10, 18.0),
        Quest("m_coins50m", "Win 50M coins in total", QuestStat.COINS_WON, 50_000_000, 20.0),
    )

    const val DAY_MS = 86_400_000L
    fun day(now: Long): Long = now / DAY_MS
    fun msToReset(now: Long): Long = DAY_MS - now % DAY_MS
}
