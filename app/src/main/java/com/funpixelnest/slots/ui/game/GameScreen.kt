package com.funpixelnest.slots.ui.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.funpixelnest.slots.data.AppGraph
import com.funpixelnest.slots.data.ThemeAssets
import com.funpixelnest.slots.ui.common.ArtButton
import com.funpixelnest.slots.ui.common.AssetImage
import com.funpixelnest.slots.ui.common.Gold
import com.funpixelnest.slots.ui.common.CoinShower
import com.funpixelnest.slots.ui.common.formatCompact
import com.funpixelnest.slots.ui.common.formatCoins
import com.funpixelnest.slots.ui.common.outlinedText
import com.funpixelnest.slots.ui.common.themeFont
import com.funpixelnest.slots.ui.lobby.CoinsBox
import com.funpixelnest.slots.ui.lobby.LevelBadge
import com.funpixelnest.slots.ui.lobby.LoadBar

@Composable
fun GameScreen(
    graph: AppGraph,
    themeId: String,
    onExit: () -> Unit,
    /** Screenshot hook: runs once the machine has loaded. */
    onReady: ((GameViewModel) -> Unit)? = null,
) {
    val assets = remember(themeId) { graph.theme(themeId) }
    if (assets == null) {
        LaunchedEffect(Unit) { onExit() }
        return
    }
    val vm: GameViewModel = viewModel(
        key = "game-$themeId",
        factory = viewModelFactory { initializer { GameViewModel(graph, assets) } },
    )
    BackHandler { onExit() }
    DisposableEffect(Unit) {
        graph.sound.startMusic()
        onDispose { }
    }

    val cfg = assets.config
    val ready = vm.phase != Phase.Loading
    LaunchedEffect(ready) { if (ready) onReady?.invoke(vm) }

    Box(Modifier.fillMaxSize().background(Color.Black).then(if (ready) Modifier.testTag("game-ready") else Modifier)) {
        // portrait art when the theme has it, otherwise the centre of the landscape background
        AssetImage(assets, cfg.backgroundPortrait ?: cfg.background, Modifier.fillMaxSize(), ContentScale.Crop)

        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            TopBar(vm, assets, onExit)
            TitleBanner(assets)
            JackpotBar(vm, assets)

            // ---------------- reels: full width, cells a little taller than wide
            Box(
                Modifier.fillMaxWidth().weight(1f).padding(horizontal = 6.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(Modifier.fillMaxWidth().aspectRatio(cfg.reels / (cfg.rows * REEL_CELL_TALL))) {
                    if (cfg.frame != null) {
                        AssetImage(assets, cfg.frame, Modifier.fillMaxSize(), ContentScale.FillBounds)
                    } else {
                        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(14.dp)))
                    }
                    ReelsView(
                        graph = graph,
                        assets = assets,
                        request = vm.spinRequest,
                        initialStops = vm.initialStops,
                        outcome = vm.result?.outcome,
                        showWins = vm.phase == Phase.ShowingWin,
                        onStopped = vm::onReelsStopped,
                        modifier = Modifier.fillMaxSize().padding(if (cfg.frame != null) 8.dp else 4.dp),
                    )
                }
            }

            WinBanner(vm, assets)
            BottomPanel(vm, assets)
        }

        if (vm.phase == Phase.Loading) LoadingOverlay(graph, assets, vm.loadProgress)
        // coins rain behind popups so the win amount stays readable
        CoinShower(vm.showerTrigger, vm.showerCoins, Modifier.fillMaxSize())
        GamePopups(graph, vm, assets)
    }
}

/** Height/width of one reel cell in portrait (symbols are drawn fitted inside). */
private const val REEL_CELL_TALL = 1.22f

@Composable
private fun TopBar(vm: GameViewModel, assets: ThemeAssets, onExit: () -> Unit) {
    val ui = assets.config.ui
    Row(
        Modifier.fillMaxWidth().height(54.dp).background(Color.Black.copy(alpha = 0.45f)).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        ArtButton(assets, ui["menu"], Modifier.size(38.dp), label = if (ui["menu"] == null) "☰" else null) { onExit() }
        LevelBadge(vm.level, vm.levelProgress, compact = true)
        Spacer(Modifier.weight(1f))
        CoinsBox(vm.shownCoins)
        ArtButton(assets, ui["info"], Modifier.size(36.dp), label = if (ui["info"] == null) "i" else null) {
            vm.showPopup(Popup.Info)
        }
        ArtButton(assets, ui["settings"], Modifier.size(36.dp), label = if (ui["settings"] == null) "⚙" else null) {
            vm.showPopup(Popup.Settings)
        }
    }
}

