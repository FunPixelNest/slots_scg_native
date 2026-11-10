package com.funpixelnest.slots.ui.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funpixelnest.slots.audio.Sfx
import com.funpixelnest.slots.data.AppGraph
import com.funpixelnest.slots.data.ThemeAssets
import com.funpixelnest.slots.engine.Economy
import com.funpixelnest.slots.engine.GameSession
import com.funpixelnest.slots.engine.JackpotWin
import com.funpixelnest.slots.engine.SlotMachine
import com.funpixelnest.slots.engine.SpinResult
import com.funpixelnest.slots.engine.BonusWheel
import com.funpixelnest.slots.engine.Feature
import com.funpixelnest.slots.engine.HoldAndSpinGame
import com.funpixelnest.slots.engine.JackpotTier
import com.funpixelnest.slots.engine.PickBonus
import com.funpixelnest.slots.engine.Unlocks
import com.funpixelnest.slots.engine.WinTier
import com.funpixelnest.slots.engine.QuestStat
import com.funpixelnest.slots.engine.Cell
import com.funpixelnest.slots.engine.FeatureEffects
import com.funpixelnest.slots.engine.Grid
import com.funpixelnest.slots.ui.common.formatCompact
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class Phase { Loading, Idle, Spinning, ShowingWin }

sealed interface Popup {
    data class BigWin(val tier: WinTier, val coins: Long) : Popup
    data class Jackpot(val win: JackpotWin) : Popup
    data class FreeSpins(val count: Int) : Popup
    data class LevelUp(val level: Int, val reward: Long, val betUnlocked: Boolean, val machines: List<String> = emptyList()) : Popup
    data object Info : Popup
    data object Settings : Popup
    data object NoCoins : Popup
    /** Bonus game paid out (offers x2 video). */
    data class BonusWin(val title: String, val coins: Long) : Popup
    /** Free games finished: total won during them. */
    data class FreeSpinsDone(val coins: Long) : Popup
}

/** Bonus game shown over the machine. */
sealed interface BonusUi {
    data class HoldSpin(val game: HoldAndSpinGame) : BonusUi
    data class Wheel(val wheel: BonusWheel) : BonusUi
    data class Pick(val pick: PickBonus) : BonusUi
}

/** A spin the reels should animate to; [nonce] makes repeated identical stops distinct. */
data class SpinRequest(val stops: List<Int>, val nonce: Int, val instant: Boolean = false)

/** Port of SlotControls + the SlotController state machine (idle -> spin -> win/lose -> idle). */
class GameViewModel(private val graph: AppGraph, val assets: ThemeAssets) : ViewModel() {
    val machine = SlotMachine(assets.config)
    val session = GameSession(machine, graph.player)

    var phase by mutableStateOf(Phase.Loading); private set
    var loadProgress by mutableStateOf(0f); private set
    var popup by mutableStateOf<Popup?>(null); private set
    var spinRequest by mutableStateOf<SpinRequest?>(null); private set
    var result by mutableStateOf<SpinResult?>(null); private set

    /** Balance shown in the HUD (lags the real balance until the win is presented). */
    var shownCoins by mutableLongStateOf(session.coins); private set
    var shownWin by mutableLongStateOf(0L); private set
    var betIndex by mutableIntStateOf(session.betIndex); private set
    var freeSpins by mutableIntStateOf(0); private set

    // ---- feature state shown above / on the reels
    /** Cascading Reels: grid being shown for the current cascade step (null = normal reels). */
    var cascadeGrid by mutableStateOf<Grid?>(null); private set
    var cascadeWins by mutableStateOf<Set<Cell>>(emptySet()); private set
    var cascadeDropped by mutableStateOf<Set<Cell>>(emptySet()); private set
    var cascadeTick by mutableIntStateOf(0); private set
    var cascadeMultiplier by mutableIntStateOf(1); private set
    /** Diamond Collection meter (0 until the target). */
    var meter by mutableIntStateOf(session.meter); private set
    /** Free Spins Multiplier / Super Free Spins: multiplier of the next free spin. */
    var freeMultiplier by mutableIntStateOf(1); private set
    /** Coin Collector: coins grabbed on the last collect. */
    var lastCollected by mutableLongStateOf(0L); private set
    var autoSpinsLeft by mutableIntStateOf(0); private set
    var level by mutableIntStateOf(session.level); private set
    var levelProgress by mutableStateOf(session.levelProgress); private set
    var jackpots by mutableStateOf(readJackpots()); private set

