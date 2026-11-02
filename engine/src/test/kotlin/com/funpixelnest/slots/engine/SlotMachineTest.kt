package com.funpixelnest.slots.engine

import java.io.File
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SlotMachineTest {

    private fun themeFiles(): List<File> {
        var dir = File("").absoluteFile
        while (!File(dir, "themes").isDirectory) dir = dir.parentFile
        return File(dir, "themes").walkTopDown().filter { it.name == "theme.json" }.sortedBy { it.path }.toList()
    }

    private fun theme(id: String) = ThemeConfig.parse(themeFiles().first { it.parentFile.name == id }.readText())

    /** Small hand-made machine: symbols 0..3 regular, 4 wild, 5 scatter. */
    private val tiny = ThemeConfig(
        id = "tiny", name = "Tiny",
        symbols = List(6) { SymbolDef("s$it") },
        wildId = 4, scatterId = 5,
        reelStrips = List(5) { listOf(0, 1, 2, 3, 4, 5) },
        payTable = listOf(
            PayLineDef(listOf(0, 0, 0, -1, -1), 1),
            PayLineDef(listOf(0, 0, 0, 0, -1), 2),
            PayLineDef(listOf(0, 0, 0, 0, 0), 4),
            PayLineDef(listOf(4, 4, 4, 4, 4), 50),
        ),
        scatterPays = listOf(ScatterPayDef(count = 3, freeSpins = 5)),
    )

    private fun gridMachine(cfg: ThemeConfig, grid: Grid) = SlotMachine(cfg).let { m ->
        m.findLineWins(grid) to m.findScatterWin(grid)
    }

    @Test
    fun `243 ways lines`() {
        val m = SlotMachine(tiny)
        assertEquals(243, m.lines.size)
        assertEquals(243, m.lines.toSet().size)
    }

    @Test
    fun `three of a kind pays once per combination of the remaining rows`() {
        // reels 1-3 have one "0" on the middle row; reels 4-5 have no "0"
        val grid = listOf(listOf(1, 0, 2), listOf(1, 0, 2), listOf(3, 0, 2), listOf(1, 2, 3), listOf(1, 2, 3))
        val (wins, scatter) = gridMachine(tiny, grid)
        // middle row fixed on reels 1-3, any of 3 rows on reels 4 and 5 => 9 lines
        assertEquals(9, wins.size)
        assertTrue(wins.all { it.pay == 1 && it.count == 3 && it.symbolId == 0 })
        assertNull(scatter)
    }

    @Test
    fun `wild substitutes but not on the first reel`() {
        val wildFirst = listOf(listOf(4, 4, 4), listOf(0, 0, 0), listOf(0, 0, 0), listOf(1, 1, 1), listOf(1, 1, 1))
        assertTrue(gridMachine(tiny, wildFirst).first.isEmpty())

        val wildSecond = listOf(listOf(0, 1, 1), listOf(4, 1, 1), listOf(0, 1, 1), listOf(0, 1, 1), listOf(2, 2, 2))
        val wins = gridMachine(tiny, wildSecond).first
        // top row 0 W 0 0 -> 4 of a kind pays 2 (reel 5 any row => 3 lines);
        // leaving reel 4 through its "1" rows still makes 0 W 0 = 3 of a kind (2 x 3 = 6 lines)
        assertEquals(9, wins.size)
        assertEquals(3, wins.count { it.pay == 2 && it.count == 4 })
        assertEquals(6, wins.count { it.pay == 1 && it.count == 3 })
    }

    @Test
    fun `best pay line wins on a line`() {
        val grid = List(5) { listOf(0, 1, 2) }
        val wins = gridMachine(tiny, grid).first
        val top = wins.single { it.rows == listOf(0, 0, 0, 0, 0) }
        assertEquals(4, top.pay)
        assertEquals(5, top.count)
    }

    @Test
    fun `scatter pays on exact count anywhere`() {
        val grid = listOf(listOf(5, 1, 2), listOf(1, 5, 2), listOf(1, 2, 5), listOf(1, 2, 3), listOf(1, 2, 3))
        val s = assertNotNull(gridMachine(tiny, grid).second)
        assertEquals(3, s.cells.size)
        assertEquals(5, s.freeSpins)
        val four = grid.toMutableList().also { it[3] = listOf(5, 2, 3) }
        assertNull(gridMachine(tiny, four).second)
    }

    @Test
    fun `every imported theme loads and spins`() {
        val files = themeFiles()
        assertTrue(files.size >= 23, "expected all themes, found ${files.size}")
        for (f in files) {
            val cfg = ThemeConfig.parse(f.readText())
            val m = SlotMachine(cfg)
            assertTrue(cfg.symbols.all { it.image != null }, "${cfg.id}: symbol without image")
            assertTrue(cfg.background != null, "${cfg.id}: no background")
            repeat(200) { m.spin(Random(it)) }
        }
    }

    @Test
    fun `session debits bet and credits wins`() {
        val cfg = theme("christmas_eve")
        val store = InMemoryPlayerStore(coins = 1_000_000)
        val s = GameSession(SlotMachine(cfg), store, Random(7))
        s.setLineBet(2)
        assertEquals(486, s.totalBet)
        var expected = store.coins
        repeat(500) {
            val r = s.spin().getOrThrow()
            if (!r.wasFreeSpin) expected -= r.totalBet
            expected += r.winCoins + r.levelUpReward
            assertEquals(expected, store.coins)
            assertEquals(r.outcome.payUnits * 2, r.winCoins)
        }
    }

    @Test
    fun `not enough coins is refused`() {
        val s = GameSession(SlotMachine(theme("wolf")), InMemoryPlayerStore(coins = 10))
        assertTrue(s.spin().isFailure)
        assertEquals(10, s.coins)
    }

    /** Monte-Carlo return-to-player per theme; printed for tuning, sanity-checked loosely. */
    @Test
    fun `rtp simulation`() {
        for (f in themeFiles()) {
            val cfg = ThemeConfig.parse(f.readText())
            val m = SlotMachine(cfg)
            val rnd = Random(42)
            val spins = 20_000
            var paid = 0L
            var bet = 0L
            var free = 0
            var hits = 0
            repeat(spins) {
                if (free > 0) free-- else bet += m.lines.size
                val o = m.spin(rnd)
                paid += o.payUnits
                free += o.freeSpins * o.freeSpinsMultiplier
                if (o.isWin) hits++
            }
            val rtp = paid.toDouble() / bet * 100
            println("RTP %-16s %6.1f%%  hit %4.1f%%".format(cfg.id, rtp, hits * 100.0 / spins))
            assertTrue(rtp in 20.0..400.0, "${cfg.id}: RTP $rtp looks broken")
        }
    }
}
