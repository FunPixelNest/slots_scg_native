package com.funpixelnest.slots.ui.flow

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funpixelnest.slots.audio.Sfx
import com.funpixelnest.slots.data.AppGraph
import com.funpixelnest.slots.engine.CoinPack
import com.funpixelnest.slots.engine.Quest
import com.funpixelnest.slots.engine.Quests
import com.funpixelnest.slots.engine.Rewards
import com.funpixelnest.slots.engine.Store
import com.funpixelnest.slots.ui.common.CoinIcon
import com.funpixelnest.slots.ui.common.CoinShower
import com.funpixelnest.slots.ui.common.formatCoins
import com.funpixelnest.slots.ui.common.formatCompact
import com.funpixelnest.slots.ui.common.outlinedText
import com.funpixelnest.slots.ui.skin.IconRoundButton
import com.funpixelnest.slots.ui.skin.Skin
import com.funpixelnest.slots.ui.skin.SkinButton
import com.funpixelnest.slots.ui.skin.SkinDialog
import com.funpixelnest.slots.ui.skin.SkinIcon
import com.funpixelnest.slots.ui.skin.SkinTone
import com.funpixelnest.slots.ui.skin.VelvetBackdrop
import kotlinx.coroutines.launch
import kotlin.random.Random

private fun plain(size: Int, color: Color = Color.White) = outlinedText(size.sp, color).copy(shadow = null)

/** Dimmed scrim + centred dialog that pops in. */
@Composable
fun Modal(onDismiss: (() -> Unit)?, tag: String, content: @Composable BoxScope.() -> Unit) {
    val pop = remember { Animatable(0.85f) }
    LaunchedEffect(Unit) { pop.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow)) }
    Box(
        Modifier.fillMaxSize().background(Color(0xB30E0220))
            .clickable(remember { MutableInteractionSource() }, null) { onDismiss?.invoke() }
            .testTag(tag),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier.fillMaxWidth(0.9f).scale(pop.value)
                .clickable(remember { MutableInteractionSource() }, null) { },
            contentAlignment = Alignment.Center, content = content,
        )
    }
}

// =========================================================================== disclaimer

/** First screen on first launch: social-casino disclaimer the player must accept. */
@Composable
fun DisclaimerScreen(onAccept: () -> Unit) {
    Box(Modifier.fillMaxSize().testTag("disclaimer"), contentAlignment = Alignment.Center) {
        VelvetBackdrop(Modifier.fillMaxSize())
        Column(
            Modifier.fillMaxWidth(0.88f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("FUNPIXEL", style = plain(13, Skin.TextSoft).copy(letterSpacing = 6.sp))
            Text("Slots Casino", style = plain(34))
            Box(
                Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color(0x14FFFFFF), Color(0x08FFFFFF))), RoundedCornerShape(22.dp))
                    .border(1.dp, Skin.Hairline, RoundedCornerShape(22.dp)).padding(20.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("BEFORE YOU PLAY", style = plain(12, Skin.Gold).copy(letterSpacing = 2.sp, textAlign = TextAlign.Start))
                    listOf(
                        "This is a free-to-play social casino game for entertainment only.",
                        "It does not offer real-money gambling or any chance to win real money or prizes.",
                        "Coins have no cash value and cannot be exchanged, transferred or withdrawn.",
                        "Success in social casino games does not mean future success in real-money gambling.",
                        "Intended for adults aged 18 and over.",
                    ).forEach { line ->
                        Row {
                            Text("•  ", style = plain(14, Skin.Gold))
                            Text(line, style = outlinedText(14.sp, Color(0xFFDDE2EE)).copy(shadow = null, textAlign = TextAlign.Start, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold))
                        }
                    }
                }
            }
            SkinButton("I'M 18+ AND I AGREE", Modifier.fillMaxWidth().height(54.dp), tone = SkinTone.Gold, textSize = 16.sp, onClick = onAccept)
            Text("Play responsibly.", style = plain(11, Skin.TextSoft))
        }
    }
}

// =========================================================================== reward claim with x2 video

/**
 * Any coin reward outside paylines: shows the amount with COLLECT and COLLECT x2 (watch a video).
 * If [alreadyGranted] the base amount is in the balance already (e.g. level-up reward) and x2 adds
 * the same again; otherwise COLLECT grants it.
 */