    /** Banner flashed over the reels when a feature fires ("RANDOM WILDS!", "x5 MULTIPLIER"). */
    var featureBanner by mutableStateOf<String?>(null); private set
    /** Bonus game in progress (Hold & Spin, wheel or pick), drawn over the machine. */
    var bonus by mutableStateOf<BonusUi?>(null); private set
    val feature: Feature? get() = session.feature

    /** XP from the last spin; [xpTick] increments each spin to replay the "+XP" float. */
    var xpGained by mutableIntStateOf(0); private set
    var xpTick by mutableIntStateOf(0); private set

    /** Increments to launch a coin shower; [showerCoins] sets its size. */
    var showerTrigger by mutableIntStateOf(0); private set
    var showerCoins by mutableIntStateOf(0); private set

    /** Reads [betIndex] (state) so the deck redraws the moment -/+ is pressed. */
    val totalBet: Long get() = Economy.TOTAL_BETS[betIndex].toLong()
    val isMaxBet: Boolean get() = betIndex == session.unlockedSteps - 1
    val nextBetUnlockLevel: Int? get() = session.nextBetUnlockLevel
    val initialStops: List<Int> = assets.config.reelStrips.map { 0 }

    private var nonce = 0
    private var presentJob: Job? = null

    init {
        graph.player.trackMachinePlayed(assets.config.id)
        viewModelScope.launch {
            assets.preload { loadProgress = it }
            phase = Phase.Idle
        }
    }

    /**
     * After every spin: if the balance can no longer cover the bet, drop to the highest bet it
     * can cover (with a short notice) instead of stopping at "not enough coins".
     */
    private suspend fun fitBetToBalance() {
        if (session.freeSpins > 0 || session.coins >= session.totalBet) return
        val affordable = Economy.TOTAL_BETS.indices.lastOrNull { it < session.betIndex && Economy.TOTAL_BETS[it] <= session.coins } ?: return
        session.setBetIndex(affordable)
        betIndex = session.betIndex
        jackpots = readJackpots()
        while (popup != null) delay(200)
        featureBanner = "BET ${formatCompact(session.totalBet)}"
        delay(1100)
        featureBanner = null
    }

    private fun readJackpots(): Map<JackpotTier, Long> = JackpotTier.entries.associateWith { session.jackpotValue(it) }

    fun changeBet(delta: Int) = adjust { session.changeLineBet(delta) }
    fun maxBet() = adjust { session.maxBet() }

    private fun adjust(block: () -> Unit) {
        if ((phase != Phase.Idle && phase != Phase.ShowingWin) || freeSpins > 0) return
        block()
        betIndex = session.betIndex
        jackpots = readJackpots()
        graph.sound.play(Sfx.Button)
    }

    fun onSpinClick() {
        if (phase == Phase.Idle || phase == Phase.ShowingWin) spin()
    }

    fun toggleAuto() {
        graph.sound.play(Sfx.Button)
        if (autoSpinsLeft > 0) { autoSpinsLeft = 0; return }
        autoSpinsLeft = AUTO_SPINS
        if (phase == Phase.Idle || phase == Phase.ShowingWin) spin()
    }

    private fun spin() {
        presentJob?.cancel()
        val r = session.spin().getOrElse {
            autoSpinsLeft = 0
            popup = Popup.NoCoins
            return
        }
        if (autoSpinsLeft > 0 && !r.wasFreeSpin) autoSpinsLeft--
        if (!r.wasFreeSpin) graph.player.feedPiggy(r.totalBet) else freeGamesWon += r.totalWin
        trackQuests(r)
        result = r
        shownWin = 0
        cascadeGrid = null
        cascadeMultiplier = 1
        shownCoins = session.coins - r.totalWin - r.levelUpReward
        freeSpins = session.freeSpins - r.freeSpinsWon
        jackpots = readJackpots()
        phase = Phase.Spinning
        spinRequest = SpinRequest(r.outcome.stops, ++nonce)
        graph.sound.play(Sfx.Spin, 0.7f)
        // XP shows up right away; a level-up is presented after the reels stop
        xpGained = r.xpGained
        xpTick++
        if (r.levelUp == null) levelProgress = session.levelProgress
    }

