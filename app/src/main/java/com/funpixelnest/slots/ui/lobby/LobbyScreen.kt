package com.funpixelnest.slots.ui.lobby

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funpixelnest.slots.audio.Sfx
import com.funpixelnest.slots.data.AppGraph
import com.funpixelnest.slots.engine.Economy
import com.funpixelnest.slots.engine.JackpotTier
import com.funpixelnest.slots.engine.ThemeIndexEntry
import com.funpixelnest.slots.engine.Unlock
import com.funpixelnest.slots.engine.Unlocks
import com.funpixelnest.slots.ui.common.AssetImage
import com.funpixelnest.slots.ui.common.CoinIcon
import com.funpixelnest.slots.ui.common.CoinShower
import com.funpixelnest.slots.ui.common.formatCompact
import com.funpixelnest.slots.ui.common.outlinedText
import com.funpixelnest.slots.ui.game.PopupFrame
import com.funpixelnest.slots.ui.game.SettingsPopup
import com.funpixelnest.slots.ui.skin.CoinCapsule
import com.funpixelnest.slots.ui.skin.GoldFrame
import com.funpixelnest.slots.ui.skin.IconRoundButton
import com.funpixelnest.slots.ui.skin.LevelMedallion
import com.funpixelnest.slots.ui.skin.MarqueeLights
import com.funpixelnest.slots.ui.skin.Ribbon
import com.funpixelnest.slots.ui.skin.Skin
import com.funpixelnest.slots.ui.skin.SkinButton
import com.funpixelnest.slots.ui.skin.SkinDialog
import com.funpixelnest.slots.ui.skin.SkinIcon
import com.funpixelnest.slots.ui.skin.SkinTone
import com.funpixelnest.slots.ui.skin.Sunburst
import com.funpixelnest.slots.ui.skin.VelvetBackdrop
import com.funpixelnest.slots.ui.skin.XpBar
import com.funpixelnest.slots.ui.skin.drawPadlock
import com.funpixelnest.slots.ui.skin.drawSkinIcon
import kotlinx.coroutines.delay
import kotlin.math.sin

/**
 * Game-style lobby: player HUD on top, a marquee sign, a sideways-scrolling casino floor of
 * machine cabinets (two rows, locked ones behind a padlock), and a bottom dock of bonuses.
 */
