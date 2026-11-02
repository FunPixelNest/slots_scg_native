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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
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
import com.funpixelnest.slots.engine.ThemeIndexEntry
import com.funpixelnest.slots.ui.common.ArtButton
import com.funpixelnest.slots.ui.common.AssetImage
import com.funpixelnest.slots.ui.common.CoinIcon
import com.funpixelnest.slots.ui.common.CoinShower
import com.funpixelnest.slots.ui.common.Gold
import com.funpixelnest.slots.ui.common.formatCompact
import com.funpixelnest.slots.ui.common.outlinedText
import com.funpixelnest.slots.ui.game.SettingsPopup
import kotlinx.coroutines.delay

@Composable
fun LobbyScreen(
    graph: AppGraph,
    onOpenTheme: (String) -> Unit,
    /** Screenshot hook: open the daily bonus popup straight away. */
    startWithDailyBonus: Boolean = false,
) {
    val common = graph.commonImages
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

    Box(Modifier.fillMaxSize().background(Color.Black).testTag("lobby")) {
        AssetImage(common, "lobby/bkg", Modifier.fillMaxSize(), ContentScale.Crop)

        Column(Modifier.fillMaxSize()) {
            // ---------------- top panel
            Box(Modifier.fillMaxWidth().height(58.dp)) {
                AssetImage(common, "lobby/up_pannel", Modifier.fillMaxSize(), ContentScale.FillBounds)
                Row(
                    Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LevelBadge(level, progress)
                    Spacer(Modifier.width(14.dp))
                    CoinsBox(coins)
                    Spacer(Modifier.weight(1f))
                    HourlyBonusButton(graph, now) { shower++ }
                    Spacer(Modifier.width(10.dp))
                    DailyBonusButton(graph, now) { showDaily = true }
                    Spacer(Modifier.width(10.dp))
                    ArtButton(common, "lobby/menu_button_normal", Modifier.size(44.dp), label = "⚙", labelSize = 18.sp) {
                        graph.sound.play(Sfx.Button); showSettings = true
                    }
                }
            }

            AssetImage(common, "lobby/lobby_title", Modifier.fillMaxWidth().height(52.dp).padding(top = 6.dp))

            // ---------------- machines (every one unlocked)
            LazyRow(
                Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(graph.themes, key = { it.id }) { entry ->
                    ThemeCard(graph, entry) {
                        graph.sound.play(Sfx.Choose)
                        onOpenTheme(entry.id)
                    }
                }
            }
        }

        if (showSettings) SettingsPopup(graph, common, null) { showSettings = false }
        CoinShower(shower, 60, Modifier.fillMaxSize())
        if (showDaily) DailyBonusPopup(graph, now, onClaimed = { shower++ }) { showDaily = false }
    }
}

@Composable
private fun ThemeCard(graph: AppGraph, entry: ThemeIndexEntry, onClick: () -> Unit) {
    val common = graph.commonImages
    Box(
        Modifier
            .fillMaxHeight()
            .aspectRatio(392f / 588f)
            .clip(RoundedCornerShape(18.dp))
            .border(2.dp, Gold.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
    ) {
        AssetImage(common, entry.tile, Modifier.fillMaxSize(), ContentScale.Crop)
        if (entry.order < 3) {
            AssetImage(common, "lobby/hot", Modifier.align(Alignment.TopEnd).padding(6.dp).width(64.dp).height(22.dp))
        }
        // round play marker in the corner, clear of the theme name painted on the art
        Box(
            Modifier.align(Alignment.TopStart).padding(8.dp).size(34.dp)
                .background(Brush.radialGradient(listOf(Color(0xFF66BB6A), Color(0xFF1B5E20))), RoundedCornerShape(50))
                .border(2.dp, Gold, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center,
        ) { Text("▶", style = outlinedText(14.sp)) }
    }
}

@Composable
fun LoadBar(graph: AppGraph, progress: Float, modifier: Modifier) {
    val common = graph.commonImages
    Box(modifier) {
        AssetImage(common, "lobby/load_bar_empty", Modifier.fillMaxSize(), ContentScale.FillBounds)
        AssetImage(
            common, "lobby/load_bar_full",
            Modifier.fillMaxHeight().fillMaxWidth(progress.coerceIn(0f, 1f)), ContentScale.FillBounds,
        )
    }
}

/** Balance HUD: gold coin + compact amount (1.25M), rolling up when it changes. */
@Composable
fun CoinsBox(coins: Long) {
    var shown by remember { mutableLongStateOf(coins) }
    LaunchedEffect(coins) {
        val from = shown
        for (i in 1..20) { shown = from + (coins - from) * i / 20; delay(30) }
    }
    Row(
        Modifier
            .background(Brush.verticalGradient(listOf(Color(0xCC1A0B2E), Color(0xCC000000))), RoundedCornerShape(50))
            .border(2.dp, Gold, RoundedCornerShape(50))
            .padding(start = 4.dp, end = 16.dp, top = 3.dp, bottom = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoinIcon(Modifier.size(28.dp))
        Spacer(Modifier.width(8.dp))
        Text(formatCompact(shown), style = outlinedText(20.sp, Gold), modifier = Modifier.testTag("coins"))
    }
}

@Composable
fun LevelBadge(level: Int, progress: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(36.dp)
                .background(Brush.radialGradient(listOf(Color(0xFF7E57C2), Color(0xFF311B92))), RoundedCornerShape(50))
                .border(2.dp, Gold, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center,
        ) {
            Text("$level", style = outlinedText(15.sp))
        }
        Column(Modifier.padding(start = 6.dp)) {
            Text("LEVEL $level", style = outlinedText(9.sp, Color(0xFFE0E0E0)))
            Box(
                Modifier.width(96.dp).height(10.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(50))
                    .border(1.dp, Color(0x88FFFFFF), RoundedCornerShape(50))
            ) {
                Box(
                    Modifier.fillMaxHeight().fillMaxWidth((progress / 100f).coerceIn(0f, 1f))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF9CCC65), Color(0xFF43A047))), RoundedCornerShape(50))
                )
            }
        }
    }
}