    /** Daily tasks and missions progress from one spin. */
    private fun trackQuests(r: SpinResult) {
        val p = graph.player
        p.track(QuestStat.SPINS)
        p.recordSpin(assets.config.id, r.totalWin, r.jackpot != null)
        if (!r.wasFreeSpin && isMaxBet) p.track(QuestStat.MAX_BET_SPINS)
        if (r.totalWin > 0) { p.track(QuestStat.WINS); p.track(QuestStat.COINS_WON, r.totalWin) }
        if (r.tier >= WinTier.BIG) p.track(QuestStat.BIG_WINS)
    }

    /** Called by the reels when the last reel has settled. */
    fun onReelsStopped() {
        val r = result ?: return
        phase = Phase.ShowingWin
        presentJob = viewModelScope.launch {
            val fx = r.effects
            meter = session.meter
            if (fx.collected > 0) lastCollected = fx.collected
            // cascading reels: show every step, wins burst and new symbols drop in
            if (fx.cascades.isNotEmpty()) {
                var acc = 0L
                fx.cascades.forEachIndexed { i, st ->
                    cascadeGrid = st.grid; cascadeWins = st.wins; cascadeDropped = st.dropped
                    cascadeMultiplier = st.multiplier
                    cascadeTick++
                    if (st.wins.isNotEmpty()) {
                        if (i > 0) { graph.sound.play(Sfx.WinCoins); featureBanner = "CASCADE x${st.multiplier}" }
                        acc += st.coins
                        shownWin = acc
                        delay(1000)
                        featureBanner = null
                    } else delay(450)
                }
            }
            featureBannerFor(r)?.let { text ->
                graph.sound.play(Sfx.Bonus, 0.8f)
                featureBanner = text
                delay(1300)
                featureBanner = null
            }
            if (r.winCoins > 0) {
                val big = r.tier >= WinTier.BIG
                graph.sound.play(if (big) Sfx.BigWin else Sfx.WinCoins)
                shower(if (big) 70 else 18)
                if (big) popup = Popup.BigWin(r.tier, r.winCoins)
                if (fx.cascades.isEmpty()) countUp(r.winCoins) else shownWin = r.winCoins
            } else if (!r.outcome.isWin) {
                graph.sound.play(Sfx.Lose, 0.5f)
            }
            r.jackpot?.let { jp ->
                while (popup != null) delay(200)
                graph.sound.play(Sfx.BigWin)
                shower(110)
                popup = Popup.Jackpot(jp)
                countUp(r.totalWin)
                jackpots = readJackpots()
            }
            r.effects.bonus?.let { f ->
                while (popup != null) delay(200)
                graph.sound.play(Sfx.Bonus)
                featureBanner = "${f.title.uppercase()}!"
                delay(1400)
                featureBanner = null
                bonus = newBonus(f, r)
                while (bonus != null) delay(200)
            }
            if (r.wasFreeSpin && session.freeSpins == 0 && r.freeSpinsWon == 0) {
                while (popup != null) delay(200)
                popup = Popup.FreeSpinsDone(freeGamesWon)
                freeGamesWon = 0
            }
            if (r.freeSpinsWon > 0) {
                while (popup != null) delay(200)
                graph.sound.play(Sfx.Bonus)
                popup = Popup.FreeSpins(r.freeSpinsWon)
            }
            freeSpins = session.freeSpins
            freeMultiplier = session.nextFreeMultiplier
            level = session.level
            levelProgress = session.levelProgress
            r.levelUp?.let {
                while (popup != null) delay(200)
                val unlocked = Economy.unlockedBetSteps(it) > Economy.unlockedBetSteps(it - 1)
                val machines = Unlocks.openedAtLevel(it, graph.themes.size).mapNotNull { o -> graph.themes.getOrNull(o)?.name }
                graph.haptics.play(com.funpixelnest.slots.audio.Haptic.Success)
                popup = Popup.LevelUp(it, r.levelUpReward, unlocked, machines)
                shower(40)
            }
            shownCoins = session.coins
            fitBetToBalance()
            // keep going for free games and auto spins
            if (session.freeSpins > 0 || autoSpinsLeft > 0) {
                delay(if (r.outcome.isWin) 1600 else 500)
                while (popup != null || bonus != null) delay(200)
                spin()
            }
        }
    }

    private fun featureBannerFor(r: SpinResult): String? = with(r.effects) {
        when {
            multiplier > 1 -> "x$multiplier MULTIPLIER!"
            meterFilled -> "DIAMOND METER FULL!"
            collected > 0 -> "COIN COLLECT!"
            freeMultiplier > 1 && r.winCoins > 0 -> "x$freeMultiplier MULTIPLIER!"
            randomWilds.isNotEmpty() -> "RANDOM WILDS!"
            expandedReels.isNotEmpty() -> "EXPANDING WILDS!"
            stickyWilds.isNotEmpty() && r.wasFreeSpin -> "STICKY WILDS x${stickyWilds.size}"
            else -> null
        }
    }

