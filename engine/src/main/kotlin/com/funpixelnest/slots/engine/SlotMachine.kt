package com.funpixelnest.slots.engine

import kotlin.random.Random

/** Visible symbols: grid[reel][row], row 0 = top. */
typealias Grid = List<List<Int>>

data class Cell(val reel: Int, val row: Int)

data class LineWin(
    /** Index into [SlotMachine.lines]. */
    val lineIndex: Int,
    /** Row per reel for this line. */
    val rows: List<Int>,
    /** Cells that form the winning combination (left-aligned, "any" positions excluded). */
    val cells: List<Cell>,
    val symbolId: Int,
    val count: Int,
    val pay: Int,
    val freeSpins: Int,
    val payMult: Int,
    val freeSpinsMult: Int,
)

data class ScatterWin(
    val cells: List<Cell>,
    val pay: Int,
    val freeSpins: Int,
    val payMult: Int,
    val freeSpinsMult: Int,
)

data class SpinOutcome(
    /** Strip index at the top row of each reel. */
    val stops: List<Int>,
    val grid: Grid,
    val lineWins: List<LineWin>,
    val scatterWin: ScatterWin?,
) {
    val linePay: Int get() = lineWins.sumOf { it.pay }
    val scatterPay: Int get() = scatterWin?.pay ?: 0
    val payMultiplier: Int get() = lineWins.fold(1) { a, w -> a * w.payMult } * (scatterWin?.payMult ?: 1)
    val freeSpinsMultiplier: Int get() =
        lineWins.fold(1) { a, w -> a * w.freeSpinsMult } * (scatterWin?.freeSpinsMult ?: 1)
    val freeSpins: Int get() = lineWins.sumOf { it.freeSpins } + (scatterWin?.freeSpins ?: 0)
    val isWin: Boolean get() = lineWins.isNotEmpty() || scatterWin != null

    /** Total pay in line-bet units, as in Unity's SlotController. */
    val payUnits: Long get() = (linePay + scatterPay).toLong() * payMultiplier

    /** Distinct cells taking part in any win, for highlighting. */
    val winningCells: Set<Cell> get() =
        (lineWins.flatMap { it.cells } + (scatterWin?.cells ?: emptyList())).toSet()
}

/**
 * Port of the Unity Holiday Slot win logic (SlotController / WinController / LineBehavior):
 * reels stop on random strip positions, every line takes the best matching pay line,
 * wilds substitute via an expanded pay table, and scatters pay on an exact count anywhere.
 */
class SlotMachine(val config: ThemeConfig) {
    val reelCount = config.reels
    val rowCount = config.rows

    /** Row index per reel for each line. */
    val lines: List<List<Int>> = when (config.mode) {
        "lines" -> config.lines
        else -> allRowCombinations(reelCount, rowCount)
    }

    /** Pay table including wild-substituted variants (Unity: payTableFull). */
    val fullPayTable: List<PayLineDef> = buildFullPayTable()

    /** Pay lines bucketed by their first symbol (-1 = "any"), so a line only checks plausible entries. */
    private val byFirstSymbol: Map<Int, List<PayLineDef>> = fullPayTable.groupBy { it.line.firstOrNull() ?: -1 }
    private val anyFirst: List<PayLineDef> = byFirstSymbol[-1].orEmpty()

    init {
        require(config.reelStrips.size == reelCount) { "${config.id}: expected $reelCount reel strips" }
        config.reelStrips.forEachIndexed { i, s ->
            require(s.isNotEmpty()) { "${config.id}: reel $i is empty" }
            require(s.all { it in config.symbols.indices }) { "${config.id}: reel $i has unknown symbol" }
        }
    }

    fun spin(random: Random = Random.Default): SpinOutcome {
        val stops = config.reelStrips.map { random.nextInt(it.size) }
        return evaluate(stops)
    }

    fun gridAt(stops: List<Int>): Grid = stops.mapIndexed { reel, stop ->
        val strip = config.reelStrips[reel]
        List(rowCount) { row -> strip[(stop + row) % strip.size] }
    }