@Composable
fun RewardClaimDialog(
    graph: AppGraph,
    title: String,
    subtitle: String?,
    amount: Long,
    alreadyGranted: Boolean = false,
    extra: @Composable () -> Unit = {},
    onDone: (granted: Long) -> Unit,
) {
    var shown by remember { mutableLongStateOf(0L) }
    LaunchedEffect(amount) { for (i in 1..30) { shown = amount * i / 30; kotlinx.coroutines.delay(25) } }
    Modal(onDismiss = null, tag = "reward-claim") {
        SkinDialog(title, Modifier.fillMaxWidth(), tone = SkinTone.Gold, sunburst = true) {
            if (subtitle != null) Text(subtitle, style = plain(13, Skin.TextSoft))
            Spacer(Modifier.height(10.dp))
            CoinIcon(Modifier.size(72.dp))
            Spacer(Modifier.height(8.dp))
            Text(formatCoins(shown), style = plain(36, Skin.Gold))
            Text("COINS", style = plain(11, Skin.TextSoft).copy(letterSpacing = 3.sp))
            extra()
            Spacer(Modifier.height(18.dp))
            SkinButton("▶  COLLECT ×${Rewards.VIDEO_MULTIPLIER}", Modifier.fillMaxWidth().height(54.dp), tone = SkinTone.Green, textSize = 17.sp) {
                graph.ads.show("reward_x2") {
                    val total = amount * Rewards.VIDEO_MULTIPLIER
                    graph.player.grant(if (alreadyGranted) total - amount else total)
                    graph.sound.play(Sfx.BigWin)
                    onDone(total)
                }
            }
            Spacer(Modifier.height(10.dp))
            SkinButton("COLLECT ${formatCompact(amount)}", Modifier.fillMaxWidth().height(46.dp), tone = SkinTone.Glass, textSize = 14.sp) {
                if (!alreadyGranted) graph.player.grant(amount)
                graph.sound.play(Sfx.WinCoins)
                onDone(amount)
            }
        }
    }
}

// =========================================================================== daily wheel

/** Once-a-day prize wheel (after the daily bonus). */
@Composable
fun DailyWheelDialog(graph: AppGraph, onDone: () -> Unit) {
    val level by graph.player.levelFlow.collectAsState()
    val segs = Rewards.DAILY_WHEEL.indices.toList()
    val sweep = 360f / segs.size
    val angle = remember { Animatable(0f) }
    var result by remember { mutableStateOf<Int?>(null) }
    var claim by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val colors = listOf(Color(0xFF6D3BEF), Color(0xFFE11D63), Color(0xFF2563EB), Color(0xFF16A34A))
    Modal(onDismiss = null, tag = "daily-wheel") {
        SkinDialog("Daily Spin", Modifier.fillMaxWidth(), tone = SkinTone.Purple) {
            Text("Spin once a day for free coins", style = plain(13, Skin.TextSoft))
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth(0.92f).aspectRatio(1f), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize().rotate(angle.value)) {
                    val r = size.minDimension / 2
                    drawCircle(Brush.sweepGradient(listOf(Skin.GoldLight, Skin.Gold, Skin.GoldDark, Skin.Gold, Skin.GoldLight)), r)
                    val inset = r * 0.05f
                    for (i in segs) {
                        val start = -90f - sweep / 2 + i * sweep
                        drawArc(colors[i % colors.size], start, sweep, true, Offset(inset, inset), Size(size.width - 2 * inset, size.height - 2 * inset))
                        drawArc(Color(0x55000000), start, sweep, true, Offset(inset, inset), Size(size.width - 2 * inset, size.height - 2 * inset), style = Stroke(1.5.dp.toPx()))
                        rotate(start + sweep / 2 + 90f, center) {
                            val t = measurer.measure(formatCompact(Rewards.wheelPrize(i, level)), plain(15))
                            drawText(t, topLeft = Offset(center.x - t.size.width / 2, center.y - r * 0.8f))
                        }
                    }
                    drawCircle(Skin.VelvetDeep, r * 0.17f)
                    drawCircle(Skin.Gold, r * 0.17f, style = Stroke(3.dp.toPx()))
                }
                Canvas(Modifier.size(30.dp).align(Alignment.TopCenter).offset(y = (-6).dp)) {
                    val p = Path().apply { moveTo(0f, 0f); lineTo(size.width, 0f); lineTo(size.width / 2, size.height); close() }
                    drawPath(p, Skin.Gold)
                }
            }
            Spacer(Modifier.height(14.dp))
            if (result == null) {
                SkinButton("SPIN", Modifier.fillMaxWidth().height(54.dp), tone = SkinTone.Gold, textSize = 18.sp, enabled = !angle.isRunning) {
                    val target = Rewards.spinDailyWheel(Random)
                    graph.sound.play(Sfx.Spin)
                    scope.launch {
                        angle.animateTo(360f * 6 - target * sweep, tween(4200, easing = FastOutSlowInEasing))
                        graph.player.lastWheelAt = System.currentTimeMillis()
                        result = target
                        graph.sound.play(Sfx.BigWin)
                        claim = true
                    }
                }
            }
        }
    }
    val r = result
    if (claim && r != null) {
        RewardClaimDialog(graph, "Daily Spin", "The wheel landed on", Rewards.wheelPrize(r, level)) { onDone() }
    }
}

