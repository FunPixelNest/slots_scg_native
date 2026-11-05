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
    fun `every lobby entry is playable`() {
        var dir = File("").absoluteFile
        while (!File(dir, "themes").isDirectory) dir = dir.parentFile
        val index = ThemeConfig.json.decodeFromString(
            kotlinx.serialization.builtins.ListSerializer(ThemeIndexEntry.serializer()),
            File(dir, "app/src/main/assets/common/themes.json").readText(),
        )
        assertEquals(23, index.size)
        for (e in index) {
            val root = File(dir, "themes/theme_${e.id}/src/main/assets/themes/${e.id}")
            val cfg = ThemeConfig.parse(File(root, "theme.json").readText())
            assertTrue(File(dir, "app/src/main/assets/common/${e.tile}.webp").isFile, "${e.id}: lobby tile missing")
            for (s in cfg.symbols) assertTrue(File(root, "${s.image}.webp").isFile, "${e.id}: ${s.image} missing")
            cfg.background?.let { assertTrue(File(root, "$it.webp").isFile, "${e.id}: background missing") }
            val session = GameSession(SlotMachine(cfg), InMemoryPlayerStore())
            assertTrue(session.canSpin(), "${e.id}: cannot spin with starting coins")
            assertTrue(session.spin().isSuccess)
        }
    }

    @Test
    fun `session debits bet and credits wins and jackpots`() {
        val cfg = theme("christmas_eve")
        val store = InMemoryPlayerStore(coins = 10_000_000_000)
        val s = GameSession(SlotMachine(cfg), store, Random(7))
        s.setBetIndex(1)
        assertEquals(20, s.lineBet)
        assertEquals(4_860, s.totalBet)
        var expected = store.coins
        var jackpots = 0
        repeat(5_000) {
            val r = s.spin().getOrThrow()
            if (!r.wasFreeSpin) expected -= r.totalBet
            expected += r.totalWin + r.levelUpReward
            assertEquals(expected, store.coins)
            assertEquals(r.outcome.payUnits * r.totalBet / s.linesCount, r.winCoins)
            if (r.jackpot != null) {
                jackpots++
                assertTrue(r.jackpot!!.coins >= r.totalBet * r.jackpot!!.tier.betMultiple)
            }
        }
        assertTrue(jackpots > 0, "expected some jackpots in 5000 spins")
    }

    @Test
    fun `bet ladder unlocks with level`() {
        val store = InMemoryPlayerStore(level = 1)
        val s = GameSession(SlotMachine(theme("wolf")), store)
        s.maxBet()
        assertEquals(Economy.FREE_BET_STEPS - 1, s.betIndex)
        assertEquals(3, s.nextBetUnlockLevel)
        s.changeLineBet(+5)
        assertEquals(Economy.FREE_BET_STEPS - 1, s.betIndex) // locked steps stay locked
        store.level = 30
        s.maxBet()
        assertEquals(Economy.LINE_BETS.size - 1, s.betIndex)
        assertEquals(null, s.nextBetUnlockLevel)
        for (i in Economy.LINE_BETS.indices) {
            assertTrue(Economy.unlockedBetSteps(Economy.levelForBetStep(i)) > i, "step $i")
        }
    }

    @Test
    fun `machine unlocks by level and video`() {
        assertEquals(Unlock.Free, Unlocks.rule(0))
        assertEquals(Unlock.Free, Unlocks.rule(3))
        assertEquals(Unlock.AtLevel(2), Unlocks.rule(4))
        assertTrue(Unlocks.rule(6) is Unlock.WatchVideo)
        assertTrue(!Unlocks.isOpen(4, playerLevel = 1, videosWatched = 0))
        assertTrue(Unlocks.isOpen(4, playerLevel = 2, videosWatched = 0))
        assertTrue(!Unlocks.isOpen(6, playerLevel = 1, videosWatched = 0))
        assertTrue(Unlocks.isOpen(6, playerLevel = 1, videosWatched = 1))
        assertEquals(listOf(4), Unlocks.openedAtLevel(2, 23))
        // every machine eventually opens by level
        assertTrue((0 until 23).all { Unlocks.isOpen(it, playerLevel = 40, videosWatched = 0) })
        val kinds = (0 until 23).map { Unlocks.rule(it)::class.simpleName }
        println("unlocks: " + (0 until 23).joinToString { "$it=${Unlocks.rule(it)}" })
        assertTrue(kinds.count { it == "WatchVideo" } >= 5)
    }

    @Test
    fun `every spin earns xp`() {
        val store = InMemoryPlayerStore(coins = 10_000_000)
        val s = GameSession(SlotMachine(theme("wolf")), store, Random(3))
        repeat(20) {
            val r = s.spin().getOrThrow()
            assertTrue(r.xpGained >= 1)
        }
        assertTrue(store.level > 1 || store.levelProgress > 0f)
    }

    @Test
    fun `daily streak`() {
        val day = Economy.DAY_MS
        assertEquals(1, Economy.nextDailyDay(0, 0, 1000))
        assertEquals(null, Economy.nextDailyDay(1000, 1, 1000 + day - 1))
        assertEquals(2, Economy.nextDailyDay(1000, 1, 1000 + day))
        assertEquals(1, Economy.nextDailyDay(1000, 7, 1000 + day))
        assertEquals(1, Economy.nextDailyDay(1000, 3, 1000 + 2 * day))
        assertTrue(Economy.dailyBonus(7, 1) > Economy.dailyBonus(1, 1))
        assertTrue(Economy.dailyBonus(1, 10) > Economy.dailyBonus(1, 1))
    }

    @Test
    fun `not enough coins is refused`() {
        val s = GameSession(SlotMachine(theme("wolf")), InMemoryPlayerStore(coins = 10))
        assertTrue(s.spin().isFailure)
        assertEquals(10, s.coins)
    }

    /** Monte-Carlo return-to-player per theme incl. jackpots; printed for tuning, sanity-checked loosely. */
    @Test
    fun `rtp simulation`() {
        for (f in themeFiles()) {
            val cfg = ThemeConfig.parse(f.readText())
            val store = InMemoryPlayerStore(coins = Long.MAX_VALUE / 4, level = 1)
            val s = GameSession(SlotMachine(cfg), store, Random(42))
            val spins = 20_000
            var paid = 0L
            var bet = 0L
            var hits = 0
            var jp = 0
            repeat(spins) {
                val r = s.spin().getOrThrow()
                if (!r.wasFreeSpin) bet += r.totalBet
                paid += r.totalWin
                if (r.outcome.isWin) hits++
                if (r.jackpot != null) jp++
                store.level = 1 // keep the bet constant
            }
            val rtp = paid.toDouble() / bet * 100
            println("RTP %-16s %6.1f%%  hit %4.1f%%  jackpots %d".format(cfg.id, rtp, hits * 100.0 / spins, jp))
            assertTrue(rtp in 20.0..400.0, "${cfg.id}: RTP $rtp looks broken")
        }
    }
}