@Composable
fun LobbyScreen(
    graph: AppGraph,
    onOpenTheme: (String) -> Unit,
    /** Screenshot hook: open the daily bonus popup straight away. */
    startWithDailyBonus: Boolean = false,
    /** Screenshot hooks: open the unlocks list, or the locked popup of a machine. */
    startWithUnlocks: Boolean = false,
    startWithLocked: String? = null,
) {
    val coins by graph.player.coinsFlow.collectAsState()
    val level by graph.player.levelFlow.collectAsState()
    val progress by graph.player.progressFlow.collectAsState()
    var showSettings by remember { mutableStateOf(false) }
    var showDaily by remember { mutableStateOf(startWithDailyBonus) }
    var showUnlocks by remember { mutableStateOf(startWithUnlocks) }
    var locked by remember { mutableStateOf(graph.themes.firstOrNull { it.id == startWithLocked }) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var shower by remember { mutableIntStateOf(0) }
    var refresh by remember { mutableIntStateOf(0) } // bumps after a video unlock

    LaunchedEffect(Unit) {
        graph.sound.startMusic()
        if (graph.player.nextDailyDay(now) != null && !startWithUnlocks && startWithLocked == null) showDaily = true
        while (true) { delay(1000); now = System.currentTimeMillis() }
    }

    Box(Modifier.fillMaxSize().background(Skin.VelvetDeep).testTag("lobby")) {
        VelvetBackdrop(Modifier.fillMaxSize())
        StageLights(Modifier.fillMaxSize())

        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            PlayerHud(level, progress, coins, onCoins = { graph.sound.play(Sfx.Button); showDaily = true }) {
                graph.sound.play(Sfx.Button); showSettings = true
            }
            // ---------------- machines: vertical scroll, two columns (sign scrolls with them)
            val unlocked = remember(refresh, level) { graph.themes.count { graph.player.isOpen(it) } }
            LazyVerticalGrid(
                GridCells.Fixed(2),
                Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) { MarqueeSign() }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("MACHINES", style = outlinedText(14.sp, Skin.GoldLight).copy(letterSpacing = 2.sp))
                        Spacer(Modifier.weight(1f))
                        Text("$unlocked / ${graph.themes.size} UNLOCKED", style = outlinedText(11.sp, Skin.TextSoft))
                    }
                }
                items(graph.themes, key = { it.id }) { entry ->
                    val open = remember(refresh, level) { graph.player.isOpen(entry) }
                    MachineCabinet(graph, entry, open, level) {
                        if (open) {
                            graph.sound.play(Sfx.Choose)
                            onOpenTheme(entry.id)
                        } else {
                            graph.sound.play(Sfx.Button)
                            locked = entry
                        }
                    }
                }
            }

            BottomDock(
                graph, now,
                onHourly = { shower++ },
                onDaily = { showDaily = true },
                onVideoCoins = {
                    graph.ads.show("free_coins") {
                        if (graph.player.claimVideoCoins(System.currentTimeMillis()) > 0) {
                            graph.sound.play(Sfx.WinCoins); shower++
                        }
                    }
                },
                onUnlocks = { showUnlocks = true },
            )
        }

        if (showSettings) SettingsPopup(graph, graph.commonImages, null) { showSettings = false }
        CoinShower(shower, 60, Modifier.fillMaxSize())
        if (showDaily) DailyBonusPopup(graph, now, onClaimed = { shower++ }) { showDaily = false }
        if (showUnlocks) UnlocksPopup(graph, level) { showUnlocks = false }
        locked?.let { entry ->
            LockedMachinePopup(
                graph, entry, level,
                onWatch = {
                    locked = null
                    graph.ads.show("unlock_${entry.id}") {
                        graph.player.addVideoWatched(entry.id)
                        graph.sound.play(Sfx.Bonus)
                        refresh++
                        shower++
                    }
                },
            ) { locked = null }
        }
    }
}

/** Soft moving spotlights and twinkles behind the casino floor. */
@Composable
private fun StageLights(modifier: Modifier) {
    val t by rememberInfiniteTransition(label = "stage").animateFloat(
        0f, 1f, infiniteRepeatable(tween(6000, easing = LinearEasing)), label = "t",
    )
    Canvas(modifier) {
        val beams = listOf(Color(0x33FF4FD8), Color(0x332D7DF6), Color(0x33FFC93C))
        beams.forEachIndexed { i, c ->
            val x = size.width * (0.2f + 0.3f * i + 0.12f * sin((t * 6.283f) + i * 2f))
            drawCircle(Brush.radialGradient(listOf(c, Color.Transparent), Offset(x, size.height * 0.3f), size.width * 0.55f),
                size.width * 0.55f, Offset(x, size.height * 0.3f))
        }
        // twinkling sparkles
        for (k in 0 until 26) {
            val px = ((k * 137) % 100) / 100f * size.width
            val py = ((k * 71) % 100) / 100f * size.height
            val a = (0.5f + 0.5f * sin(t * 6.283f * 2 + k)).coerceIn(0f, 1f)
            drawCircle(Color(0xFFFFF59D).copy(alpha = a * 0.7f), 1.6.dp.toPx() + a * 1.5.dp.toPx(), Offset(px, py))
        }
    }
}

/** Player plate: XP medallion + XP bar, coin capsule, settings. */
@Composable
fun PlayerHud(level: Int, progress: Float, coins: Long, onCoins: () -> Unit, onSettings: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp)
            .shadow(10.dp, RoundedCornerShape(50))
            .background(Skin.goldBorder, RoundedCornerShape(50))
            .padding(2.5.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF3B0D5C), Color(0xFF14031F))), RoundedCornerShape(50))
            .padding(end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LevelMedallion(level, progress, size = 56.dp)
        XpBar(level, progress, Modifier.width(92.dp).padding(start = 6.dp))
        Spacer(Modifier.weight(1f))
        CoinCapsule(coins, onPlus = onCoins)
        Spacer(Modifier.width(6.dp))
        IconRoundButton(SkinIcon.Gear, size = 38.dp, onClick = onSettings)
    }
}

