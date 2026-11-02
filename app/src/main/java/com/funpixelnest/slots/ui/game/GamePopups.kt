package com.funpixelnest.slots.ui.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funpixelnest.slots.data.AppGraph
import com.funpixelnest.slots.data.ThemeAssets
import com.funpixelnest.slots.engine.PayLineDef
import com.funpixelnest.slots.engine.ThemeConfig
import com.funpixelnest.slots.engine.WinTier
import com.funpixelnest.slots.ui.common.ArtButton
import com.funpixelnest.slots.ui.common.AssetImage
import com.funpixelnest.slots.ui.common.FallbackPanel
import com.funpixelnest.slots.ui.common.Gold
import com.funpixelnest.slots.ui.common.formatCoins
import com.funpixelnest.slots.ui.common.outlinedText
import com.funpixelnest.slots.ui.common.rememberImage
import com.funpixelnest.slots.ui.common.themeFont
import kotlinx.coroutines.delay

@Composable
fun GamePopups(graph: AppGraph, vm: GameViewModel, assets: ThemeAssets) {
    val ui = assets.config.ui
    val font = themeFont(assets.config.id)
    when (val p = vm.popup) {
        null -> Unit
        is Popup.BigWin -> {
            val (art, title) = when (p.tier) {
                WinTier.HUGE -> (ui["popupHugeWin"] ?: ui["popupBigWin"]) to "HUGE WIN!"
                WinTier.MEGA -> (ui["popupMegaWin"] ?: ui["popupBigWin"]) to "MEGA WIN!"
                else -> (ui["popupBigWin"] ?: ui["popupMegaWin"]) to "BIG WIN!"
            }
            AutoDismiss(3500, vm::dismissPopup)
            PopupFrame(assets, art, vm::dismissPopup, widthFraction = 0.42f) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(title, style = outlinedText(34.sp, Gold, font))
                    CountingText(p.coins, outlinedText(40.sp, Color.White, font))
                }
            }
        }
        is Popup.FreeSpins -> {
            AutoDismiss(3000, vm::dismissPopup)
            PopupFrame(assets, ui["popupFreeSpins"] ?: ui["popupPanel"], vm::dismissPopup, widthFraction = 0.42f) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("FREE SPINS", style = outlinedText(30.sp, Gold, font))
                    Text("${p.count}", style = outlinedText(54.sp, Color.White, font))
                }
            }
        }
        is Popup.LevelUp -> {
            AutoDismiss(3000, vm::dismissPopup)
            PopupFrame(assets, ui["popupPanel"], vm::dismissPopup, widthFraction = 0.4f) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("LEVEL UP!", style = outlinedText(30.sp, Gold, font))
                    Text("Level ${p.level}", style = outlinedText(24.sp))
                    Text("+${formatCoins(p.reward)} coins", style = outlinedText(22.sp, Gold))
                }
            }
        }
        Popup.NoCoins -> PopupFrame(assets, ui["popupPanel"], vm::dismissPopup, widthFraction = 0.45f) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("NOT ENOUGH COINS", style = outlinedText(24.sp, Gold, font))
                Spacer(Modifier.height(6.dp))
                Text("Lower your bet, or collect\nthe free gift in the lobby.", style = outlinedText(16.sp))
                Spacer(Modifier.height(12.dp))
                ArtButton(assets, ui["buttonLong"], Modifier.width(150.dp).height(44.dp), label = "OK") { vm.dismissPopup() }
            }
        }
        Popup.Info -> PayTablePopup(assets, vm.lineBet, vm.session.linesCount, vm::dismissPopup)
        Popup.Settings -> SettingsPopup(graph, assets, ui["popupSettings"], vm::dismissPopup)
    }
}

@Composable
private fun AutoDismiss(ms: Long, onDismiss: () -> Unit) {
    LaunchedEffect(Unit) { delay(ms); onDismiss() }
}

@Composable
private fun CountingText(target: Long, style: androidx.compose.ui.text.TextStyle) {
    var shown by remember { mutableLongStateOf(0L) }
    LaunchedEffect(target) {
        val steps = 40
        for (i in 1..steps) { shown = target * i / steps; delay(30) }
    }
    Text(formatCoins(shown), style = style)
}

