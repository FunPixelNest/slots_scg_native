package com.funpixelnest.slots.engine

import kotlin.random.Random

/** Special features; each machine gets its own (see [Features.forTheme]). */
enum class Feature(val title: String, val blurb: String) {
    EXPANDING_WILD("Expanding Wilds", "Wilds on reels 2-5 can expand to fill their whole reel."),
    STICKY_WILDS("Sticky Wilds", "During free spins every wild that lands stays in place until the free games end."),
    MULTIPLIER("Win Multiplier", "Any win can be boosted by a random 2x, 3x, 5x or 10x multiplier."),
    RANDOM_WILDS("Random Wilds", "On some spins 2 to 5 extra wilds are dropped onto reels 2-5."),
    HOLD_AND_SPIN("Hold & Spin", "4+ scatters start Hold & Spin: coins lock, 3 respins, each new coin resets them. Fill all 15 spots for the GRAND."),
    BONUS_WHEEL("Bonus Wheel", "4+ scatters spin the Bonus Wheel for coins, multipliers or a jackpot."),
    PICK_BONUS("Pick Bonus", "4+ scatters open the Pick Bonus: pick 3 of 9 treasures to win what is inside."),
    ;

    val isBonusGame: Boolean get() = this == HOLD_AND_SPIN || this == BONUS_WHEEL || this == PICK_BONUS
}

object Features {
    private val byTheme: Map<String, Feature> = buildMap {
        listOf("china", "pirates", "most_wanted", "gold_mine").forEach { put(it, Feature.HOLD_AND_SPIN) }
        listOf("wolf", "viking", "witch").forEach { put(it, Feature.EXPANDING_WILD) }
        listOf("egypt", "egypt_2").forEach { put(it, Feature.STICKY_WILDS) }
        listOf("neon", "modern", "gems").forEach { put(it, Feature.MULTIPLIER) }
        listOf("christmas_eve", "valentines_day", "candy").forEach { put(it, Feature.RANDOM_WILDS) }
        listOf("greek", "india", "arabian").forEach { put(it, Feature.BONUS_WHEEL) }
        listOf("underwater", "tropical", "forest", "farm", "wild_west").forEach { put(it, Feature.PICK_BONUS) }
    }

    fun forTheme(id: String): Feature? = byTheme[id]

    /** Scatters needed to start a bonus game. */
    const val BONUS_SCATTERS = 4

    // ---- tuning (checked by the RTP test) ----
    const val RANDOM_WILDS_CHANCE = 0.08
    /** Wilds expand on this share of spins (they show on most spins, and a full wild reel is strong). */
    const val EXPANDING_CHANCE = 0.03
    const val MULTIPLIER_CHANCE = 0.12
    val MULTIPLIERS = listOf(2 to 50, 3 to 30, 5 to 15, 10 to 5) // value to weight
}

/** What the features changed on a spin, so the UI can show it. */
data class FeatureEffects(
    /** Cells turned wild by Random Wilds. */
    val randomWilds: Set<Cell> = emptySet(),
    /** Reels filled by an Expanding Wild. */
    val expandedReels: Set<Int> = emptySet(),
    /** Cells held wild by Sticky Wilds (free spins). */
    val stickyWilds: Set<Cell> = emptySet(),
    /** Win multiplier applied (1 = none). */
    val multiplier: Int = 1,
    /** Bonus game started by this spin, if any. */
    val bonus: Feature? = null,
)

/** A prize inside a bonus game: coins, or a jackpot tier worth its current value. */
data class Prize(val coins: Long, val jackpot: JackpotTier? = null) {
    val label: String get() = jackpot?.label ?: ""
}

internal fun <T> Random.weighted(items: List<Pair<T, Int>>): T {
    var r = nextInt(items.sumOf { it.second })
    for ((v, w) in items) {
        if (r < w) return v
        r -= w
    }
    return items.last().first
}

/**
 * Hold & Spin on a 5x3 board. Coins lock where they land; 3 respins, reset whenever a new coin
 * lands. Coins carry a multiple of the total bet or a Mini/Minor/Major jackpot; a full board adds
 * the GRAND.
 */