/** Light-bulb marquee sign with the game name. */
@Composable
private fun MarqueeSign() {
    Box(Modifier.fillMaxWidth().height(96.dp).padding(horizontal = 40.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
        Sunburst(Modifier.fillMaxSize().scale(1.4f))
        MarqueeLights(Modifier.fillMaxSize(), bulbs = 30) {
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color(0xFFB71C1C), Color(0xFF4A0000))), RoundedCornerShape(12.dp),
                ),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("FUNPIXEL", style = outlinedText(12.sp, Skin.GoldLight).copy(letterSpacing = 6.sp))
                    Text("SLOTS CASINO", style = outlinedText(28.sp, Color.White).copy(letterSpacing = 2.sp))
                }
            }
        }
    }
}

/**
 * One machine on the casino floor: gold cabinet with the machine art, a jackpot plate
 * labelled GRAND JACKPOT, and a padlock with the unlock condition when locked.
 */
@Composable
private fun MachineCabinet(graph: AppGraph, entry: ThemeIndexEntry, open: Boolean, level: Int, onClick: () -> Unit) {
    val grand = Economy.LINE_BETS[0].toLong() * 243 * JackpotTier.GRAND.betMultiple +
        graph.player.jackpotPool(entry.id, JackpotTier.GRAND)
    val rule = Unlocks.rule(entry.order)
    val bob by rememberInfiniteTransition(label = "bob").animateFloat(
        0f, 1f, infiniteRepeatable(tween(1400 + entry.order * 37), RepeatMode.Reverse), label = "b",
    )
    Column(
        Modifier.fillMaxWidth().aspectRatio(0.6f)
            .clickable(remember { MutableInteractionSource() }, null, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.weight(1f)) {
            GoldFrame(Modifier.fillMaxSize(), corner = 16.dp) {
                AssetImage(graph.commonImages, entry.tile, Modifier.fillMaxSize().alpha(if (open) 1f else 0.55f), ContentScale.Crop)
                if (!open) {
                    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))
                    LockBadge(rule, level, Modifier.align(Alignment.Center))
                }
            }
            when {
                entry.order < 3 -> Ribbon("HOT", Modifier.align(Alignment.TopEnd).offset(x = 4.dp, y = (-6).dp), tone = SkinTone.Red, textSize = 11.sp)
                open && rule != Unlock.Free -> Ribbon("NEW", Modifier.align(Alignment.TopEnd).offset(x = 4.dp, y = (-6).dp), tone = SkinTone.Green, textSize = 11.sp)
            }
            if (open) {
                // round play button that gently bobs
                Box(
                    Modifier.align(Alignment.TopStart).offset(x = (-4).dp, y = (-4 - 3 * bob).dp).size(36.dp)
                        .shadow(6.dp, CircleShape)
                        .background(Skin.goldBorder, CircleShape).padding(2.5.dp)
                        .background(Brush.radialGradient(listOf(Color(0xFF7CF29A), Color(0xFF138A3A))), CircleShape),
                    contentAlignment = Alignment.Center,
                ) { Text("▶", style = outlinedText(15.sp)) }
            }
        }
        Spacer(Modifier.height(10.dp))
        Column(
            Modifier.fillMaxWidth()
                .background(Skin.goldBorder, RoundedCornerShape(10.dp)).padding(1.5.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF2A0846), Color(0xFF0E0219))), RoundedCornerShape(9.dp))
                .padding(vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("GRAND JACKPOT", style = outlinedText(8.sp, Skin.TextSoft).copy(letterSpacing = 1.sp))
            Text(formatCompact(grand), style = outlinedText(14.sp, Skin.GoldLight))
        }
    }
}

/** Padlock and the condition that opens the machine. */
@Composable
private fun LockBadge(rule: Unlock, level: Int, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Canvas(Modifier.size(46.dp)) { drawPadlock(Offset.Zero, size.width) }
        when (rule) {
            is Unlock.AtLevel -> Pill("LEVEL ${rule.level}", SkinTone.Purple)
            is Unlock.WatchVideo -> {
                Pill("▶ WATCH", SkinTone.Green)
                Text("or level ${rule.level}", style = outlinedText(9.sp, Skin.TextSoft))
            }
            Unlock.Free -> Unit
        }
        if (rule is Unlock.AtLevel) {
            Text("${(rule.level - level).coerceAtLeast(0)} levels to go", style = outlinedText(9.sp, Skin.TextSoft))
        }
    }
}