// =========================================================================== store

/** Coin store: bottom sheet with offers, packs and free coins. */
@Composable
fun StoreSheet(graph: AppGraph, onClose: () -> Unit) {
    val coins by graph.player.coinsFlow.collectAsState()
    var tab by remember { mutableIntStateOf(0) }
    var bought by remember { mutableStateOf<CoinPack?>(null) }
    var shower by remember { mutableIntStateOf(0) }
    Box(
        Modifier.fillMaxSize().background(Color(0x990E0220))
            .clickable(remember { MutableInteractionSource() }, null, onClick = onClose)
            .testTag("store"),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            Modifier.fillMaxWidth().fillMaxHeight(0.94f)
                .shadow(30.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Skin.Velvet, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .border(1.dp, Skin.Hairline, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .clickable(remember { MutableInteractionSource() }, null) { }
                .padding(horizontal = 18.dp),
        ) {
            Box(Modifier.padding(top = 10.dp).align(Alignment.CenterHorizontally).width(42.dp).height(5.dp).background(Color(0x33FFFFFF), RoundedCornerShape(50)))
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("BALANCE", style = plain(10, Skin.TextSoft).copy(letterSpacing = 2.sp, textAlign = TextAlign.Start))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CoinIcon(Modifier.size(22.dp)); Spacer(Modifier.width(8.dp))
                        Text(formatCoins(coins), style = plain(22))
                    }
                }
                IconRoundButton(SkinIcon.Close, size = 38.dp, onClick = onClose)
            }
            // tabs
            Row(
                Modifier.padding(top = 14.dp).fillMaxWidth().height(42.dp)
                    .background(Color(0x0DFFFFFF), RoundedCornerShape(14.dp)).border(1.dp, Skin.Hairline, RoundedCornerShape(14.dp)).padding(4.dp),
            ) {
                listOf("Coins", "Offers").forEachIndexed { i, t ->
                    Box(
                        Modifier.weight(1f).fillMaxHeight()
                            .background(if (tab == i) Color(0x1FFFFFFF) else Color.Transparent, RoundedCornerShape(11.dp))
                            .clickable { tab = i },
                        contentAlignment = Alignment.Center,
                    ) { Text(t, style = plain(13, if (tab == i) Color.White else Skin.TextSoft)) }
                }
            }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(top = 14.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OfferCard(graph) { bought = Store.WELCOME_OFFER; shower++ }
                if (tab == 0) {
                    Store.PACKS.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            row.forEach { p -> PackCard(p, Modifier.weight(1f)) { graph.billing.buy(p.id) { graph.player.grant(p.coins); bought = p; shower++ } } }
                        }
                    }
                } else {
                    PiggyCard(graph) { graph.overlay = com.funpixelnest.slots.data.Overlay.Piggy }
                }
                FreeCoinsRow(graph) { shower++ }
            }
        }
        CoinShower(shower, 60, Modifier.fillMaxSize())
    }
    bought?.let { p ->
        Modal(onDismiss = { bought = null }, tag = "purchase-done") {
            SkinDialog("Thank you!", Modifier.fillMaxWidth(), tone = SkinTone.Green) {
                CoinIcon(Modifier.size(60.dp))
                Text("+${formatCoins(p.coins)}", style = plain(30, Skin.Gold))
                Text("coins added to your balance", style = plain(12, Skin.TextSoft))
                Spacer(Modifier.height(14.dp))
                SkinButton("GREAT!", Modifier.fillMaxWidth().height(48.dp), tone = SkinTone.Gold) { bought = null }
            }
        }
    }
}

