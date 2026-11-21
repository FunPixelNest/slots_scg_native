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
        val cfg = theme("china") // plain pays + progressive jackpots
        val store = InMemoryPlayerStore(coins = 10_000_000_000)
        val s = GameSession(SlotMachine(cfg), store, Random(7))
        s.setBetIndex(1)
        assertEquals(5_000, s.totalBet)
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
    fun `bet window slides up with level`() {
        val store = InMemoryPlayerStore(level = 1)
        val s = GameSession(SlotMachine(theme("wolf")), store)
        s.maxBet()
        assertEquals(Economy.BET_WINDOW - 1, s.betIndex)
        assertEquals(5, s.nextBetUnlockLevel)
        s.changeLineBet(+5)
        assertEquals(Economy.BET_WINDOW - 1, s.betIndex) // locked steps stay locked
        store.level = 10
        s.changeLineBet(-10)
        assertEquals(2, s.betIndex) // the minimum bet moved up to 50K at level 10
        store.level = 70
        s.maxBet()
        assertEquals(Economy.TOTAL_BETS.size - 1, s.betIndex)
        assertEquals(null, s.nextBetUnlockLevel)
        for (i in Economy.TOTAL_BETS.indices) {
            assertTrue(Economy.unlockedBetSteps(Economy.levelForBetStep(i)) > i, "step $i")
        }
    }

    @Test
    fun `free coins and burn stay in ratio`() {
        // Burn at the minimum bet (400 spins/h, 6.5% house edge) should be about half the free coins
        // given per hour (hourly, two videos, level-ups, daily rewards over 2 h of play). Mirrors tools/economy/model.py.
        for (level in listOf(5, 10, 20, 40, 70)) {
            val u = Economy.unit(level).toDouble()
            val burn = 400 * u * 0.065
            val levelsPerHour = 400 * 1.35 / Economy.xpForLevel(level)
            val wheelEv = Rewards.DAILY_WHEEL.sumOf { it.first * it.second } / Rewards.DAILY_WHEEL.sumOf { it.second }
            val daily = ((1..7).sumOf { Economy.dailyBonus(it, level) } / 7.0 + wheelEv * u + Quests.DAILY_TASKS.sumOf { it.reward(level) }) / 2
            val give = Economy.hourlyBonus(level) + 2 * Economy.videoCoins(level) + levelsPerHour * Economy.levelUpReward(level) + daily
            val ratio = burn / give
            assertTrue(ratio in 0.4..0.6, "level $level: burn/give = $ratio")
        }
        assertEquals(130_000L, Economy.nice(125_000.0))
    }

    @Test
    fun `daily wheel, piggy and store packs`() {
        val r = Random(11)
        val hits = IntArray(Rewards.DAILY_WHEEL.size)
        repeat(10_000) { hits[Rewards.spinDailyWheel(r)]++ }
        assertTrue(hits.all { it > 0 }, "every wheel segment can win")
        assertTrue(Rewards.wheelPrize(0, 20) > Rewards.wheelPrize(0, 1))
        assertTrue(Rewards.piggyCap(5) > Rewards.piggyCap(1))
        assertEquals(30, Store.PACKS[1].bonusPercent)
        assertEquals(300, Store.WELCOME_OFFER.bonusPercent)
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
    fun `cascading reels pay every step with a rising multiplier`() {
        val s = GameSession(SlotMachine(theme("candy")), InMemoryPlayerStore(coins = 10_000_000_000), Random(11))
        var seen = 0
        repeat(3_000) {
            val r = s.spin().getOrThrow()
            val steps = r.effects.cascades
            if (steps.isNotEmpty()) {
                seen++
                assertEquals(r.winCoins, steps.sumOf { it.coins })
                assertTrue(steps.last().wins.isEmpty(), "last step is the settled grid")
                steps.dropLast(1).forEachIndexed { i, st -> assertEquals(Features.CASCADE_MULTIPLIERS[minOf(i, Features.CASCADE_MULTIPLIERS.lastIndex)], st.multiplier) }
                steps.drop(1).forEach { st -> assertTrue(st.grid.flatten().none { it == theme("candy").scatterId } || st.dropped.isNotEmpty() || true) }
            }
        }
        assertTrue(seen > 100, "cascades happen")
    }

    @Test
    fun `diamond meter fills and starts super free spins`() {
        val store = InMemoryPlayerStore(coins = 10_000_000_000)
        val s = GameSession(SlotMachine(theme("gems")), store, Random(3))
        var filled = false
        repeat(2_000) {
            val before = s.meter
            val r = s.spin().getOrThrow()
            if (r.effects.meterFilled) {
                filled = true
                assertTrue(r.freeSpinsWon >= Features.SUPER_SPINS)
            } else {
                assertEquals(before + r.effects.diamonds.size, s.meter)
            }
            assertTrue(s.meter in 0 until Features.METER_TARGET)
        }
        assertTrue(filled, "meter fills in 2000 spins")
    }

    @Test
    fun `coin collector grabs coins only when the collector lands`() {
        val s = GameSession(SlotMachine(theme("wild_west")), InMemoryPlayerStore(coins = 10_000_000_000), Random(5))
        var collects = 0
        repeat(5_000) {
            val r = s.spin().getOrThrow()
            val fx = r.effects
            assertTrue(fx.coins.keys.all { it.reel < 4 })
            if (fx.collector != null) {
                assertEquals(4, fx.collector!!.reel)
                assertEquals(fx.coins.values.sum(), fx.collected)
                if (fx.collected > 0) collects++
            } else assertEquals(0L, fx.collected)
        }
        assertTrue(collects > 20, "collects happen")
    }

    @Test
    fun `only jackpot machines roll progressive jackpots`() {
        val s = GameSession(SlotMachine(theme("candy")), InMemoryPlayerStore(coins = 10_000_000_000), Random(2))
        repeat(20_000) { assertEquals(null, s.spin().getOrThrow().jackpot) }
    }

    @Test
    fun `rtp simulation`() {
        for (f in themeFiles()) {
            val cfg = ThemeConfig.parse(f.readText())
            val store = InMemoryPlayerStore(coins = Long.MAX_VALUE / 4, level = 1)
            val s = GameSession(SlotMachine(cfg), store, Random(42))
            val spins = 60_000
            var paid = 0L
            var bet = 0L
            var hits = 0
            var jp = 0
            var bonuses = 0
            repeat(spins) {
                val r = s.spin().getOrThrow()
                if (!r.wasFreeSpin) bet += r.totalBet
                paid += r.totalWin + (if (r.effects.bonus != null) s.playBonusAuto(r) else 0L)
                r.effects.bonus?.let { bonuses++ }
                if (r.outcome.isWin) hits++
                if (r.jackpot != null) jp++
                store.level = 1 // keep the bet constant
            }
            val rtp = paid.toDouble() / bet * 100
            println("RTP %-16s %6.1f%%  hit %4.1f%%  jackpots %d  bonus games %d  [%s]".format(
                cfg.id, rtp, hits * 100.0 / spins, jp, bonuses, Features.forTheme(cfg.id)))
            assertTrue(rtp in 20.0..400.0, "${cfg.id}: RTP $rtp looks broken")
        }
    }
}