@Composable
private fun Pill(text: String, tone: SkinTone) {
    Box(
        Modifier.background(Skin.goldBorder, RoundedCornerShape(50)).padding(1.5.dp)
            .background(Brush.verticalGradient(listOf(tone.top, tone.bottom)), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 3.dp),
    ) { Text(text, style = outlinedText(12.sp, Color.White)) }
}

private fun countdown(ms: Long): String {
    val s = ms / 1000
    return if (s >= 3600) "%d:%02d:%02d".format(s / 3600, s / 60 % 60, s % 60) else "%02d:%02d".format(s / 60, s % 60)
}

/** Bottom dock: Hourly · Daily · (big) Free Coins video · Unlocks. */
@Composable
private fun BottomDock(
    graph: AppGraph,
    now: Long,
    onHourly: () -> Unit,
    onDaily: () -> Unit,
    onVideoCoins: () -> Unit,
    onUnlocks: () -> Unit,
) {
    val player = graph.player
    val hourlyLeft = player.hourlyReadyIn(now)
    val dailyReady = player.nextDailyDay(now) != null
    val videoLeft = player.videoCoinsReadyIn(now)
    Box(Modifier.fillMaxWidth().height(104.dp), contentAlignment = Alignment.BottomCenter) {
        // dock plate
        Box(
            Modifier.fillMaxWidth().height(80.dp)
                .background(Skin.goldBorder, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .padding(top = 2.5.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF3B0D5C), Color(0xFF12021F))), RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
        )
        Row(Modifier.fillMaxWidth().height(104.dp), verticalAlignment = Alignment.Bottom) {
            DockButton(
                SkinIcon.Clock, "HOURLY",
                if (hourlyLeft == 0L) "COLLECT" else countdown(hourlyLeft),
                hot = hourlyLeft == 0L, modifier = Modifier.weight(1f),
            ) {
                if (player.claimHourly(System.currentTimeMillis()) > 0) { graph.sound.play(Sfx.WinCoins); onHourly() }
            }
            DockButton(SkinIcon.Gift, "DAILY", if (dailyReady) "READY!" else "DAY ${player.lastDailyDay}/7", hot = dailyReady, modifier = Modifier.weight(1f)) {
                graph.sound.play(Sfx.Button); onDaily()
            }
            // raised centre medallion
            Column(Modifier.weight(1.3f), horizontalAlignment = Alignment.CenterHorizontally) {
                val pulse by rememberInfiniteTransition(label = "vid").animateFloat(
                    1f, if (videoLeft == 0L) 1.08f else 1f, infiniteRepeatable(tween(650), RepeatMode.Reverse), label = "p",
                )
                Box(
                    Modifier.size(78.dp).scale(pulse).shadow(12.dp, CircleShape)
                        .background(Skin.goldBorder, CircleShape).padding(3.dp)
                        .background(Brush.radialGradient(listOf(Color(0xFFFF8A80), Color(0xFFB71C1C))), CircleShape)
                        .clickable(remember { MutableInteractionSource() }, null, enabled = videoLeft == 0L) {
                            graph.sound.play(Sfx.Button); onVideoCoins()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Canvas(Modifier.size(30.dp)) { drawSkinIcon(SkinIcon.Video) }
                        Text("+${formatCompact(player.videoCoinsAmount())}", style = outlinedText(11.sp, Skin.GoldLight))
                    }
                }
                Text(if (videoLeft == 0L) "FREE COINS" else countdown(videoLeft), style = outlinedText(10.sp, Color.White), modifier = Modifier.padding(bottom = 6.dp))
            }
            DockButton(SkinIcon.Lock, "UNLOCKS", "MACHINES", hot = false, modifier = Modifier.weight(1f)) {
                graph.sound.play(Sfx.Button); onUnlocks()
            }
            DockButton(SkinIcon.Trophy, "LEVEL", "REWARDS", hot = false, modifier = Modifier.weight(1f)) {
                graph.sound.play(Sfx.Button); onUnlocks()
            }
        }
    }
}