    private fun newBonus(f: Feature, r: SpinResult): BonusUi? = when (f) {
        Feature.HOLD_AND_SPIN -> BonusUi.HoldSpin(session.newHoldAndSpin(r))
        Feature.BONUS_WHEEL -> BonusUi.Wheel(session.newWheel(r))
        Feature.PICK_BONUS -> BonusUi.Pick(session.newPick(r))
        else -> null
    }

    /** Called by the bonus screen when the player collects. */
    fun finishBonus(total: Long, jackpots: List<JackpotTier>) {
        bonus = null
        graph.player.track(QuestStat.BONUS_GAMES)
        graph.player.track(QuestStat.COINS_WON, total)
        session.awardBonus(total, jackpots)
        this.jackpots = readJackpots()
        shownCoins = session.coins
        if (total > 0) {
            graph.sound.play(Sfx.BigWin)
            shower(90)
            popup = Popup.BonusWin(feature?.title ?: "Bonus", total)
        }
    }

    /** Screenshot hook: open a bonus game straight away. */
    fun previewBonus(f: Feature, stops: List<Int>) {
        preview(stops)
        val fake = result ?: return
        bonus = newBonus(f, fake.copy(baseGrid = listOf(listOf(1, 1, 1), listOf(1, 1, 1), listOf(1, 1, 1), listOf(1, 1, 1), listOf(1, 1, 1))))
            ?.let { b -> if (b is BonusUi.HoldSpin) BonusUi.HoldSpin(session.previewHoldAndSpin(totalBet)) else b }
    }

    private fun shower(coins: Int) {
        showerCoins = coins
        showerTrigger++
    }

    private suspend fun countUp(target: Long) {
        val from = shownWin
        val steps = 30
        for (i in 1..steps) {
            shownWin = from + (target - from) * i / steps
            delay(25)
        }
    }

    fun showPopup(p: Popup) { graph.sound.play(Sfx.Button); popup = p }

    /** Balance changed outside a spin (x2 video, refill, store). */
    fun syncCoins() { shownCoins = session.coins }

    private var freeGamesWon = 0L
    fun dismissPopup() { popup = null }

    fun addCoins(amount: Long) {
        session.addCoins(amount)
        shownCoins = session.coins
        shower(50)
    }

    /**
     * Screenshot/preview hook: show the reels stopped on [stops] with their wins highlighted,
     * without spending coins or animating.
     */
    fun preview(stops: List<Int>, showPopup: Popup? = null) {
        val outcome = machine.evaluate(stops)
        val win = session.coinsFor(outcome.payUnits)
        result = SpinResult(
            outcome, totalBet, false, win, 0,
            Economy.winTier(win, totalBet), null, null, 0,
        )
        spinRequest = SpinRequest(stops, ++nonce, instant = true)
        shownWin = win
        phase = Phase.ShowingWin
        popup = showPopup
        if (showPopup is Popup.BigWin || showPopup is Popup.Jackpot) shower(90)
    }

    /**
     * Screenshot hook: like [preview], plus a typical moment of this machine's feature
     * (coins and collector, diamonds, a cascade step, a free-spin multiplier).
     */
    fun previewShowcase(stops: List<Int>) {
        preview(stops)
        val r = result ?: return
        val b = totalBet
        when (feature) {
            Feature.COIN_COLLECTOR -> {
                val coins = mapOf(Cell(0, 2) to b * 2, Cell(1, 0) to b / 2, Cell(2, 1) to b, Cell(3, 2) to b * 5)
                result = r.copy(effects = FeatureEffects(coins = coins, collector = Cell(4, 1), collected = coins.values.sum()))
                lastCollected = coins.values.sum()
            }
            Feature.COLLECT_METER -> {
                result = r.copy(effects = FeatureEffects(diamonds = setOf(Cell(1, 1), Cell(3, 0))))
                meter = 34
            }
            Feature.CASCADE -> cascadeMultiplier = 3
            Feature.FREE_SPINS_MULTIPLIER -> { freeSpins = 6; freeMultiplier = 3 }
            else -> Unit
        }
    }

    companion object {
        const val AUTO_SPINS = 50
    }
}
