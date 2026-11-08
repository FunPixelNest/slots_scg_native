package com.funpixelnest.slots

import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.core.app.ApplicationProvider
import com.funpixelnest.slots.data.AppGraph
import com.funpixelnest.slots.engine.JackpotTier
import com.funpixelnest.slots.engine.JackpotWin
import com.funpixelnest.slots.engine.SlotMachine
import com.funpixelnest.slots.engine.ThemeConfig
import com.funpixelnest.slots.engine.ThemeIndexEntry
import com.funpixelnest.slots.engine.WinTier
import com.funpixelnest.slots.ui.common.AssetImage
import com.funpixelnest.slots.ui.common.outlinedText
import com.funpixelnest.slots.ui.game.GameScreen
import com.funpixelnest.slots.ui.game.GameViewModel
import com.funpixelnest.slots.ui.game.Popup
import com.funpixelnest.slots.ui.lobby.LobbyScreen
import com.github.takahirom.roborazzi.captureRoboImage
import kotlinx.serialization.builtins.ListSerializer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import kotlin.random.Random

/** Portrait phone, 822x1782 px. */
private const val PHONE = "w411dp-h891dp-port-xhdpi"
private const val OUT = "build/screenshots"

private fun themeIndex(): List<ThemeIndexEntry> = ThemeConfig.json.decodeFromString(
    ListSerializer(ThemeIndexEntry.serializer()),
    File("src/main/assets/common/themes.json").readText(),
).sortedBy { it.order }

/** Stops that give a nice multi-line win, found deterministically with the real engine. */
private fun winningStops(cfg: ThemeConfig): List<Int> {
    val m = SlotMachine(cfg)
    val rnd = Random(2024)
    var best = m.spin(rnd)
    repeat(4000) {
        val o = m.spin(rnd)
        if (o.lineWins.size in 3..60 && o.payUnits > best.payUnits) best = o
    }
    return best.stops
}

abstract class ScreenshotBase {
    @get:Rule
    val compose = createComposeRule()

    protected val graph: AppGraph by lazy { AppGraph.get(ApplicationProvider.getApplicationContext()) }

    @Before
    fun freshGraph() {
        AppGraph.resetForTests()
        compose.mainClock.autoAdvance = false
        graph.player.coins = 12_500_000 // a typical balance for the screenshots
    }

    /** Let frames, coroutines and image decoding (IO threads) progress for [ms] of virtual time. */
    protected fun settle(ms: Long) {
        var t = 0L
        while (t < ms) {
            compose.mainClock.advanceTimeBy(50)
            shadowOf(Looper.getMainLooper()).idle()
            Thread.sleep(15)
            t += 50
        }
    }

    protected fun waitForTag(tag: String, maxMs: Long = 60_000) {
        val end = System.currentTimeMillis() + maxMs
        while (compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isEmpty()) {
            check(System.currentTimeMillis() < end) { "timed out waiting for $tag" }
            settle(100)
        }
    }

    /** Same layout as MainActivity: the screen above, the banner-ad safe area below. */
    protected fun setScreen(content: @androidx.compose.runtime.Composable () -> Unit) = compose.setContent {
        androidx.compose.foundation.layout.Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) { content() }
            com.funpixelnest.slots.ui.common.AdBannerSlot()
        }
    }

    protected fun shot(name: String) {
        File(OUT).mkdirs()
        compose.onRoot().captureRoboImage("$OUT/$name.png")
    }
}

/** Every slot machine, reels stopped on a real winning spin with its wins highlighted. */
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = PHONE)
class MachineScreenshotTest(private val order: Int, private val themeId: String) : ScreenshotBase() {

    @Test
    fun machine() {
        val stops = winningStops(graph.theme(themeId)!!.config)
        setScreen {
            GameScreen(graph, themeId, onExit = {}, onReady = { vm -> vm.previewShowcase(stops) })
        }
        waitForTag("game-ready")
        settle(2500)
        shot("machine_%02d_%s".format(order + 1, themeId))
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{1}")
        fun themes(): List<Array<Any>> = themeIndex().map { arrayOf(it.order, it.id) }
    }
}

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = PHONE)
class LobbyScreenshotTest : ScreenshotBase() {

