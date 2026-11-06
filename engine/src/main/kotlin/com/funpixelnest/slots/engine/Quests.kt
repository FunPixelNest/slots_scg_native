package com.funpixelnest.slots.engine

/** What a task or mission counts. */
enum class QuestStat { SPINS, WINS, COINS_WON, BIG_WINS, MAX_BET_SPINS, BONUS_GAMES, MACHINES, LEVEL }

data class Quest(val id: String, val title: String, val stat: QuestStat, val target: Long, val reward: Long)

/**
 * Daily tasks reset every day (UTC midnight); missions are long-term goals counted over the
 * player's whole history. Each pays coins once (with the x2 video option).
 */
object Quests {
    val DAILY_TASKS = listOf(
        Quest("t_spin50", "Spin 50 times", QuestStat.SPINS, 50, 100_000),
        Quest("t_win15", "Win on 15 spins", QuestStat.WINS, 15, 150_000),
        Quest("t_coins500k", "Win 500K coins", QuestStat.COINS_WON, 500_000, 200_000),
        Quest("t_maxbet10", "Spin 10 times at Max bet", QuestStat.MAX_BET_SPINS, 10, 250_000),
        Quest("t_bigwin1", "Land a Big Win", QuestStat.BIG_WINS, 1, 300_000),
    )

    val MISSIONS = listOf(
        Quest("m_machines3", "Play 3 different machines", QuestStat.MACHINES, 3, 400_000),
        Quest("m_level5", "Reach level 5", QuestStat.LEVEL, 5, 500_000),
        Quest("m_spins500", "Spin 500 times", QuestStat.SPINS, 500, 1_000_000),
        Quest("m_bonus5", "Trigger 5 bonus games", QuestStat.BONUS_GAMES, 5, 1_000_000),
        Quest("m_level10", "Reach level 10", QuestStat.LEVEL, 10, 1_500_000),
        Quest("m_big10", "Land 10 Big Wins", QuestStat.BIG_WINS, 10, 2_000_000),
        Quest("m_machines10", "Play 10 different machines", QuestStat.MACHINES, 10, 2_500_000),
        Quest("m_coins50m", "Win 50M coins in total", QuestStat.COINS_WON, 50_000_000, 5_000_000),
    )

    const val DAY_MS = 86_400_000L
    fun day(now: Long): Long = now / DAY_MS
    fun msToReset(now: Long): Long = DAY_MS - now % DAY_MS
}