@Composable
private fun OfferCard(graph: AppGraph, onBought: () -> Unit) {
    val p = Store.WELCOME_OFFER
    Box(
        Modifier.fillMaxWidth().height(132.dp)
            .shadow(16.dp, RoundedCornerShape(22.dp), spotColor = Skin.Ruby)
            .background(Brush.linearGradient(listOf(Color(0xFFFF3D8B), Color(0xFF7C3AED), Color(0xFF3B1C8C))), RoundedCornerShape(22.dp)),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("WELCOME OFFER", style = plain(10, Color(0xFFFFD1E4)).copy(letterSpacing = 2.sp))
            Text("+${p.bonusPercent}%", style = plain(32))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${formatCoins(p.coins)} coins  ", style = plain(13))
                Text(formatCoins(p.baseCoins), style = plain(11, Color(0xFFFFC2DA)).copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough))
            }
            Spacer(Modifier.height(8.dp))
            SkinButton(p.price, Modifier.width(110.dp).height(36.dp), tone = SkinTone.Gold, textSize = 14.sp) {
                graph.billing.buy(p.id) { graph.player.grant(p.coins); onBought() }
            }
        }
        Text("⏱ 23:59:41", style = plain(11), modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
            .background(Color(0x59000000), RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 4.dp))
        CoinStack(15, Modifier.align(Alignment.BottomEnd).padding(end = 18.dp, bottom = 16.dp).size(width = 110.dp, height = 70.dp))
    }
}

@Composable
private fun PackCard(p: CoinPack, modifier: Modifier, onBuy: () -> Unit) {
    val accent = when (p.badge) { "BEST VALUE" -> Skin.Gold; null -> null; else -> Skin.Sapphire }
    Box(modifier) {
        Column(
            Modifier.fillMaxWidth().padding(top = 8.dp)
                .background(Color(0x0DFFFFFF), RoundedCornerShape(20.dp))
                .border(1.dp, accent?.copy(alpha = 0.7f) ?: Skin.Hairline, RoundedCornerShape(20.dp))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            CoinStack(2 + Store.PACKS.indexOf(p) * 3, Modifier.size(width = 72.dp, height = 46.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(formatCompact(p.coins), style = plain(18)); Text(" coins", style = plain(11, Skin.TextSoft))
            }
            Text(if (p.bonusPercent > 0) "+${p.bonusPercent}% more" else " ", style = plain(11, Skin.Emerald))
            SkinButton(p.price, Modifier.fillMaxWidth().height(38.dp), tone = SkinTone.Green, textSize = 14.sp, onClick = onBuy)
        }
        val badge = p.badge
        if (badge != null && accent != null) {
            Text(
                badge, style = plain(9, Color(0xFF1C0738)).copy(letterSpacing = 1.sp),
                modifier = Modifier.align(Alignment.TopCenter).background(accent, RoundedCornerShape(50)).padding(horizontal = 9.dp, vertical = 3.dp),
            )
        }
    }
}

/** Little pile of coins (count grows with pack size). */
@Composable
fun CoinStack(count: Int, modifier: Modifier) {
    Box(modifier) {
        val cols = 3
        for (i in 0 until count.coerceAtMost(18)) {
            val col = i % cols
            val lvl = i / cols
            CoinIcon(
                Modifier.size(26.dp).align(Alignment.BottomCenter)
                    .offset(x = ((col - 1) * 18).dp, y = (-(lvl * 6) - if (col == 1) 3 else 0).dp)
            )
        }
    }
}

@Composable
private fun FreeCoinsRow(graph: AppGraph, onClaimed: () -> Unit) {
    val now = System.currentTimeMillis()
    val left = graph.player.videoCoinsReadyIn(now)
    Row(
        Modifier.fillMaxWidth().background(Color(0x0DFFFFFF), RoundedCornerShape(18.dp)).border(1.dp, Skin.Hairline, RoundedCornerShape(18.dp)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconRoundButton(SkinIcon.Video, size = 40.dp, tone = SkinTone.Gold) { }
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text("Free coins", style = plain(14).copy(textAlign = TextAlign.Start))
            Text(if (left == 0L) "Watch a short video" else "Next video in ${left / 60000 + 1} min", style = plain(11, Skin.TextSoft).copy(textAlign = TextAlign.Start))
        }
        SkinButton("+${formatCompact(graph.player.videoCoinsAmount())}", Modifier.width(92.dp).height(38.dp), tone = SkinTone.Green, enabled = left == 0L) {
            graph.ads.show("free_coins") {
                if (graph.player.claimVideoCoins(System.currentTimeMillis()) > 0) { graph.sound.play(Sfx.WinCoins); onClaimed() }
            }
        }
    }
}

// =========================================================================== piggy bank

@Composable
private fun PiggyCard(graph: AppGraph, onOpen: () -> Unit) {
    val piggy by graph.player.piggyFlow.collectAsState()
    Row(
        Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0x33FF3D8B), Color(0x147C5CFF))), RoundedCornerShape(20.dp))
            .border(1.dp, Skin.Ruby.copy(alpha = 0.45f), RoundedCornerShape(20.dp)).clickable(onClick = onOpen).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PiggyIcon(Modifier.size(56.dp))
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text("Piggy Bank", style = plain(16).copy(textAlign = TextAlign.Start))
            Text("${formatCompact(piggy)} saved from your spins", style = plain(11, Skin.TextSoft).copy(textAlign = TextAlign.Start))
        }
        Text("OPEN ›", style = plain(13, Skin.Gold))
    }
}