    @Test
    fun lobby() {
        graph.player.claimDaily(System.currentTimeMillis()) // daily already taken -> no popup
        graph.player.coins = 12_500_000
        setScreen { LobbyScreen(graph, onOpenTheme = {}, skipStartup = true) }
        waitForTag("lobby")
        settle(3000)
        shot("lobby_01_main")
    }

    @Test
    fun dailyBonus() {
        val now = System.currentTimeMillis()
        graph.player.lastDailyAt = now - 30 * 60 * 60 * 1000L // claimed yesterday...
        graph.player.lastDailyDay = 3                       // ...on day 3 -> today is day 4
        setScreen { LobbyScreen(graph, onOpenTheme = {}, startWithDailyBonus = true) }
        waitForTag("daily-bonus")
        settle(2500)
        shot("flow_03_daily_bonus")
    }

    @Test
    fun dailyTasks() {
        graph.player.claimDaily(System.currentTimeMillis())
        graph.player.level = 6
        graph.player.track(com.funpixelnest.slots.engine.QuestStat.SPINS, 50)
        graph.player.track(com.funpixelnest.slots.engine.QuestStat.WINS, 9)
        graph.player.track(com.funpixelnest.slots.engine.QuestStat.COINS_WON, 180_000)
        setScreen { LobbyScreen(graph, onOpenTheme = {}, startWithQuests = "tasks") }
        waitForTag("tasks")
        settle(1500)
        shot("lobby_04_tasks")
    }

    @Test
    fun missions() {
        graph.player.claimDaily(System.currentTimeMillis())
        graph.player.level = 6
        listOf("china", "pirates", "egypt").forEach { graph.player.trackMachinePlayed(it) }
        graph.player.track(com.funpixelnest.slots.engine.QuestStat.SPINS, 220)
        setScreen { LobbyScreen(graph, onOpenTheme = {}, startWithQuests = "missions") }
        waitForTag("missions")
        settle(1500)
        shot("lobby_07_missions")
    }

    @Test
    fun lockedVideoMachine() {
        graph.player.claimDaily(System.currentTimeMillis())
        val videoMachine = graph.themes.first { com.funpixelnest.slots.engine.Unlocks.rule(it.order) is com.funpixelnest.slots.engine.Unlock.WatchVideo }
        setScreen { LobbyScreen(graph, onOpenTheme = {}, startWithLocked = videoMachine.id) }
        waitForTag("lobby")
        settle(3000)
        shot("lobby_05_locked_watch_video")
    }

    @Test
    fun rewardedVideo() {
        graph.ads.show("free_coins") {}
        compose.setContent { com.funpixelnest.slots.ui.common.RewardedVideoOverlay(graph.ads) }
        settle(1500)
        shot("lobby_06_rewarded_video")
    }

    @Test
    fun disclaimer() {
        setScreen { com.funpixelnest.slots.ui.flow.DisclaimerScreen {} }
        waitForTag("disclaimer"); settle(1500)
        shot("flow_01_disclaimer")
    }

    @Test
    fun welcomeBonus() {
        // fresh player: welcome bonus is the first startup popup
        setScreen { LobbyScreen(graph, onOpenTheme = {}) }
        waitForTag("reward-claim"); settle(2000)
        shot("flow_02_welcome_bonus")
    }

    @Test
    fun dailyWheel() {
        setScreen { com.funpixelnest.slots.ui.flow.DailyWheelDialog(graph) {} }
        waitForTag("daily-wheel"); settle(1500)
        shot("flow_04_daily_wheel")
    }

    @Test
    fun store() {
        graph.player.coins = 12_500_000
        setScreen { com.funpixelnest.slots.ui.flow.StoreSheet(graph) {} }
        waitForTag("store"); settle(2000)
        shot("flow_06_store")
    }