@Composable
private fun TitleBanner(assets: ThemeAssets) {
    Box(Modifier.fillMaxWidth().height(62.dp).padding(top = 4.dp), contentAlignment = Alignment.Center) {
        if (assets.config.title != null) {
            AssetImage(assets, assets.config.title, Modifier.fillMaxHeight().fillMaxWidth(0.8f))
        } else {
            Text(assets.config.name, style = outlinedText(30.sp, Gold, themeFont(assets.config.id)))
        }
    }
}

@Composable
private fun WinBanner(vm: GameViewModel, assets: ThemeAssets) {
    val win = vm.shownWin
    Box(Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 40.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
        val art = assets.config.ui["panelWin"]
        if (art != null) AssetImage(assets, art, Modifier.fillMaxSize(), ContentScale.FillBounds)
        else Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(50)))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("WIN  ", style = outlinedText(13.sp, Color(0xFFE0E0E0)))
            Text(formatCoins(win), style = outlinedText(if (win > 0) 22.sp else 18.sp, if (win > 0) Gold else Color.White), maxLines = 1)
        }
    }
}

/** Bet row on top, then AUTO · big round SPIN · MAX BET. */
@Composable
private fun BottomPanel(vm: GameViewModel, assets: ThemeAssets) {
    val cfg = assets.config
    val ui = cfg.ui
    fun label(key: String, text: String) = if (key in cfg.uiLabeled && ui[key] != null) null else text
    val busy = vm.phase == Phase.Spinning || vm.phase == Phase.Loading
    val betLocked = busy || vm.freeSpins > 0

    Column(
        Modifier.fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Black.copy(alpha = 0.7f))))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ArtButton(assets, ui["minus"], Modifier.size(44.dp), label = if (ui["minus"] == null) "−" else null, enabled = !betLocked) {
                vm.changeBet(-1)
            }
            InfoPanel(
                assets, "panelBet", "TOTAL BET  ·  LEVEL ${vm.betIndex + 1}/${vm.session.unlockedSteps}",
                formatCompact(vm.totalBet), Modifier.width(210.dp).height(56.dp),
                footnote = vm.nextBetUnlockLevel?.takeIf { vm.isMaxBet }?.let { "Level $it unlocks higher bets" },
            )
            ArtButton(assets, ui["plus"], Modifier.size(44.dp), label = if (ui["plus"] == null) "+" else null, enabled = !betLocked) {
                vm.changeBet(1)
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            val autoText = if (vm.autoSpinsLeft > 0) "STOP\n${vm.autoSpinsLeft}" else "AUTO\nSPIN"
            ArtButton(
                assets, ui["autoSpin"], Modifier.width(96.dp).height(56.dp),
                label = if (vm.autoSpinsLeft > 0) autoText else label("autoSpin", autoText), labelSize = 14.sp,
            ) { vm.toggleAuto() }

            val spinText = if (vm.freeSpins > 0) "FREE\n${vm.freeSpins}" else "SPIN"
            ArtButton(
                assets, ui["spin"], Modifier.size(118.dp),
                label = if (vm.freeSpins > 0) spinText else label("spin", spinText),
                labelSize = 26.sp, enabled = !busy, fallbackColor = Color(0xFFD32F2F),
            ) { vm.onSpinClick() }

            ArtButton(
                assets, ui["maxBet"], Modifier.width(96.dp).height(56.dp),
                label = label("maxBet", "MAX\nBET"), labelSize = 14.sp, enabled = !betLocked,
            ) { vm.maxBet() }
        }
    }
}

@Composable
private fun InfoPanel(
    assets: ThemeAssets,
    key: String,
    title: String,
    value: String,
    modifier: Modifier,
    highlight: Boolean = false,
    footnote: String? = null,
) {
    val art = assets.config.ui[key]
    val artHasTitle = key in assets.config.uiLabeled
    Box(modifier, contentAlignment = Alignment.Center) {
        if (art != null) AssetImage(assets, art, Modifier.fillMaxSize(), ContentScale.FillBounds)
        else Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp)))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (!artHasTitle) Text(title, style = outlinedText(10.sp, Color(0xFFE0E0E0)))
            Text(value, style = outlinedText(if (highlight) 22.sp else 20.sp, if (highlight) Gold else Color.White), maxLines = 1)
        }
        if (footnote != null) {
            Text(footnote, style = outlinedText(9.sp, Color(0xFFFFCC80)), modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
private fun LoadingOverlay(graph: AppGraph, assets: ThemeAssets, progress: Float) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.85f)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (assets.config.title != null) AssetImage(assets, assets.config.title, Modifier.width(320.dp).height(110.dp))
            else Text(assets.config.name, style = outlinedText(34.sp, Gold, themeFont(assets.config.id)))
            Spacer(Modifier.height(18.dp))
            LoadBar(graph, progress, Modifier.width(240.dp).height(26.dp))
        }
    }
}