    /** Evaluate a grid that features may have changed (e.g. extra wilds). */
    fun evaluateGrid(stops: List<Int>, grid: Grid): SpinOutcome =
        SpinOutcome(stops, grid, findLineWins(grid), findScatterWin(grid))

    fun evaluate(stops: List<Int>): SpinOutcome {
        val grid = gridAt(stops)
        return SpinOutcome(stops, grid, findLineWins(grid), findScatterWin(grid))
    }

    fun findLineWins(grid: Grid): List<LineWin> {
        val wins = ArrayList<LineWin>()
        lines.forEachIndexed { index, rows ->
            var best: PayLineDef? = null
            val first = grid[0][rows[0]]
            for (bucket in arrayOf(byFirstSymbol[first].orEmpty(), anyFirst)) for (p in bucket) {
                if (!matches(p, grid, rows)) continue
                val b = best
                if (b == null || b.pay < p.pay || (b.pay == p.pay && b.freeSpins < p.freeSpins)) best = p
            }
            best?.let { p ->
                val cells = rows.indices.filter { p.line.getOrElse(it) { -1 } >= 0 }.map { Cell(it, rows[it]) }
                val sym = p.line.firstOrNull { it >= 0 && it != config.wildId } ?: config.wildId
                wins += LineWin(index, rows, cells, sym, cells.size, p.pay, p.freeSpins, p.payMult, p.freeSpinsMult)
            }
        }
        return wins
    }

    fun findScatterWin(grid: Grid): ScatterWin? {
        if (config.scatterId < 0 || config.scatterPays.isEmpty()) return null
        val cells = ArrayList<Cell>()
        grid.forEachIndexed { reel, col -> col.forEachIndexed { row, s -> if (s == config.scatterId) cells += Cell(reel, row) } }
        val pay = config.scatterPays.lastOrNull { it.count > 0 && it.count == cells.size } ?: return null
        return ScatterWin(cells, pay.pay, pay.freeSpins, pay.payMult, pay.freeSpinsMult)
    }

    private fun matches(p: PayLineDef, grid: Grid, rows: List<Int>): Boolean {
        for (reel in 0 until reelCount) {
            val need = p.line.getOrElse(reel) { -1 }
            if (need >= 0 && grid[reel][rows[reel]] != need) return false
        }
        return true
    }

    /** Port of PayLine.GetWildLines: every non-empty subset of substitutable positions becomes wild. */
    private fun buildFullPayTable(): List<PayLineDef> {
        val out = ArrayList<PayLineDef>()
        val seen = HashSet<List<Int>>()
        for (base in config.payTable) {
            val line = List(reelCount) { base.line.getOrElse(it) { -1 } }
            val p = base.copy(line = line)
            out += p
            seen += line
            if (config.wildId < 0) continue
            val positions = ArrayList<Int>()
            var counter = 0
            for (i in line.indices) {
                val s = line[i]
                if (s == -1 || s == config.wildId) continue
                if (!config.wildInFirstPosition && counter == 0) { counter++; continue }
                if (config.canSubstitute(s)) positions += i
                counter++
            }
            val maxWilds = if (config.wildInFirstPosition) positions.size - 1 else positions.size
            for (mask in 1 until (1 shl positions.size)) {
                if (Integer.bitCount(mask) > maxWilds) continue
                val l = line.toMutableList()
                positions.forEachIndexed { bit, pos -> if (mask and (1 shl bit) != 0) l[pos] = config.wildId }
                if (seen.add(l)) out += p.copy(line = l)
            }
        }
        return out
    }

    companion object {
        fun allRowCombinations(reels: Int, rows: Int): List<List<Int>> {
            var acc = listOf(emptyList<Int>())
            repeat(reels) { acc = acc.flatMap { prefix -> (0 until rows).map { prefix + it } } }
            return acc
        }
    }
}
