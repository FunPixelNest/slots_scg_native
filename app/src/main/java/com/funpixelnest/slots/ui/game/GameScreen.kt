package com.funpixelnest.slots.ui.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import com.funpixelnest.slots.ui.common.formatCoins
import com.funpixelnest.slots.ui.common.outlinedText
import com.funpixelnest.slots.ui.common.themeFont
import com.funpixelnest.slots.ui.lobby.CoinsBox
import com.funpixelnest.slots.ui.lobby.LevelBadge
import com.funpixelnest.slots.ui.lobby.LoadBar

@Composable
fun GameScreen(graph: AppGraph, themeId: String, onExit: () -> Unit) {
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

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AssetImage(assets, cfg.background, Modifier.fillMaxSize(), ContentScale.Crop)

        Column(Modifier.fillMaxSize()) {
            TopBar(vm, assets, onExit)

            // ---------------- reels
            Box(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 8.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.fillMaxHeight().aspectRatio(cfg.reels / (cfg.rows * 1.02f), matchHeightConstraintsFirst = true)) {
                    if (cfg.frame != null) {
                        AssetImage(assets, cfg.frame, Modifier.fillMaxSize(), ContentScale.FillBounds)
                    } else {
                        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)))
                    }
                    ReelsView(
                        graph = graph,
                        assets = assets,
                        request = vm.spinRequest,
                        initialStops = vm.initialStops,
                        outcome = vm.result?.outcome,
                        showWins = vm.phase == Phase.ShowingWin,
                        onStopped = vm::onReelsStopped,
                        modifier = Modifier.fillMaxSize().padding(if (cfg.frame != null) 10.dp else 4.dp),
                    )
                }
            }

            BottomBar(vm, assets)
        }

        if (vm.phase == Phase.Loading) LoadingOverlay(graph, assets, vm.loadProgress)
        GamePopups(graph, vm, assets)
    }
}

@Composable
private fun TopBar(vm: GameViewModel, assets: ThemeAssets, onExit: () -> Unit) {
    val ui = assets.config.ui
    Row(
        Modifier.fillMaxWidth().height(52.dp).background(Color.Black.copy(alpha = 0.35f)).padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArtButton(assets, ui["menu"], Modifier.size(42.dp), label = if (ui["menu"] == null) "☰" else null) { onExit() }
        Spacer(Modifier.width(12.dp))
        LevelBadge(vm.level, vm.levelProgress)
        Spacer(Modifier.weight(1f))
        if (assets.config.title != null) {
            AssetImage(assets, assets.config.title, Modifier.height(46.dp).width(180.dp))
        } else {
            Text(assets.config.name, style = outlinedText(22.sp, Gold, themeFont(assets.config.id)))
        }
        Spacer(Modifier.weight(1f))
        CoinsBox(vm.shownCoins)
        Spacer(Modifier.width(10.dp))
        ArtButton(assets, ui["info"], Modifier.size(40.dp), label = if (ui["info"] == null) "i" else null) {
            vm.showPopup(Popup.Info)
        }
        Spacer(Modifier.width(8.dp))
        ArtButton(assets, ui["settings"], Modifier.size(40.dp), label = if (ui["settings"] == null) "⚙" else null) {
            vm.showPopup(Popup.Settings)
        }
    }
}

@Composable
private fun BottomBar(vm: GameViewModel, assets: ThemeAssets) {
    val cfg = assets.config
    val ui = cfg.ui
    fun label(key: String, text: String) = if (key in cfg.uiLabeled && ui[key] != null) null else text
    val busy = vm.phase == Phase.Spinning || vm.phase == Phase.Loading
    val betLocked = busy || vm.freeSpins > 0

    Row(
        Modifier.fillMaxWidth().height(84.dp).background(Color.Black.copy(alpha = 0.45f)).padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        InfoPanel(assets, "panelBalance", "WAYS", "${vm.session.linesCount}", Modifier.weight(0.8f))

        ArtButton(assets, ui["minus"], Modifier.size(40.dp), label = if (ui["minus"] == null) "−" else null, enabled = !betLocked) {
            vm.changeBet(-1)
        }
        InfoPanel(assets, "panelBet", "TOTAL BET", formatCoins(vm.totalBet), Modifier.weight(1.1f))
        ArtButton(assets, ui["plus"], Modifier.size(40.dp), label = if (ui["plus"] == null) "+" else null, enabled = !betLocked) {
            vm.changeBet(1)
        }

        InfoPanel(assets, "panelWin", "WIN", formatCoins(vm.shownWin), Modifier.weight(1.2f), highlight = vm.shownWin > 0)

        ArtButton(
            assets, ui["maxBet"], Modifier.width(92.dp).height(52.dp),
            label = label("maxBet", "MAX BET"), labelSize = 14.sp, enabled = !betLocked,
        ) { vm.maxBet() }

        val autoText = if (vm.autoSpinsLeft > 0) "STOP\n${vm.autoSpinsLeft}" else "AUTO\nSPIN"
        ArtButton(
            assets, ui["autoSpin"], Modifier.width(92.dp).height(52.dp),
            label = if (vm.autoSpinsLeft > 0) autoText else label("autoSpin", autoText), labelSize = 13.sp,
        ) { vm.toggleAuto() }

        val spinText = when {
            vm.freeSpins > 0 -> "FREE\n${vm.freeSpins}"
            else -> "SPIN"
        }
        ArtButton(
            assets, ui["spin"], Modifier.width(110.dp).fillMaxHeight(),
            label = if (vm.freeSpins > 0) spinText else label("spin", spinText),
            labelSize = 20.sp, enabled = !busy, fallbackColor = Color(0xFFD32F2F),
        ) { vm.onSpinClick() }
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
) {
    val art = assets.config.ui[key]
    val artHasTitle = key in assets.config.uiLabeled
    Box(modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
        if (art != null) AssetImage(assets, art, Modifier.fillMaxWidth().height(46.dp), ContentScale.FillBounds)
        else Box(Modifier.fillMaxWidth().height(46.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp)))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (!artHasTitle) Text(title, style = outlinedText(10.sp, Color(0xFFE0E0E0)))
            Text(value, style = outlinedText(if (highlight) 18.sp else 16.sp, if (highlight) Gold else Color.White), maxLines = 1)
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