@Composable
private fun DockButton(icon: SkinIcon, title: String, sub: String, hot: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val glow by rememberInfiniteTransition(label = "dock").animateFloat(
        0.6f, 1f, infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "g",
    )
    Column(
        modifier.padding(bottom = 6.dp).clickable(remember { MutableInteractionSource() }, null, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(44.dp)
                .background(Skin.goldBorder, RoundedCornerShape(14.dp)).padding(2.dp)
                .background(
                    Brush.verticalGradient(
                        if (hot) listOf(Color(0xFF7CF29A), Color(0xFF138A3A)) else listOf(Color(0xFF8E44E8), Color(0xFF3B0D5C)),
                    ),
                    RoundedCornerShape(12.dp),
                )
                .alpha(if (hot) glow else 1f),
            contentAlignment = Alignment.Center,
        ) { Canvas(Modifier.size(24.dp)) { drawSkinIcon(icon) } }
        Text(title, style = outlinedText(10.sp, Skin.GoldLight))
        Text(sub, style = outlinedText(8.sp, if (hot) Color(0xFF7CF29A) else Skin.TextSoft))
    }
}

/** Tapping a locked machine: shows what opens it, with a watch-video shortcut where allowed. */
@Composable
private fun LockedMachinePopup(graph: AppGraph, entry: ThemeIndexEntry, level: Int, onWatch: () -> Unit, onDismiss: () -> Unit) {
    val rule = Unlocks.rule(entry.order)
    PopupFrame("LOCKED", onDismiss, tone = SkinTone.Purple, widthFraction = 0.86f) {
        Box(Modifier.width(150.dp).aspectRatio(392f / 588f)) {
            GoldFrame(Modifier.fillMaxSize()) {
                AssetImage(graph.commonImages, entry.tile, Modifier.fillMaxSize(), ContentScale.Crop)
            }
        }
        Text(entry.name.uppercase(), style = outlinedText(22.sp, Skin.GoldLight))
        when (rule) {
            is Unlock.AtLevel -> {
                Text("Reach LEVEL ${rule.level} to play.", style = outlinedText(16.sp))
                Text("You are level $level. Every spin earns XP!", style = outlinedText(12.sp, Skin.TextSoft))
                SkinButton("OK", Modifier.width(160.dp).height(48.dp), tone = SkinTone.Purple, onClick = onDismiss)
            }
            is Unlock.WatchVideo -> {
                Text("Watch a short video to unlock now,", style = outlinedText(15.sp))
                Text("or reach level ${rule.level}.", style = outlinedText(15.sp))
                SkinButton("▶  WATCH & UNLOCK", Modifier.width(240.dp).height(54.dp), tone = SkinTone.Green, textSize = 18.sp, onClick = onWatch)
            }
            Unlock.Free -> Unit
        }
    }
}

/** Level road: every machine with how it unlocks, open ones ticked. */
@Composable
private fun UnlocksPopup(graph: AppGraph, level: Int, onDismiss: () -> Unit) {
    PopupFrame("MACHINE UNLOCKS", onDismiss, tone = SkinTone.Gold, widthFraction = 0.94f, heightFraction = 0.8f) {
        Text("You are level $level. Machines open as you level up.", style = outlinedText(12.sp, Skin.TextSoft))
        LazyVerticalGrid(GridCells.Fixed(3), Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(graph.themes) { e ->
                val open = graph.player.isOpen(e)
                val rule = Unlocks.rule(e.order)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.fillMaxWidth().aspectRatio(392f / 588f)) {
                        GoldFrame(Modifier.fillMaxSize(), corner = 10.dp) {
                            AssetImage(graph.commonImages, e.tile, Modifier.fillMaxSize().alpha(if (open) 1f else 0.45f), ContentScale.Crop)
                            if (!open) Canvas(Modifier.size(30.dp).align(Alignment.Center)) { drawPadlock(Offset.Zero, size.width) }
                        }
                    }
                    Text(
                        when {
                            open -> "✓ OPEN"
                            rule is Unlock.AtLevel -> "LEVEL ${rule.level}"
                            rule is Unlock.WatchVideo -> "▶ VIDEO / LV ${rule.level}"
                            else -> ""
                        },
                        style = outlinedText(10.sp, if (open) Color(0xFF7CF29A) else Skin.GoldLight),
                    )
                }
            }
        }
        SkinButton("CLOSE", Modifier.width(160.dp).height(46.dp), tone = SkinTone.Purple, onClick = onDismiss)
    }
}

