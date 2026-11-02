package com.funpixelnest.slots.ui.lobby

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.scale
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
import com.funpixelnest.slots.ui.common.AssetImage
import com.funpixelnest.slots.ui.common.CoinIcon
import com.funpixelnest.slots.ui.common.CoinShower
import com.funpixelnest.slots.ui.common.formatCompact
import com.funpixelnest.slots.ui.common.outlinedText
import com.funpixelnest.slots.ui.game.SettingsPopup
import com.funpixelnest.slots.ui.skin.CoinCapsule
import com.funpixelnest.slots.ui.skin.GoldFrame
import com.funpixelnest.slots.ui.skin.IconRoundButton
import com.funpixelnest.slots.ui.skin.LevelStar
import com.funpixelnest.slots.ui.skin.Ribbon
import com.funpixelnest.slots.ui.skin.Skin
import com.funpixelnest.slots.ui.skin.SkinButton
import com.funpixelnest.slots.ui.skin.SkinDialog
import com.funpixelnest.slots.ui.skin.SkinIcon
import com.funpixelnest.slots.ui.skin.SkinTone
import com.funpixelnest.slots.ui.skin.Sunburst
import com.funpixelnest.slots.ui.skin.VelvetBackdrop
import kotlinx.coroutines.delay

@Composable
fun LobbyScreen(
    graph: AppGraph,
    onOpenTheme: (String) -> Unit,
    /** Screenshot hook: open the daily bonus popup straight away. */
    startWithDailyBonus: Boolean = false,
) {
    val coins by graph.player.coinsFlow.collectAsState()
    val level by graph.player.levelFlow.collectAsState()
    val progress by graph.player.progressFlow.collectAsState()
    var showSettings by remember { mutableStateOf(false) }
    var showDaily by remember { mutableStateOf(startWithDailyBonus) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var shower by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        graph.sound.startMusic()
        // offer the daily bonus once when it is ready
        if (graph.player.nextDailyDay(now) != null) showDaily = true
        while (true) { delay(1000); now = System.currentTimeMillis() }
    }

    Box(Modifier.fillMaxSize().background(Skin.VelvetDeep).testTag("lobby")) {
        VelvetBackdrop(Modifier.fillMaxSize())

        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            // ---------------- HUD: level · coins · settings
            Row(
                Modifier.fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xE6170327), Color(0x99170327))))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LevelStar(level, progress)
                Spacer(Modifier.weight(1f))
                CoinCapsule(coins, onPlus = { graph.sound.play(Sfx.Button); showDaily = true })
                Spacer(Modifier.width(8.dp))
                IconRoundButton(SkinIcon.Gear) { graph.sound.play(Sfx.Button); showSettings = true }
            }
            Box(Modifier.fillMaxWidth().height(2.dp).background(Skin.goldBorder))

            LazyVerticalGrid(
                GridCells.Fixed(2),
                Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 6.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) { Brand() }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    BonusRow(graph, now, onHourly = { shower++ }, onDaily = { showDaily = true })
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(Modifier.fillMaxWidth().padding(top = 4.dp), contentAlignment = Alignment.Center) {
                        Ribbon("ALL GAMES · ${graph.themes.size}", tone = SkinTone.Purple, textSize = 16.sp)
                    }
                }
                items(graph.themes, key = { it.id }) { entry ->
                    MachineCard(graph, entry) {
                        graph.sound.play(Sfx.Choose)
                        onOpenTheme(entry.id)
                    }
                }
            }
        }

        if (showSettings) SettingsPopup(graph, graph.commonImages, null) { showSettings = false }
        CoinShower(shower, 60, Modifier.fillMaxSize())
        if (showDaily) DailyBonusPopup(graph, now, onClaimed = { shower++ }) { showDaily = false }
    }
}

/** Brand header over a turning sunburst. */
@Composable
private fun Brand() {
    Box(Modifier.fillMaxWidth().height(118.dp), contentAlignment = Alignment.Center) {
        Sunburst(Modifier.fillMaxSize())
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("FUNPIXEL", style = outlinedText(16.sp, Skin.TextSoft).copy(letterSpacing = 6.sp))
            Text("SLOTS", style = outlinedText(52.sp, Skin.GoldLight).copy(letterSpacing = 4.sp))
            Text("EVERY MACHINE UNLOCKED", style = outlinedText(11.sp, Skin.Gold).copy(letterSpacing = 2.sp))
        }
    }
}

