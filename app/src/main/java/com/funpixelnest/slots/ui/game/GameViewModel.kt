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
import com.funpixelnest.slots.engine.JackpotTier
import com.funpixelnest.slots.engine.JackpotWin
import com.funpixelnest.slots.engine.SlotMachine
import com.funpixelnest.slots.engine.SpinResult
import com.funpixelnest.slots.engine.WinTier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class Phase { Loading, Idle, Spinning, ShowingWin }

sealed interface Popup {
    data class BigWin(val tier: WinTier, val coins: Long) : Popup
    data class Jackpot(val win: JackpotWin) : Popup
    data class FreeSpins(val count: Int) : Popup
    data class LevelUp(val level: Int, val reward: Long, val betUnlocked: Boolean) : Popup
    data object Info : Popup
    data object Settings : Popup
    data object NoCoins : Popup
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
    var autoSpinsLeft by mutableIntStateOf(0); private set
    var level by mutableIntStateOf(session.level); private set
    var levelProgress by mutableStateOf(session.levelProgress); private set
    var jackpots by mutableStateOf(readJackpots()); private set

    /** Increments to launch a coin shower; [showerCoins] sets its size. */
    var showerTrigger by mutableIntStateOf(0); private set
    var showerCoins by mutableIntStateOf(0); private set

    val lineBet: Int get() = session.lineBet
    val totalBet: Long get() = session.totalBet
    val isMaxBet: Boolean get() = betIndex == session.unlockedSteps - 1
    val nextBetUnlockLevel: Int? get() = session.nextBetUnlockLevel
    val initialStops: List<Int> = assets.config.reelStrips.map { 0 }

    private var nonce = 0
    private var presentJob: Job? = null

    init {
        viewModelScope.launch {
            assets.preload { loadProgress = it }
            phase = Phase.Idle
        }
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
        result = r
        shownWin = 0
        shownCoins = session.coins - r.totalWin - r.levelUpReward
        freeSpins = session.freeSpins - r.freeSpinsWon
        jackpots = readJackpots()
        phase = Phase.Spinning
        spinRequest = SpinRequest(r.outcome.stops, ++nonce)
        graph.sound.play(Sfx.Spin, 0.7f)
    }

    /** Called by the reels when the last reel has settled. */
    fun onReelsStopped() {
        val r = result ?: return
        phase = Phase.ShowingWin
        presentJob = viewModelScope.launch {
            if (r.winCoins > 0) {
                val big = r.tier >= WinTier.BIG
                graph.sound.play(if (big) Sfx.BigWin else Sfx.WinCoins)
                shower(if (big) 70 else 18)
                if (big) popup = Popup.BigWin(r.tier, r.winCoins)
                countUp(r.winCoins)
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
            if (r.freeSpinsWon > 0) {
                while (popup != null) delay(200)
                graph.sound.play(Sfx.Bonus)
                popup = Popup.FreeSpins(r.freeSpinsWon)
            }
            freeSpins = session.freeSpins
            level = session.level
            levelProgress = session.levelProgress
            r.levelUp?.let {
                while (popup != null) delay(200)
                val unlocked = Economy.unlockedBetSteps(it) > Economy.unlockedBetSteps(it - 1)
                popup = Popup.LevelUp(it, r.levelUpReward, unlocked)
                shower(40)
            }
            shownCoins = session.coins
            // keep going for free games and auto spins
            if (session.freeSpins > 0 || autoSpinsLeft > 0) {
                delay(if (r.outcome.isWin) 1600 else 500)
                while (popup != null) delay(200)
                spin()
            }
        }
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
        val win = outcome.payUnits * session.lineBet
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

    companion object {
        const val AUTO_SPINS = 50
    }
}