/** Drawn piggy bank (pink pig with a coin slot). */
@Composable
fun PiggyIcon(modifier: Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val pink = Brush.radialGradient(listOf(Color(0xFFFFB3CF), Color(0xFFFF5C9A)), Offset(w * 0.4f, h * 0.4f), w * 0.7f)
        drawOval(pink, Offset(w * 0.1f, h * 0.25f), Size(w * 0.78f, h * 0.6f))
        drawCircle(Color(0xFFFF5C9A), w * 0.12f, Offset(w * 0.86f, h * 0.55f))
        drawCircle(Color(0xFF9D1747), w * 0.025f, Offset(w * 0.84f, h * 0.53f))
        drawCircle(Color(0xFF9D1747), w * 0.025f, Offset(w * 0.9f, h * 0.57f))
        drawPath(Path().apply { moveTo(w * 0.62f, h * 0.3f); lineTo(w * 0.72f, h * 0.12f); lineTo(w * 0.76f, h * 0.34f); close() }, Color(0xFFFF5C9A))
        drawCircle(Color(0xFF2A0A18), w * 0.035f, Offset(w * 0.68f, h * 0.45f))
        drawRoundRect(Color(0xFF9D1747), Offset(w * 0.36f, h * 0.29f), Size(w * 0.2f, h * 0.05f), androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
        drawRoundRect(Color(0xFFFF5C9A), Offset(w * 0.24f, h * 0.76f), Size(w * 0.1f, h * 0.18f), androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
        drawRoundRect(Color(0xFFFF5C9A), Offset(w * 0.6f, h * 0.76f), Size(w * 0.1f, h * 0.18f), androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
    }
}

/** Piggy bank: fills from every paid spin; break it open with a purchase. */
@Composable
fun PiggyBankDialog(graph: AppGraph, onClose: () -> Unit) {
    val piggy by graph.player.piggyFlow.collectAsState()
    val cap = graph.player.piggyCap()
    var broken by remember { mutableLongStateOf(0L) }
    Modal(onDismiss = onClose, tag = "piggy-bank") {
        SkinDialog("Piggy Bank", Modifier.fillMaxWidth(), tone = SkinTone.Red, sunburst = true) {
            Text("Every spin drops coins into your piggy.", style = plain(13, Skin.TextSoft))
            Spacer(Modifier.height(10.dp))
            PiggyIcon(Modifier.size(120.dp))
            Spacer(Modifier.height(6.dp))
            Text(formatCoins(piggy), style = plain(32, Skin.Gold))
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().height(10.dp).background(Color(0x1FFFFFFF), RoundedCornerShape(50))) {
                Box(Modifier.fillMaxHeight().fillMaxWidth((piggy.toFloat() / cap).coerceIn(0.02f, 1f)).background(Brush.horizontalGradient(listOf(Skin.Ruby, Skin.Gold)), RoundedCornerShape(50)))
            }
            Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Text("${(piggy * 100 / cap)}% full", style = plain(10, Skin.TextSoft)); Spacer(Modifier.weight(1f))
                Text("max ${formatCompact(cap)}", style = plain(10, Skin.TextSoft))
            }
            Spacer(Modifier.height(16.dp))
            SkinButton(
                if (graph.player.canBreakPiggy()) "BREAK IT · ${Rewards.PIGGY_PRICE}" else "KEEP SPINNING TO FILL",
                Modifier.fillMaxWidth().height(54.dp), tone = SkinTone.Gold, textSize = 16.sp, enabled = graph.player.canBreakPiggy(),
            ) {
                graph.billing.buy("piggy_bank") {
                    val amount = graph.player.piggy
                    graph.player.grant(amount)
                    graph.player.piggy = 0
                    broken = amount
                    graph.sound.play(Sfx.BigWin)
                }
            }
            Spacer(Modifier.height(8.dp))
            SkinButton("CLOSE", Modifier.fillMaxWidth().height(44.dp), tone = SkinTone.Glass, onClick = onClose)
            if (broken > 0) Text("+${formatCoins(broken)} added!", style = plain(14, Skin.Emerald), modifier = Modifier.padding(top = 8.dp))
        }
    }
}


