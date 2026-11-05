package com.funpixelnest.slots.ui.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.funpixelnest.slots.ui.common.CoinIcon
import com.funpixelnest.slots.ui.common.formatCompact
import com.funpixelnest.slots.ui.skin.Skin
import com.funpixelnest.slots.ui.skin.SkinButton
import com.funpixelnest.slots.ui.skin.SkinDialog
import com.funpixelnest.slots.ui.skin.SkinTone
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
import androidx.compose.foundation.layout.aspectRatio
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
import com.funpixelnest.slots.engine.Features
import com.funpixelnest.slots.engine.PayLineDef
import com.funpixelnest.slots.engine.PlayerRules
import com.funpixelnest.slots.engine.ThemeConfig
import com.funpixelnest.slots.engine.WinTier
import com.funpixelnest.slots.ui.common.AssetImage
import com.funpixelnest.slots.ui.common.formatCoins
import com.funpixelnest.slots.ui.common.outlinedText
import com.funpixelnest.slots.ui.common.themeFont
import kotlinx.coroutines.delay

@Composable
fun GamePopups(graph: AppGraph, vm: GameViewModel, assets: ThemeAssets) {
    val font = themeFont(assets.config.id)
    when (val p = vm.popup) {
        null -> Unit
        is Popup.BigWin -> {
            val title = when (p.tier) {
                WinTier.HUGE -> "HUGE WIN!"
                WinTier.MEGA -> "MEGA WIN!"
                else -> "BIG WIN!"
            }
            AutoDismiss(3500, vm::dismissPopup)
            PopupFrame(title, vm::dismissPopup, tone = SkinTone.Gold, sunburst = true) {
                CoinIcon(Modifier.size(56.dp))
                CountingText(p.coins, outlinedText(44.sp, Skin.GoldLight, font))
                Text("COINS", style = outlinedText(14.sp, Skin.TextSoft).copy(letterSpacing = 3.sp))
            }
        }
        is Popup.Jackpot -> {
            AutoDismiss(4500, vm::dismissPopup)
            PopupFrame("${p.win.tier.label} JACKPOT", vm::dismissPopup, tone = SkinTone.Red, sunburst = true) {
                Text("CONGRATULATIONS!", style = outlinedText(18.sp, Skin.TextSoft))
                CoinIcon(Modifier.size(64.dp))
                CountingText(p.win.coins, outlinedText(44.sp, Skin.GoldLight, font))
            }
        }
        is Popup.FreeSpins -> {
            AutoDismiss(3000, vm::dismissPopup)
            PopupFrame("FREE SPINS", vm::dismissPopup, tone = SkinTone.Green, sunburst = true) {
                Text("${p.count}", style = outlinedText(64.sp, Color.White, font))
                Text("FREE GAMES WON", style = outlinedText(16.sp, Skin.GoldLight))
            }
        }
        is Popup.LevelUp -> {
            AutoDismiss(3000, vm::dismissPopup)
            PopupFrame("LEVEL UP!", vm::dismissPopup, tone = SkinTone.Purple, sunburst = true) {
                Text("LEVEL ${p.level}", style = outlinedText(36.sp, Color.White))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoinIcon(Modifier.size(28.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("+${formatCoins(p.reward)}", style = outlinedText(24.sp, Skin.GoldLight))
                }
                if (p.betUnlocked) Text("Higher bets unlocked!", style = outlinedText(15.sp, Color(0xFFFFCC80)))
                p.machines.forEach { name ->
                    Text("NEW MACHINE: ${name.uppercase()}", style = outlinedText(15.sp, Color(0xFF7CF29A)))
                }
            }
        }
        Popup.NoCoins -> PopupFrame("OUT OF COINS", vm::dismissPopup, tone = SkinTone.Red) {
            Text("Here are some free coins\nto keep playing!", style = outlinedText(17.sp))
            Spacer(Modifier.height(4.dp))
            SkinButton(
                "GET ${formatCompact(PlayerRules.START_COINS)}", Modifier.width(220.dp).height(52.dp),
                tone = SkinTone.Green, textSize = 20.sp,
            ) {
                vm.addCoins(PlayerRules.START_COINS)
                vm.dismissPopup()
            }
        }
        Popup.Info -> PayTablePopup(assets, vm.lineBet, vm.session.linesCount, vm::dismissPopup)
        Popup.Settings -> SettingsPopup(graph, assets, null, vm::dismissPopup)
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

/** Dimmed backdrop + skin dialog that pops in with a spring. */
@Composable
fun PopupFrame(
    title: String,
    onDismiss: () -> Unit,
    tone: SkinTone = SkinTone.Red,
    sunburst: Boolean = false,
    widthFraction: Float = 0.9f,
    heightFraction: Float? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scale = remember { Animatable(0.6f) }
    LaunchedEffect(Unit) { scale.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow)) }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(remember { MutableInteractionSource() }, null, onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        SkinDialog(
            title,
            Modifier.fillMaxWidth(widthFraction)
                .let { if (heightFraction != null) it.fillMaxHeight(heightFraction) else it }
                .scale(scale.value)
                .clickable(remember { MutableInteractionSource() }, null) { /* swallow */ },
            tone = tone, sunburst = sunburst,
        ) {
            Column(
                Modifier.then(if (heightFraction != null) Modifier.fillMaxHeight() else Modifier),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = content,
            )
        }
    }
}

@Composable
private fun PayTablePopup(assets: ThemeAssets, lineBet: Int, lines: Int, onDismiss: () -> Unit) {
    val cfg = assets.config
    PopupFrame("PAYTABLE", onDismiss, tone = SkinTone.Purple, widthFraction = 0.94f, heightFraction = 0.82f) {
        Text("$lines ways · pays left to right · line bet ${formatCoins(lineBet.toLong())}", style = outlinedText(12.sp, Skin.TextSoft))
        Features.forTheme(cfg.id)?.let { f ->
            Column(
                Modifier.fillMaxWidth().background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp)).padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("★ ${f.title.uppercase()} ★", style = outlinedText(15.sp, Color(0xFF7CF29A)))
                Text(f.blurb, style = outlinedText(12.sp, Skin.TextSoft))
            }
        }
        val rows = payRows(cfg)
        LazyVerticalGrid(
            GridCells.Fixed(2), Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(rows) { r ->
                Row(
                    Modifier.background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp)).padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AssetImage(assets, cfg.symbols[r.symbol].image, Modifier.size(58.dp))
                    Column(Modifier.padding(start = 6.dp)) {
                        r.pays.forEach { (count, pay) ->
                            Row {
                                Text("$count× ", style = outlinedText(13.sp, Skin.TextSoft))
                                Text(formatCompact(pay.toLong() * lineBet), style = outlinedText(13.sp, Skin.GoldLight))
                            }
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
        rules.forEach { Text(it, style = outlinedText(12.sp, Skin.GoldLight)) }
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

/** Sound / music toggles; used in the lobby and in every machine. */
@Composable
fun SettingsPopup(graph: AppGraph, @Suppress("UNUSED_PARAMETER") assets: ThemeAssets, @Suppress("UNUSED_PARAMETER") art: String?, onDismiss: () -> Unit) {
    var sound by remember { mutableStateOf(graph.player.soundOn) }
    var music by remember { mutableStateOf(graph.player.musicOn) }
    PopupFrame("SETTINGS", onDismiss, tone = SkinTone.Purple, widthFraction = 0.82f) {
        Toggle("Sound", sound) { sound = it; graph.sound.setSound(it) }
        Toggle("Music", music) { music = it; graph.sound.setMusic(it) }
        Spacer(Modifier.height(4.dp))
        SkinButton("DONE", Modifier.width(160.dp).height(46.dp), tone = SkinTone.Green, onClick = onDismiss)
        Text("FunPixelNest", style = outlinedText(10.sp, Skin.TextSoft))
    }
}

/** Pill switch in the skin colours. */
@Composable
private fun Toggle(label: String, on: Boolean, onChange: (Boolean) -> Unit) {
    val knob by animateFloatAsState(if (on) 1f else 0f, label = "knob")
    Row(
        Modifier.clickable { onChange(!on) }.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = outlinedText(20.sp), modifier = Modifier.width(96.dp))
        Box(
            Modifier.width(64.dp).height(32.dp)
                .background(Skin.goldBorder, RoundedCornerShape(50))
                .padding(2.dp)
                .background(if (on) Color(0xFF16A34A) else Color(0xFF3A1B5C), RoundedCornerShape(50))
                .padding(3.dp),
        ) {
            Box(
                Modifier.offset(x = 32.dp * knob).size(26.dp)
                    .background(Skin.goldFill, CircleShape)
            )
        }
        Text(if (on) "ON" else "OFF", style = outlinedText(15.sp, if (on) Skin.GoldLight else Color.Gray), modifier = Modifier.padding(start = 8.dp))
    }
}
