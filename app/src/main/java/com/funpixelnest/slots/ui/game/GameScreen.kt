package com.funpixelnest.slots.ui.game

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

        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            TopBar(vm, assets, onExit)
            TitleBanner(assets)
            featureChip(vm.feature)?.let { chip ->
                Box(Modifier.fillMaxWidth().padding(bottom = 4.dp), contentAlignment = Alignment.Center) {
                    Ribbon("★ $chip ★", tone = SkinTone.Purple, textSize = 12.sp)
                }
            }
            JackpotBar(vm, assets)

            // ---------------- reels: full width, cells a little taller than wide
            BoxWithConstraints(
                Modifier.fillMaxWidth().weight(1f).padding(horizontal = 6.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                // use the free height: cells grow from slightly tall up to REEL_CELL_MAX_TALL
                val tall = ((maxHeight / maxWidth) * cfg.reels / cfg.rows).coerceIn(REEL_CELL_TALL, REEL_CELL_MAX_TALL)
                GoldFrame(Modifier.fillMaxWidth().aspectRatio(cfg.reels / (cfg.rows * tall)), corner = 18.dp) {
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
                        effects = vm.result?.effects,
                        onStopped = vm::onReelsStopped,
                        modifier = Modifier.fillMaxSize().padding(if (cfg.frame != null) 8.dp else 4.dp),
                    )
                    FeatureBanner(vm.featureBanner, Modifier.align(Alignment.Center))
                }
            }

            WinBanner(vm, assets)
            BottomPanel(vm, assets)
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

@Composable
private fun TopBar(vm: GameViewModel, @Suppress("UNUSED_PARAMETER") assets: ThemeAssets, onExit: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        IconRoundButton(SkinIcon.Back, size = 40.dp, tone = SkinTone.Red) { onExit() }
        // player plate: level medallion + XP bar, with "+XP" floating up after every spin
        Box(Modifier.weight(1f)) {
            Row(
                Modifier.fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(50))
                    .background(Skin.goldBorder, RoundedCornerShape(50))
                    .padding(2.dp)
                    .background(Brush.verticalGradient(listOf(Color(0xFF3B0D5C), Color(0xFF14031F))), RoundedCornerShape(50))
                    .padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LevelMedallion(vm.level, vm.levelProgress, size = 50.dp)
                XpBar(vm.level, vm.levelProgress, Modifier.weight(1f).padding(start = 6.dp))
            }
            XpFloat(vm.xpTick, vm.xpGained, Modifier.align(Alignment.TopEnd))
        }
        CoinCapsule(vm.shownCoins)
        IconRoundButton(SkinIcon.Info, size = 34.dp) { vm.showPopup(Popup.Info) }
        IconRoundButton(SkinIcon.Gear, size = 34.dp) { vm.showPopup(Popup.Settings) }
    }
}

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
private fun TitleBanner(assets: ThemeAssets) {
    Box(Modifier.fillMaxWidth().height(60.dp).padding(top = 4.dp), contentAlignment = Alignment.Center) {
        if (assets.config.title != null) {
            AssetImage(assets, assets.config.title, Modifier.fillMaxHeight().fillMaxWidth(0.8f))
        } else {
            Text(assets.config.name.uppercase(), style = outlinedText(28.sp, Skin.GoldLight, themeFont(assets.config.id)))
        }
    }
}

@Composable
private fun WinBanner(vm: GameViewModel, @Suppress("UNUSED_PARAMETER") assets: ThemeAssets) {
    val win = vm.shownWin
    Box(Modifier.fillMaxWidth().padding(horizontal = 56.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
        ValueWell(if (win > 0) "YOU WIN" else "WIN", formatCoins(win), Modifier.fillMaxWidth(), highlight = win > 0)
    }
}

/** Velvet console: bet row on top, then AUTO · round SPIN · MAX BET. */
@Composable
private fun BottomPanel(vm: GameViewModel, @Suppress("UNUSED_PARAMETER") assets: ThemeAssets) {
    val busy = vm.phase == Phase.Spinning || vm.phase == Phase.Loading
    val betLocked = busy || vm.freeSpins > 0

    val chase by rememberInfiniteTransition(label = "console").animateFloat(
        0f, 3f, infiniteRepeatable(tween(900, easing = LinearEasing)), label = "c",
    )
    Column(
        Modifier.fillMaxWidth()
            .drawWithContent {
                drawContent()
                // cabinet light strip and corner rivets
                val n = 18
                for (k in 0 until n) {
                    val x = size.width * (k + 0.5f) / n
                    val lit = (k + chase.toInt()) % 3 == 0
                    drawCircle(if (lit) Color(0xFFFFFDE7) else Color(0xFFB98A2A), 2.6.dp.toPx(), Offset(x, 7.dp.toPx()))
                    if (lit) drawCircle(Color(0x55FFF59D), 5.dp.toPx(), Offset(x, 7.dp.toPx()))
                }
                listOf(14.dp.toPx() to size.height - 14.dp.toPx(), size.width - 14.dp.toPx() to size.height - 14.dp.toPx())
                    .forEach { (x, y) ->
                        drawCircle(Skin.GoldDark, 4.dp.toPx(), Offset(x, y))
                        drawCircle(Skin.GoldLight, 2.dp.toPx(), Offset(x - 1, y - 1))
                    }
            }
            .background(Skin.goldBorder, RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
            .padding(top = 2.5.dp)
            .background(Skin.velvetFill, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(start = 14.dp, end = 14.dp, top = 16.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconRoundButton(SkinIcon.Minus, size = 44.dp, tone = SkinTone.Blue, enabled = !betLocked) { vm.changeBet(-1) }
            ValueWell(
                "TOTAL BET  ·  LEVEL ${vm.betIndex + 1}/${vm.session.unlockedSteps}",
                formatCompact(vm.totalBet), Modifier.width(210.dp),
                footnote = vm.nextBetUnlockLevel?.takeIf { vm.isMaxBet }?.let { "Level $it unlocks higher bets" },
            )
            IconRoundButton(SkinIcon.Plus, size = 44.dp, tone = SkinTone.Blue, enabled = !betLocked) { vm.changeBet(1) }
        }
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            SkinButton(
                if (vm.autoSpinsLeft > 0) "STOP\n${vm.autoSpinsLeft}" else "AUTO\nSPIN",
                Modifier.width(94.dp).height(58.dp),
                tone = if (vm.autoSpinsLeft > 0) SkinTone.Red else SkinTone.Purple, textSize = 14.sp,
            ) { vm.toggleAuto() }

            SpinButton(
                if (vm.freeSpins > 0) "FREE" else "SPIN",
                Modifier.size(124.dp),
                sub = if (vm.freeSpins > 0) "${vm.freeSpins} LEFT" else null,
                enabled = !busy,
            ) { vm.onSpinClick() }

            SkinButton(
                "MAX\nBET", Modifier.width(94.dp).height(58.dp),
                tone = SkinTone.Gold, enabled = !betLocked, textSize = 14.sp,
            ) { vm.maxBet() }
        }
    }
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