/** Loading bar used on machine load screens. */
@Composable
fun LoadBar(@Suppress("UNUSED_PARAMETER") graph: AppGraph, progress: Float, modifier: Modifier) {
    Box(
        modifier
            .background(Skin.goldBorder, RoundedCornerShape(50))
            .padding(2.5.dp)
            .background(Color(0xFF0B0114), RoundedCornerShape(50))
            .padding(3.dp)
    ) {
        Box(
            Modifier.fillMaxHeight().fillMaxWidth(progress.coerceIn(0.03f, 1f))
                .background(Brush.verticalGradient(listOf(Color(0xFFFFE27A), Color(0xFFE08A00))), RoundedCornerShape(50))
        )
    }
}

/** Seven-day streak calendar; day 7 pays the most, missing a day restarts at day 1. */
@Composable
private fun DailyBonusPopup(graph: AppGraph, now: Long, onClaimed: () -> Unit, onDismiss: () -> Unit) {
    val player = graph.player
    val today = player.nextDailyDay(now)
    val streakDay = today ?: player.lastDailyDay
    Box(
        Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)).clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        SkinDialog(
            "DAILY BONUS",
            Modifier.fillMaxWidth(0.92f)
                .clickable(remember { MutableInteractionSource() }, null) { /* swallow taps */ }
                .testTag("daily-bonus"),
            tone = SkinTone.Red, sunburst = true,
        ) {
            Text("Come back every day. Day 7 pays the most!", style = outlinedText(12.sp, Skin.TextSoft))
            Spacer(Modifier.height(12.dp))
            for (days in listOf(1..4, 5..Economy.DAILY_DAYS)) Row(
                Modifier.padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (d in days) {
                    val claimed = (today == null && d <= streakDay) || (today != null && d < today)
                    val isToday = d == today
                    Column(
                        Modifier.weight(if (d == 7) 2f else 1f).height(100.dp)
                            .background(
                                when {
                                    isToday -> Brush.verticalGradient(listOf(Color(0xFFFFE27A), Color(0xFFE08A00)))
                                    claimed -> Brush.verticalGradient(listOf(Color(0xFF4ADE80), Color(0xFF15803D)))
                                    else -> Brush.verticalGradient(listOf(Color(0xFF5B2A9E), Color(0xFF2A0846)))
                                },
                                RoundedCornerShape(14.dp),
                            )
                            .border(if (isToday) 3.dp else 1.5.dp, if (isToday) Brush.verticalGradient(listOf(Color.White, Color.White)) else Skin.goldBorder, RoundedCornerShape(14.dp))
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("DAY $d", style = outlinedText(12.sp))
                        CoinIcon(Modifier.size(if (d == 7) 40.dp else 30.dp))
                        Text(formatCompact(Economy.dailyBonus(d, player.level)), style = outlinedText(13.sp, if (isToday) Color.White else Skin.GoldLight))
                        if (claimed) Text("✓", style = outlinedText(12.sp, Color.White))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            if (today != null) {
                SkinButton("COLLECT", Modifier.width(220.dp).height(52.dp), tone = SkinTone.Green, textSize = 20.sp) {
                    if (player.claimDaily(System.currentTimeMillis()) > 0) {
                        graph.sound.play(Sfx.BigWin)
                        onClaimed()
                    }
                    onDismiss()
                }
            } else {
                Text("Next bonus in ${countdown(player.lastDailyAt + Economy.DAY_MS - now)}", style = outlinedText(14.sp))
                Spacer(Modifier.height(8.dp))
                SkinButton("OK", Modifier.width(160.dp).height(46.dp), tone = SkinTone.Purple, onClick = onDismiss)
            }
        }
    }
}