private fun countdown(ms: Long): String {
    val s = ms / 1000
    return "%d:%02d:%02d".format(s / 3600, s / 60 % 60, s % 60)
}

/** Free coins every hour (amount grows with level). */
@Composable
private fun HourlyBonusButton(graph: AppGraph, now: Long, onClaimed: () -> Unit) {
    val left = graph.player.hourlyReadyIn(now)
    val ready = left == 0L
    val pulse by rememberInfiniteTransition(label = "hourly").animateFloat(
        1f, if (ready) 1.07f else 1f, infiniteRepeatable(tween(600), RepeatMode.Reverse), label = "pulse",
    )
    ArtButton(
        graph.commonImages, "lobby/long_button_normal",
        Modifier.width(176.dp).height(44.dp).scale(pulse),
        label = if (ready) "HOURLY ${formatCompact(Economy.hourlyBonus(graph.player.level))}" else "HOURLY ${countdown(left)}",
        labelSize = 13.sp, enabled = ready,
    ) {
        if (graph.player.claimHourly(System.currentTimeMillis()) > 0) {
            graph.sound.play(Sfx.WinCoins)
            onClaimed()
        }
    }
}

@Composable
private fun DailyBonusButton(graph: AppGraph, now: Long, onOpen: () -> Unit) {
    val ready = graph.player.nextDailyDay(now) != null
    ArtButton(
        graph.commonImages, "lobby/long_button_normal", Modifier.width(120.dp).height(44.dp),
        label = if (ready) "DAILY!" else "DAILY ✓", labelSize = 13.sp,
    ) { graph.sound.play(Sfx.Button); onOpen() }
}

/** Seven-day streak calendar; day 7 pays the most, missing a day restarts at day 1. */
@Composable
private fun DailyBonusPopup(graph: AppGraph, now: Long, onClaimed: () -> Unit, onDismiss: () -> Unit) {
    val player = graph.player
    val today = player.nextDailyDay(now)
    val streakDay = today ?: player.lastDailyDay
    Box(
        Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.65f)).clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.fillMaxWidth(0.82f)
                .background(Brush.verticalGradient(listOf(Color(0xFF4A148C), Color(0xFF1A0533))), RoundedCornerShape(24.dp))
                .border(3.dp, Brush.verticalGradient(listOf(Gold, Color(0xFFB8860B))), RoundedCornerShape(24.dp))
                .clickable(remember { MutableInteractionSource() }, null) { /* swallow taps */ }
                .padding(18.dp)
                .testTag("daily-bonus"),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("DAILY BONUS", style = outlinedText(26.sp, Gold))
            Text("Come back every day – day 7 pays the most!", style = outlinedText(12.sp, Color(0xFFE0E0E0)))
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (d in 1..Economy.DAILY_DAYS) {
                    val claimed = (today == null && d <= streakDay) || (today != null && d < today)
                    val isToday = d == today
                    Column(
                        Modifier.weight(1f).height(if (d == 7) 120.dp else 104.dp)
                            .background(
                                when {
                                    isToday -> Brush.verticalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF8F00)))
                                    claimed -> Brush.verticalGradient(listOf(Color(0xFF43A047), Color(0xFF1B5E20)))
                                    else -> Brush.verticalGradient(listOf(Color(0xFF5E35B1), Color(0xFF311B92)))
                                },
                                RoundedCornerShape(14.dp),
                            )
                            .border(if (isToday) 3.dp else 1.dp, if (isToday) Color.White else Color(0x66FFFFFF), RoundedCornerShape(14.dp))
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("DAY $d", style = outlinedText(12.sp))
                        CoinIcon(Modifier.size(if (d == 7) 40.dp else 30.dp))
                        Text(formatCompact(Economy.dailyBonus(d, player.level)), style = outlinedText(13.sp, if (isToday) Color.White else Gold))
                        if (claimed) Text("✓", style = outlinedText(12.sp, Color.White))
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            if (today != null) {
                ArtButton(graph.commonImages, "lobby/long_button_normal", Modifier.width(220.dp).height(50.dp), label = "COLLECT", labelSize = 18.sp) {
                    if (player.claimDaily(System.currentTimeMillis()) > 0) {
                        graph.sound.play(Sfx.BigWin)
                        onClaimed()
                    }
                    onDismiss()
                }
            } else {
                Text("Next bonus in ${countdown(player.lastDailyAt + Economy.DAY_MS - now)}", style = outlinedText(14.sp))
            }
        }
    }
}
