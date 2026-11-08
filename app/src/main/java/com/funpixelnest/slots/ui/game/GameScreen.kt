package com.funpixelnest.slots.ui.game

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.ui.draw.scale
import com.funpixelnest.slots.engine.Feature
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import com.funpixelnest.slots.ui.skin.LevelMedallion
import com.funpixelnest.slots.ui.skin.XpBar
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import com.funpixelnest.slots.ui.common.AssetImage
import com.funpixelnest.slots.ui.common.CoinShower
import com.funpixelnest.slots.ui.common.formatCompact
import com.funpixelnest.slots.ui.common.formatCoins
import com.funpixelnest.slots.ui.common.outlinedText
import com.funpixelnest.slots.ui.common.themeFont
import com.funpixelnest.slots.ui.lobby.LoadBar
import com.funpixelnest.slots.ui.skin.CoinCapsule
import com.funpixelnest.slots.ui.skin.GoldFrame
import com.funpixelnest.slots.ui.skin.IconRoundButton
import com.funpixelnest.slots.ui.skin.Ribbon
import com.funpixelnest.slots.ui.skin.Skin
import com.funpixelnest.slots.ui.skin.SkinButton
import com.funpixelnest.slots.ui.skin.SkinIcon
import com.funpixelnest.slots.ui.skin.SkinTone
import com.funpixelnest.slots.ui.skin.SpinButton
import com.funpixelnest.slots.ui.skin.ValueWell

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

        // soft shade behind the jackpots so they read on bright machine art
        Box(
            Modifier.fillMaxWidth().height(260.dp).background(
                Brush.verticalGradient(0f to Color(0xB316052C), 0.75f to Color(0x6616052C), 1f to Color.Transparent),
            ),
        )
        val busy = vm.phase == Phase.Spinning || vm.phase == Phase.Loading
        Column(Modifier.fillMaxSize()) {
            CabinetTopBar(
                vm.level, vm.levelProgress, vm.shownCoins,
                onBack = onExit, onCoins = { graph.openStore() }, onMenu = { vm.showPopup(Popup.Info) },
            ) { XpFloat(vm.xpTick, vm.xpGained, Modifier.align(Alignment.TopEnd)) }
            Spacer(Modifier.height(10.dp))
            FeatureHeader(vm, assets)

            // ---------------- reels inside the gold cabinet
            BoxWithConstraints(
                Modifier.fillMaxWidth().weight(1f).padding(horizontal = 6.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                // cells grow from slightly tall up to REEL_CELL_MAX_TALL with the free height (frame takes ~60dp)
                val tall = (((maxHeight - 60.dp) / (maxWidth - 30.dp)) * cfg.reels / cfg.rows).coerceIn(REEL_CELL_TALL, REEL_CELL_MAX_TALL)
                val plaque = featureChip(vm.feature)?.let { "$it · 243 WAYS" } ?: "243 WAYS"
                val frameH = (maxWidth - 30.dp) * (cfg.rows * tall / cfg.reels) + 60.dp
                CabinetFrame(plaque, Modifier.fillMaxWidth().height(minOf(frameH, maxHeight))) {
                    ReelsView(
                        graph = graph,
                        assets = assets,
                        request = vm.spinRequest,
                        initialStops = vm.initialStops,
                        outcome = vm.result?.outcome,
                        showWins = vm.phase == Phase.ShowingWin,
                        effects = vm.result?.effects,
                        onStopped = vm::onReelsStopped,
                        accent = Skin.Gold,
                        cascadeGrid = vm.cascadeGrid,
                        cascadeWins = vm.cascadeWins,
                        cascadeDropped = vm.cascadeDropped,
                        cascadeTick = vm.cascadeTick,
                        marks = if (vm.phase == Phase.ShowingWin) marksOf(vm.result?.effects) else emptyMap(),
                        modifier = Modifier.fillMaxSize(),
                    )
                    FeatureBanner(vm.featureBanner, Modifier.align(Alignment.Center))
                }
            }

            WinMeter(vm.shownWin, Modifier.align(Alignment.CenterHorizontally).width(260.dp).height(42.dp))
            Spacer(Modifier.height(32.dp)) // room for the raised SPIN button
            ControlDeck(
                totalBet = vm.totalBet, betLocked = busy || vm.freeSpins > 0, spinEnabled = !busy,
                freeSpins = vm.freeSpins, autoLeft = vm.autoSpinsLeft,
                onMinus = { vm.changeBet(-1) }, onPlus = { vm.changeBet(1) }, onSpin = vm::onSpinClick,
                onAuto = vm::toggleAuto, onMaxBet = vm::maxBet,
            )
        }

        if (vm.phase == Phase.Loading) LoadingOverlay(graph, assets, vm.loadProgress)
        // coins rain behind popups so the win amount stays readable
        CoinShower(vm.showerTrigger, vm.showerCoins, Modifier.fillMaxSize())
        BonusOverlay(graph, vm)
        GamePopups(graph, vm, assets)
    }
}