private fun countdown(ms: Long): String {
    val s = ms / 1000
    return "%d:%02d:%02d".format(s / 3600, s / 60 % 60, s % 60)
}

/** Hourly and daily bonus side by side, each in its own velvet plate. */
@Composable
private fun BonusRow(graph: AppGraph, now: Long, onHourly: () -> Unit, onDaily: () -> Unit) {
    val player = graph.player
    val hourlyLeft = player.hourlyReadyIn(now)
    val dailyReady = player.nextDailyDay(now) != null
    val pulse by rememberInfiniteTransition(label = "bonus").animateFloat(
        1f, 1.05f, infiniteRepeatable(tween(650), RepeatMode.Reverse), label = "pulse",
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        BonusPlate("HOURLY BONUS", formatCompact(Economy.hourlyBonus(player.level)), Modifier.weight(1f)) {
            val ready = hourlyLeft == 0L
            SkinButton(
                if (ready) "COLLECT" else countdown(hourlyLeft),
                Modifier.fillMaxWidth().height(40.dp).scale(if (ready) pulse else 1f),
                tone = if (ready) SkinTone.Green else SkinTone.Purple, enabled = ready, textSize = 15.sp,
            ) {
                if (player.claimHourly(System.currentTimeMillis()) > 0) {
                    graph.sound.play(Sfx.WinCoins)
                    onHourly()
                }
            }
        }
        val day = player.nextDailyDay(now) ?: player.lastDailyDay
        BonusPlate("DAILY BONUS", "DAY $day / 7", Modifier.weight(1f)) {
            SkinButton(
                if (dailyReady) "OPEN" else "CALENDAR",
                Modifier.fillMaxWidth().height(40.dp).scale(if (dailyReady) pulse else 1f),
                tone = if (dailyReady) SkinTone.Gold else SkinTone.Purple, textSize = 15.sp,
            ) { graph.sound.play(Sfx.Button); onDaily() }
        }
    }
}

@Composable
private fun BonusPlate(title: String, value: String, modifier: Modifier, button: @Composable () -> Unit) {
    Column(
        modifier
            .background(Skin.goldBorder, RoundedCornerShape(16.dp))
            .padding(2.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF34105A), Color(0xFF1A0530))), RoundedCornerShape(14.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(title, style = outlinedText(11.sp, Skin.TextSoft).copy(letterSpacing = 1.sp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            CoinIcon(Modifier.size(22.dp))
            Spacer(Modifier.width(6.dp))
            Text(value, style = outlinedText(18.sp, Skin.GoldLight))
        }
        button()
    }
}

/** Gold-framed machine card with a HOT tag and a Grand-jackpot plate under it. */
@Composable
private fun MachineCard(graph: AppGraph, entry: ThemeIndexEntry, onClick: () -> Unit) {
    val grand = Economy.LINE_BETS[0].toLong() * 243 * JackpotTier.GRAND.betMultiple +
        graph.player.jackpotPool(entry.id, JackpotTier.GRAND)
    Column(
        Modifier.clickable(remember { MutableInteractionSource() }, null, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            GoldFrame(Modifier.fillMaxWidth().aspectRatio(392f / 588f), corner = 18.dp) {
                AssetImage(graph.commonImages, entry.tile, Modifier.fillMaxSize(), ContentScale.Crop)
            }
            if (entry.order < 3) {
                Ribbon("HOT", Modifier.align(Alignment.TopEnd).offset(x = 4.dp, y = (-6).dp), tone = SkinTone.Red, textSize = 12.sp)
            }
        }
        Row(
            Modifier.offset(y = (-12).dp)
                .background(Skin.goldBorder, RoundedCornerShape(50))
                .padding(2.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF2A0846), Color(0xFF0E0219))), RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("GRAND ", style = outlinedText(10.sp, Skin.TextSoft))
            Text(formatCompact(grand), style = outlinedText(14.sp, Skin.GoldLight))
        }
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