// =========================================================================== tasks & missions

/** Daily tasks ([daily] = true) or long-term missions; each finished one is claimed with the x2 video option. */
@Composable
fun QuestsDialog(graph: AppGraph, daily: Boolean, onClose: () -> Unit) {
    val p = graph.player
    val tick by p.questsFlow.collectAsState()
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) { while (true) { kotlinx.coroutines.delay(1000); now = System.currentTimeMillis() } }
    var claiming by remember { mutableStateOf<Quest?>(null) }
    val list = if (daily) Quests.DAILY_TASKS else Quests.MISSIONS
    Modal(onClose, tag = if (daily) "tasks" else "missions") {
        SkinDialog(if (daily) "Daily Tasks" else "Missions", Modifier.fillMaxWidth(), tone = SkinTone.Purple) {
            val left = Quests.msToReset(now) / 1000
            Text(
                if (daily) "New tasks in %d:%02d:%02d".format(left / 3600, left / 60 % 60, left % 60) else "Long-term goals with big rewards",
                style = plain(12, Skin.TextSoft),
            )
            Spacer(Modifier.height(12.dp))
            Column(Modifier.heightIn(max = 470.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                list.forEach { q ->
                    // tick: re-read progress whenever the store reports a change
                    val progress = remember(tick, now / 60_000) { p.questProgress(q, daily) }
                    val claimed = remember(tick) { p.isClaimed(q, daily) }
                    QuestRow(q, progress, claimed) { graph.sound.play(Sfx.Button); claiming = q }
                }
            }
            Spacer(Modifier.height(14.dp))
            SkinButton("CLOSE", Modifier.fillMaxWidth().height(44.dp), tone = SkinTone.Glass, textSize = 14.sp, onClick = onClose)
        }
    }
    claiming?.let { q ->
        RewardClaimDialog(graph, q.title, if (daily) "Daily task complete!" else "Mission complete!", q.reward) {
            p.markClaimed(q, daily); claiming = null
        }
    }
}

@Composable
private fun QuestRow(q: Quest, progress: Long, claimed: Boolean, onClaim: () -> Unit) {
    val done = progress >= q.target
    Row(
        Modifier.fillMaxWidth().background(Color(0x14FFFFFF), RoundedCornerShape(14.dp))
            .border(1.dp, if (done && !claimed) Skin.Gold.copy(alpha = 0.7f) else Skin.Hairline, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(q.title, style = plain(13).copy(textAlign = TextAlign.Start))
            Spacer(Modifier.height(6.dp))
            Box(Modifier.fillMaxWidth().height(8.dp).background(Color(0x1FFFFFFF), RoundedCornerShape(50))) {
                Box(
                    Modifier.fillMaxHeight().fillMaxWidth((progress.toFloat() / q.target).coerceIn(0.02f, 1f))
                        .background(Brush.horizontalGradient(listOf(Skin.Sapphire, Skin.Emerald)), RoundedCornerShape(50)),
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${formatCompact(progress)} / ${formatCompact(q.target)}", style = plain(10, Skin.TextSoft))
                Spacer(Modifier.width(10.dp))
                CoinIcon(Modifier.size(12.dp)); Spacer(Modifier.width(3.dp))
                Text(formatCompact(q.reward), style = plain(11, Skin.Gold))
            }
        }
        Spacer(Modifier.width(10.dp))
        when {
            claimed -> Text("✓ DONE", style = plain(12, Skin.Emerald), modifier = Modifier.width(76.dp))
            done -> SkinButton("CLAIM", Modifier.width(76.dp).height(36.dp), tone = SkinTone.Green, textSize = 13.sp, onClick = onClaim)
            else -> Text("${(progress * 100 / q.target)}%", style = plain(13, Skin.TextSoft), modifier = Modifier.width(76.dp))
        }
    }
}