/** Height/width of one reel cell in portrait (symbols are drawn fitted inside). */
private const val REEL_CELL_TALL = 1.22f
private const val REEL_CELL_MAX_TALL = 1.55f

/** "+N XP" that rises and fades each time [tick] changes. */
@Composable
private fun XpFloat(tick: Int, xp: Int, modifier: Modifier) {
    if (tick == 0 || xp <= 0) return
    val anim = remember(tick) { Animatable(0f) }
    LaunchedEffect(tick) { anim.animateTo(1f, tween(1100)) }
    val t = anim.value
    if (t >= 1f) return
    Text(
        "+$xp XP",
        style = outlinedText(15.sp, Color(0xFF7CF29A)),
        modifier = modifier.offset(y = (12 - 26 * t).dp).alpha(1f - t * t).padding(end = 18.dp),
    )
}

@Composable
private fun LoadingOverlay(graph: AppGraph, assets: ThemeAssets, progress: Float) {
    Box(Modifier.fillMaxSize().background(Skin.VelvetDeep), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (assets.config.title != null) AssetImage(assets, assets.config.title, Modifier.width(320.dp).height(110.dp))
            else Text(assets.config.name, style = outlinedText(34.sp, Skin.GoldLight, themeFont(assets.config.id)))
            Spacer(Modifier.height(18.dp))
            LoadBar(graph, progress, Modifier.width(240.dp).height(26.dp))
        }
    }
}

/** Big flashing text over the reels when a feature fires. */
@Composable
private fun FeatureBanner(text: String?, modifier: Modifier) {
    if (text == null) return
    val pop = remember(text) { Animatable(0.4f) }
    LaunchedEffect(text) { pop.animateTo(1f, spring(dampingRatio = 0.45f)) }
    Box(
        modifier.scale(pop.value)
            .background(Skin.goldBorder, RoundedCornerShape(16.dp)).padding(3.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFFE53935), Color(0xFF6A0D0D))), RoundedCornerShape(13.dp))
            .padding(horizontal = 22.dp, vertical = 10.dp),
    ) { Text(text, style = outlinedText(26.sp, Skin.GoldLight)) }
}

/** Short feature name for the HUD chip and lobby cards. */
fun featureChip(f: Feature?): String? = f?.title?.uppercase()

/** Coins, collector and diamonds a spin put on the reels. */
private fun marksOf(fx: com.funpixelnest.slots.engine.FeatureEffects?): Map<com.funpixelnest.slots.engine.Cell, CellMark> {
    if (fx == null) return emptyMap()
    val m = HashMap<com.funpixelnest.slots.engine.Cell, CellMark>()
    fx.coins.forEach { (c, v) -> m[c] = CellMark.Coin(v) }
    fx.diamonds.forEach { m[it] = CellMark.Diamond }
    fx.collector?.let { m[it] = CellMark.Collector }
    return m
}