    @Test
    fun piggyBank() {
        graph.player.piggy = 1_350_000
        setScreen { com.funpixelnest.slots.ui.flow.PiggyBankDialog(graph) {} }
        waitForTag("piggy-bank"); settle(1500)
        shot("flow_07_piggy_bank")
    }

    /** All 23 lobby tiles at once, to check every machine has its card art. */
    @Test
    fun allTiles() {
        val themes = graph.themes
        compose.setContent {
            LazyVerticalGrid(
                GridCells.Fixed(5),
                Modifier.fillMaxSize().background(Color(0xFF1A0533)).padding(6.dp),
            ) {
                items(themes) { e ->
                    Box(Modifier.padding(3.dp).aspectRatio(600f / 504f).clip(RoundedCornerShape(8.dp))) {
                        AssetImage(graph.commonImages, e.tile, Modifier.fillMaxSize(), ContentScale.Crop)
                    }
                }
            }
        }
        settle(4000)
        shot("lobby_03_all_tiles")
    }
}

/** Popups and the coin economy on one machine. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = PHONE)
class PopupScreenshotTest : ScreenshotBase() {

    private fun machineWith(themeId: String, name: String, settleMs: Long = 1200, action: (GameViewModel, List<Int>) -> Unit) {
        val stops = winningStops(graph.theme(themeId)!!.config)
        setScreen { GameScreen(graph, themeId, onExit = {}, onReady = { vm -> action(vm, stops) }) }
        waitForTag("game-ready")
        settle(settleMs)
        shot(name)
    }

    @Test
    fun paytable() = machineWith("christmas_eve", "popup_01_paytable") { vm, _ -> vm.showPopup(Popup.Info) }

    @Test
    fun bigWin() = machineWith("wolf", "popup_02_big_win", 900) { vm, stops ->
        vm.preview(stops, Popup.BigWin(WinTier.MEGA, 1_284_500))
    }

    @Test
    fun jackpot() = machineWith("egypt", "popup_03_grand_jackpot", 900) { vm, stops ->
        vm.preview(stops, Popup.Jackpot(JackpotWin(JackpotTier.GRAND, 25_431_870)))
    }

    @Test
    fun levelUp() = machineWith("pirates", "popup_04_level_up") { vm, stops ->
        vm.preview(stops, Popup.LevelUp(5, 250_000, betUnlocked = true, machines = listOf("Wolf Moon")))
    }

    @Test
    fun holdAndSpin() = machineWith("china", "popup_07_hold_and_spin", 900) { vm, stops ->
        vm.previewBonus(com.funpixelnest.slots.engine.Feature.HOLD_AND_SPIN, stops)
    }

    @Test
    fun bonusWheel() = machineWith("greek", "popup_08_bonus_wheel", 900) { vm, stops ->
        vm.previewBonus(com.funpixelnest.slots.engine.Feature.BONUS_WHEEL, stops)
    }

    @Test
    fun pickBonus() = machineWith("farm", "popup_09_pick_bonus", 900) { vm, stops ->
        vm.previewBonus(com.funpixelnest.slots.engine.Feature.PICK_BONUS, stops)
    }

    @Test
    fun freeSpinsDone() = machineWith("egypt_2", "popup_10_free_spins_done", 1200) { vm, stops ->
        vm.preview(stops, Popup.FreeSpinsDone(3_480_000))
    }

    @Test
    fun bonusWinClaim() = machineWith("greek", "popup_11_bonus_win_x2", 1500) { vm, stops ->
        vm.preview(stops, Popup.BonusWin("Bonus Wheel", 365_000))
    }

    @Test
    fun settings() = machineWith("valentines_day", "popup_05_settings") { vm, _ -> vm.showPopup(Popup.Settings) }

    @Test
    fun noCoins() = machineWith("candy", "popup_06_no_coins") { vm, _ -> vm.showPopup(Popup.NoCoins) }
}
