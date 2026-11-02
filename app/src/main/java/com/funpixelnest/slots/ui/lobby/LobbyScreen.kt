package com.funpixelnest.slots.ui.lobby

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funpixelnest.slots.audio.Sfx
import com.funpixelnest.slots.data.AppGraph
import com.funpixelnest.slots.data.PackState
import com.funpixelnest.slots.data.PrefsPlayerStore
import com.funpixelnest.slots.engine.ThemeIndexEntry
import com.funpixelnest.slots.ui.common.ArtButton
import com.funpixelnest.slots.ui.common.AssetImage
import com.funpixelnest.slots.ui.common.Gold
import com.funpixelnest.slots.ui.common.formatCoins
import com.funpixelnest.slots.ui.common.outlinedText
import com.funpixelnest.slots.ui.game.SettingsPopup
import kotlinx.coroutines.delay

@Composable
fun LobbyScreen(graph: AppGraph, onOpenTheme: (String) -> Unit, onConfirmCellular: () -> Unit) {
    val common = graph.commonImages
    val coins by graph.player.coinsFlow.collectAsState()
    val level by graph.player.levelFlow.collectAsState()
    val packStates by graph.packs.states.collectAsState()
    var showSettings by remember { mutableStateOf(false) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        graph.packs.refresh(graph.themes)
        graph.sound.startMusic()
        while (true) { delay(1000); now = System.currentTimeMillis() }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AssetImage(common, "lobby/bkg", Modifier.fillMaxSize(), ContentScale.Crop)

        Column(Modifier.fillMaxSize()) {
            // ---------------- top panel
            Box(Modifier.fillMaxWidth().height(56.dp)) {
                AssetImage(common, "lobby/up_pannel", Modifier.fillMaxSize(), ContentScale.FillBounds)
                Row(
                    Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LevelBadge(level, graph.player.levelProgress)
                    Spacer(Modifier.width(16.dp))
                    CoinsBox(coins)
                    Spacer(Modifier.weight(1f))
                    GiftButton(graph, now)
                    Spacer(Modifier.width(12.dp))
                    ArtButton(common, "lobby/menu_button_normal", Modifier.size(44.dp), label = "⚙", labelSize = 18.sp) {
                        graph.sound.play(Sfx.Button); showSettings = true
                    }
                }
            }

            AssetImage(common, "lobby/lobby_title", Modifier.fillMaxWidth().height(56.dp).padding(top = 6.dp))

            // ---------------- machines
            LazyRow(
                Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(graph.themes, key = { it.id }) { entry ->
                    ThemeCard(graph, entry, packStates[entry.pack]) {
                        graph.sound.play(Sfx.Choose)
                        when (packStates[entry.pack] ?: PackState.NotDownloaded) {
                            PackState.Ready -> onOpenTheme(entry.id)
                            PackState.WaitingForWifi -> onConfirmCellular()
                            is PackState.Downloading -> Unit
                            else -> graph.packs.download(entry)
                        }
                    }
                }
            }
        }

        if (showSettings) SettingsPopup(graph, common, null) { showSettings = false }
    }
}

@Composable
private fun ThemeCard(graph: AppGraph, entry: ThemeIndexEntry, state: PackState?, onClick: () -> Unit) {
    val common = graph.commonImages
    Box(
        Modifier
            .fillMaxHeight()
            .aspectRatio(392f / 588f)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
    ) {
        AssetImage(common, entry.tile, Modifier.fillMaxSize(), ContentScale.Crop)
        if (entry.order < 3) {
            AssetImage(common, "lobby/hot", Modifier.align(Alignment.TopEnd).padding(6.dp).width(64.dp).height(22.dp))
        }
        when (state) {
            PackState.Ready, null -> Unit
            is PackState.Downloading -> DownloadOverlay(graph, "Downloading…", state.progress)
            PackState.WaitingForWifi -> DownloadOverlay(graph, "Tap to download on mobile data", null)
            is PackState.Failed -> DownloadOverlay(graph, "Download failed – tap to retry", null)
            PackState.NotDownloaded -> DownloadOverlay(
                graph, "Tap to download\n${"%.0f".format(entry.sizeBytes / 1_000_000.0)} MB", null,
            )
        }
    }
}

@Composable
private fun DownloadOverlay(graph: AppGraph, text: String, progress: Float?) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⬇", style = outlinedText(34.sp, Gold))
            Text(text, style = outlinedText(13.sp))
            if (progress != null) {
                Spacer(Modifier.height(8.dp))
                LoadBar(graph, progress, Modifier.width(120.dp).height(20.dp))
            }
        }
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

@Composable
fun CoinsBox(coins: Long) {
    Row(
        Modifier.background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(50)).padding(horizontal = 14.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("🪙", fontSize = 16.sp)
        Spacer(Modifier.width(6.dp))
        Text(formatCoins(coins), style = outlinedText(18.sp, Gold))
    }
}

@Composable
fun LevelBadge(level: Int, progress: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(34.dp).background(Gold, RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
            Text("$level", style = outlinedText(14.sp))
        }
        Box(
            Modifier.padding(start = 6.dp).width(90.dp).height(10.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
        ) {
            Box(
                Modifier.fillMaxHeight().fillMaxWidth((progress / 100f).coerceIn(0f, 1f))
                    .background(Color(0xFF66BB6A), RoundedCornerShape(50))
            )
        }
    }
}

@Composable
private fun GiftButton(graph: AppGraph, now: Long) {
    val readyAt = graph.player.lastGiftAt + PrefsPlayerStore.GIFT_INTERVAL_MS
    val ready = now >= readyAt
    val pulse by rememberInfiniteTransition(label = "gift").animateFloat(
        1f, if (ready) 1.08f else 1f, infiniteRepeatable(tween(600), RepeatMode.Reverse), label = "pulse",
    )
    val label = if (ready) "🎁 FREE ${formatCoins(PrefsPlayerStore.GIFT_COINS)}" else {
        val left = (readyAt - now) / 1000
        "🎁 %d:%02d:%02d".format(left / 3600, left / 60 % 60, left % 60)
    }
    ArtButton(
        graph.commonImages, "lobby/long_button_normal",
        Modifier.width(170.dp).height(42.dp).scale(pulse), label = label, labelSize = 13.sp, enabled = ready,
    ) {
        graph.player.lastGiftAt = System.currentTimeMillis()
        graph.player.coins += PrefsPlayerStore.GIFT_COINS
        graph.sound.play(Sfx.WinCoins)
    }
}