/** Dimmed backdrop + themed panel art (or a drawn panel), popping in like Unity's PopUpsController. */
@Composable
fun PopupFrame(
    assets: ThemeAssets,
    art: String?,
    onDismiss: () -> Unit,
    widthFraction: Float = 0.6f,
    heightFraction: Float = 0.62f,
    content: @Composable BoxScope.() -> Unit,
) {
    val scale = remember { Animatable(0.6f) }
    LaunchedEffect(Unit) { scale.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow)) }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(remember { MutableInteractionSource() }, null, onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        val img = rememberImage(assets, art)
        val box = Modifier.fillMaxWidth(widthFraction).fillMaxHeight(heightFraction).scale(scale.value)
            .clickable(remember { MutableInteractionSource() }, null) { /* swallow */ }
        if (img != null) {
            Box(box, contentAlignment = Alignment.Center) {
                AssetImage(assets, art, Modifier.fillMaxSize(), ContentScale.FillBounds)
                Box(Modifier.fillMaxSize().padding(28.dp), contentAlignment = Alignment.Center, content = content)
            }
        } else {
            FallbackPanel(box, content)
        }
    }
}

@Composable
private fun PayTablePopup(assets: ThemeAssets, lineBet: Int, lines: Int, onDismiss: () -> Unit) {
    val cfg = assets.config
    PopupFrame(assets, cfg.ui["popupInfo"], onDismiss, widthFraction = 0.86f, heightFraction = 0.86f) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("PAYTABLE", style = outlinedText(22.sp, Gold, themeFont(cfg.id)))
            Text(
                "$lines ways • pays left to right • line bet $lineBet",
                style = outlinedText(12.sp, Color(0xFFE0E0E0)),
            )
            Spacer(Modifier.height(6.dp))
            val rows = payRows(cfg)
            LazyVerticalGrid(GridCells.Fixed(4), Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(rows) { r ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AssetImage(assets, cfg.symbols[r.symbol].image, Modifier.size(54.dp))
                        Column(Modifier.padding(start = 4.dp)) {
                            r.pays.forEach { (count, pay) ->
                                Text("${count}× ${formatCoins(pay.toLong() * lineBet)}", style = outlinedText(12.sp))
                            }
                        }
                    }
                }
            }
            val rules = buildList {
                if (cfg.wildId >= 0) add("WILD substitutes for all symbols except scatter (not on reel 1).")
                cfg.scatterPays.forEach { s ->
                    add("${s.count} SCATTERS anywhere: " + listOfNotNull(
                        s.freeSpins.takeIf { it > 0 }?.let { "$it free spins" },
                        s.pay.takeIf { it > 0 }?.let { "${formatCoins(it.toLong() * lineBet)} coins" },
                    ).joinToString(" + "))
                }
            }
            rules.forEach { Text(it, style = outlinedText(12.sp, Gold)) }
        }
    }
}

private data class PayRow(val symbol: Int, val pays: List<Pair<Int, Int>>)

/** "N of a kind" pays per symbol from the base pay table (left-aligned runs of one symbol). */
private fun payRows(cfg: ThemeConfig): List<PayRow> {
    val bySymbol = HashMap<Int, MutableList<Pair<Int, Int>>>()
    for (p: PayLineDef in cfg.payTable) {
        val s = p.line.firstOrNull() ?: continue
        if (s < 0) continue
        val run = p.line.takeWhile { it == s }.size
        if (p.line.drop(run).any { it >= 0 }) continue
        bySymbol.getOrPut(s) { ArrayList() }.add(run to p.pay)
    }
    return bySymbol.entries.sortedByDescending { e -> e.value.maxOf { it.second } }
        .map { PayRow(it.key, it.value.sortedByDescending { p -> p.first }) }
}

/** Sound / music toggles; used in the lobby (common art) and in every machine (theme art). */
@Composable
fun SettingsPopup(graph: AppGraph, assets: ThemeAssets, art: String?, onDismiss: () -> Unit) {
    var sound by remember { mutableStateOf(graph.player.soundOn) }
    var music by remember { mutableStateOf(graph.player.musicOn) }
    val panel = art ?: if (assets.config.id == "common") "lobby/settings_pop_up" else null
    PopupFrame(assets, panel, onDismiss, widthFraction = 0.4f, heightFraction = 0.7f) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("SETTINGS", style = outlinedText(24.sp, Gold))
            Toggle(graph, "Sound", sound) { sound = it; graph.sound.setSound(it) }
            Toggle(graph, "Music", music) { music = it; graph.sound.setMusic(it) }
            Text("Converted from Unity – FunPixelNest", style = outlinedText(10.sp, Color(0xFFBDBDBD)))
        }
    }
}

@Composable
private fun Toggle(graph: AppGraph, label: String, on: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.clickable { onChange(!on) }.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = outlinedText(20.sp), modifier = Modifier.width(90.dp))
        AssetImage(
            graph.commonImages, if (on) "lobby/radio_button_on" else "lobby/radio_button_off",
            Modifier.size(36.dp),
        )
        Text(if (on) "ON" else "OFF", style = outlinedText(16.sp, if (on) Gold else Color.Gray), modifier = Modifier.padding(start = 6.dp))
    }
}