class HoldAndSpinGame(
    private val totalBet: Long,
    private val jackpotValue: (JackpotTier) -> Long,
    startCells: Collection<Cell>,
    private val random: Random,
    val reels: Int = 5,
    val rows: Int = 3,
) {
    val coins = LinkedHashMap<Cell, Prize>()
    var respinsLeft = 3
        private set
    var grandWon = false
        private set
    val isOver: Boolean get() = respinsLeft == 0 || coins.size == reels * rows

    init {
        startCells.forEach { coins[it] = randomPrize() }
    }

    /** One respin; returns the cells where new coins landed. */
    fun respin(): List<Cell> {
        check(!isOver)
        val landed = ArrayList<Cell>()
        for (r in 0 until reels) for (row in 0 until rows) {
            val c = Cell(r, row)
            if (c !in coins && random.nextDouble() < LAND_CHANCE) {
                coins[c] = randomPrize()
                landed += c
            }
        }
        respinsLeft = if (landed.isNotEmpty()) 3 else respinsLeft - 1
        if (coins.size == reels * rows) {
            grandWon = true
            respinsLeft = 0
        }
        return landed
    }

    /** Total won (call when [isOver]). */
    fun total(): Long = coins.values.sumOf { it.coins } + if (grandWon) jackpotValue(JackpotTier.GRAND) else 0L

    fun playToEnd(): Long {
        while (!isOver) respin()
        return total()
    }

    private fun randomPrize(): Prize {
        val pick = random.weighted(COIN_TABLE)
        return when (pick) {
            -1 -> Prize(jackpotValue(JackpotTier.MINI), JackpotTier.MINI)
            -2 -> Prize(jackpotValue(JackpotTier.MINOR), JackpotTier.MINOR)
            -3 -> Prize(jackpotValue(JackpotTier.MAJOR), JackpotTier.MAJOR)
            else -> Prize(totalBet * pick / 2)
        }
    }

    companion object {
        const val LAND_CHANCE = 0.045
        /** Half-bets (1 = 0.5x bet) to weight; negatives are jackpots. */
        private val COIN_TABLE = listOf(
            1 to 400, 2 to 300, 3 to 150, 4 to 80, 6 to 40, 10 to 20, -1 to 8, -2 to 2, -3 to 1,
        )
    }
}

/** Bonus wheel with 12 segments; the pointer lands on one by weight. */
class BonusWheel(private val totalBet: Long, private val jackpotValue: (JackpotTier) -> Long) {
    data class Segment(val betMultiple: Int, val jackpot: JackpotTier?, val weight: Int)

    val segments = listOf(
        Segment(3, null, 180), Segment(6, null, 160), Segment(0, JackpotTier.MINI, 30), Segment(4, null, 170),
        Segment(8, null, 100), Segment(0, JackpotTier.MINOR, 6), Segment(3, null, 180), Segment(15, null, 50),
        Segment(0, JackpotTier.MAJOR, 1), Segment(8, null, 120), Segment(20, null, 20), Segment(0, JackpotTier.GRAND, 1),
    )

    fun prizeOf(index: Int): Prize = segments[index].let { s ->
        if (s.jackpot != null) Prize(jackpotValue(s.jackpot), s.jackpot) else Prize(totalBet * s.betMultiple)
    }

    fun spin(random: Random): Int = random.weighted(segments.mapIndexed { i, s -> i to s.weight })
}

/** Pick 3 of 9 hidden treasures; prizes are dealt when the bonus starts. */
class PickBonus(totalBet: Long, jackpotValue: (JackpotTier) -> Long, random: Random) {
    val hidden: List<Prize> = List(9) {
        val halfBets = random.weighted(listOf(2 to 30, 4 to 30, 6 to 20, 10 to 12, 20 to 5, -1 to 3))
        if (halfBets < 0) Prize(jackpotValue(JackpotTier.MINI), JackpotTier.MINI) else Prize(totalBet * halfBets / 2)
    }
    val picked = LinkedHashSet<Int>()
    val isOver: Boolean get() = picked.size >= PICKS

    fun pick(index: Int): Prize? {
        if (isOver || index !in hidden.indices || !picked.add(index)) return null
        return hidden[index]
    }

    fun total(): Long = picked.sumOf { hidden[it].coins }

    companion object { const val PICKS = 3 }
}
