package com.funpixelnest.slots.ui.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funpixelnest.slots.audio.Sfx
import com.funpixelnest.slots.data.AppGraph
import com.funpixelnest.slots.engine.Cell
import com.funpixelnest.slots.engine.JackpotTier
import com.funpixelnest.slots.engine.Prize
import com.funpixelnest.slots.ui.common.CoinIcon
import com.funpixelnest.slots.ui.common.formatCoins
import com.funpixelnest.slots.ui.common.formatCompact
import com.funpixelnest.slots.ui.common.outlinedText
import com.funpixelnest.slots.ui.skin.GoldFrame
import com.funpixelnest.slots.ui.skin.Ribbon
import com.funpixelnest.slots.ui.skin.Skin
import com.funpixelnest.slots.ui.skin.SkinButton
import com.funpixelnest.slots.ui.skin.SkinIcon
import com.funpixelnest.slots.ui.skin.SkinTone
import com.funpixelnest.slots.ui.skin.Sunburst
import com.funpixelnest.slots.ui.skin.drawSkinIcon
import com.funpixelnest.slots.ui.skin.ball
import com.funpixelnest.slots.ui.skin.glowPanel
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.alpha
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Full-screen bonus game over the machine. */
@Composable
fun BonusOverlay(graph: AppGraph, vm: GameViewModel) {
    val b = vm.bonus ?: return
    Box(
        Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0xE62A1AA8), Color(0xF5070838))))
            .clickable(remember { MutableInteractionSource() }, null) { }
            .testTag("bonus-game"),
        contentAlignment = Alignment.Center,
    ) {
        Sunburst(Modifier.fillMaxSize())
        when (b) {
            is BonusUi.HoldSpin -> HoldAndSpinBonus(graph, vm, b)
            is BonusUi.Wheel -> WheelBonus(graph, vm, b)
            is BonusUi.Pick -> PickBonusGame(graph, vm, b)
        }
    }
}

@Composable
private fun BonusTitle(title: String, sub: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        com.funpixelnest.slots.ui.skin.GoldDisplay(title, 40.sp)
        Spacer(Modifier.height(6.dp))
        Text(sub, style = outlinedText(13.sp, Skin.TextSoft).copy(shadow = null))
    }
}

@Composable
private fun PrizeText(p: Prize, size: Int = 14) {
    val jp = p.jackpot
    if (jp != null) Text(jp.label, style = com.funpixelnest.slots.ui.skin.displayText(size.sp, Color.White))
    else Text(formatCompact(p.coins), style = com.funpixelnest.slots.ui.skin.displayText(size.sp, Color(0xFF5A2000)).copy(shadow = null))
}

// ------------------------------------------------------------------ Hold & Spin

@Composable
private fun HoldAndSpinBonus(graph: AppGraph, vm: GameViewModel, b: BonusUi.HoldSpin) {
    val game = b.game
    var tick by remember { mutableIntStateOf(0) }       // recompose after each respin
    var fresh by remember { mutableStateOf<List<Cell>>(emptyList()) }
    LaunchedEffect(game) {
        delay(1200)
        while (!game.isOver) {
            fresh = game.respin()
            if (fresh.isNotEmpty()) graph.sound.play(Sfx.WinCoins) else graph.sound.play(Sfx.ReelStop, 0.6f)
            tick++
            delay(1100)
        }
    }
    val pulse by rememberInfiniteTransition(label = "hs").animateFloat(
        0.9f, 1.08f, infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "p",
    )
    val board = tick // read so the board redraws after every respin
    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        BonusTitle("HOLD & SPIN", "Coins lock in place. Each new coin resets the respins. Fill all 15 for the GRAND!")
        if (game.isOver && board >= 0) Text("BONUS COMPLETE", style = com.funpixelnest.slots.ui.skin.displayText(22.sp, Color(0xFF8CFF6A)))
        else Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("RESPINS", style = outlinedText(12.sp, Skin.TextSoft).copy(shadow = null, letterSpacing = 2.sp))
            repeat(3) { i -> Box(Modifier.size(26.dp).then(if (i < game.respinsLeft) Modifier.ball(SkinTone.Green) else Modifier.background(Color(0xFF0A0A40), CircleShape).border(2.dp, Color(0xFF3A3A8A), CircleShape))) }
        }
        GoldFrame(Modifier.fillMaxWidth().aspectRatio(5f / 3.3f), corner = 18.dp) {
            Column(Modifier.fillMaxSize().padding(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (row in 0 until game.rows) Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (reel in 0 until game.reels) {
                        val c = Cell(reel, row)
                        val prize = game.coins[c]
                        Box(
                            Modifier.weight(1f).fillMaxSize()
                                .then(if (prize == null) Modifier.background(Color(0x8C00001E), RoundedCornerShape(12.dp)).border(1.5.dp, Color(0x596AB4FF), RoundedCornerShape(12.dp)) else Modifier),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (prize != null) {
                                val jp = prize.jackpot
                                val tone = when (jp) {
                                    null -> null
                                    JackpotTier.MINI -> SkinTone.Green
                                    JackpotTier.MINOR -> SkinTone.Blue
                                    JackpotTier.MAJOR -> SkinTone.Purple
                                    else -> SkinTone.Red
                                }
                                Box(
                                    Modifier.scale(if (c in fresh) pulse else 1f).fillMaxSize(0.92f).aspectRatio(1f)
                                        .then(if (tone != null) Modifier.ball(tone) else Modifier.ball(SkinTone.Gold).background(Brush.radialGradient(listOf(Color(0xFFFFF6B0), Color(0xFFFFC21A), Color(0xFFD07000))), CircleShape)),
                                    contentAlignment = Alignment.Center,
                                ) { PrizeText(prize, 13) }
                            }
                        }
                    }
                }
            }
        }
        val total = game.coins.values.sumOf { it.coins }
        com.funpixelnest.slots.ui.game.WinMeter(total, Modifier.height(46.dp), "TOTAL")
        if (game.grandWon) Text("BOARD FULL – GRAND JACKPOT!", style = outlinedText(18.sp, Color(0xFFFF8A80)))
        if (game.isOver) {
            SkinButton("COLLECT", Modifier.width(220.dp).height(54.dp), tone = SkinTone.Green, textSize = 20.sp) {
                val jps = game.coins.values.mapNotNull { it.jackpot } + listOfNotNull(JackpotTier.GRAND.takeIf { game.grandWon })
                vm.finishBonus(game.total(), jps)
            }
        }
    }
}

// ------------------------------------------------------------------ Bonus wheel

private val WHEEL_COLORS = listOf(Color(0xFF2A6BFF), Color(0xFF9A3AFF), Color(0xFF1AA6C8), Color(0xFFFF8A00), Color(0xFF2FA83A), Color(0xFFC41A9A))

@Composable
private fun WheelBonus(graph: AppGraph, vm: GameViewModel, b: BonusUi.Wheel) {
    val wheel = b.wheel
    val n = wheel.segments.size
    val sweep = 360f / n
    val angle = remember { Animatable(0f) }
    var result by remember { mutableStateOf<Int?>(null) }
    var spinning by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val labels = remember(wheel) {
        wheel.segments.indices.map { i ->
            wheel.segments[i].jackpot?.label ?: "${formatCompact(wheel.prizeOf(i).coins)}"
        }
    }
    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        BonusTitle("BONUS WHEEL", "Spin the wheel for coins or a jackpot!")
        Box(Modifier.fillMaxWidth(0.92f).aspectRatio(1f), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize().rotate(angle.value)) {
                val r = size.minDimension / 2
                drawCircle(Skin.goldFill, r)
                for (i in 0 until n) {
                    val start = -90f - sweep / 2 + i * sweep
                    drawArc(
                        if (wheel.segments[i].jackpot != null) Color(0xFFFFC93C) else WHEEL_COLORS[i % WHEEL_COLORS.size],
                        start, sweep, true, Offset(r * 0.06f, r * 0.06f), Size(size.width - r * 0.12f, size.height - r * 0.12f),
                    )
                    drawArc(Skin.GoldDeep, start, sweep, true, Offset(r * 0.06f, r * 0.06f), Size(size.width - r * 0.12f, size.height - r * 0.12f), style = Stroke(2.dp.toPx()))
                    rotate(start + sweep / 2 + 90f, center) {
                        val text = measurer.measure(
                            labels[i],
                            outlinedText(if (wheel.segments[i].jackpot != null) 13.sp else 15.sp,
                                if (wheel.segments[i].jackpot != null) Color(0xFF4A0000) else Color.White),
                        )
                        drawText(text, topLeft = Offset(center.x - text.size.width / 2, center.y - r * 0.82f))
                    }
                }
                drawCircle(Skin.goldFill, r * 0.16f)
                drawCircle(Skin.GoldDeep, r * 0.16f, style = Stroke(3.dp.toPx()))
            }
            // pointer at the top
            Canvas(Modifier.size(34.dp).align(Alignment.TopCenter)) {
                val p = Path().apply { moveTo(0f, 0f); lineTo(size.width, 0f); lineTo(size.width / 2, size.height); close() }
                drawPath(p, Color(0xFFE53935))
                drawPath(p, Color.White, style = Stroke(2.dp.toPx()))
            }
        }
        when {
            result != null -> {
                val prize = wheel.prizeOf(result!!)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoinIcon(Modifier.size(30.dp)); Spacer(Modifier.width(8.dp))
                    val jp = prize.jackpot
                    com.funpixelnest.slots.ui.skin.GoldDisplay(if (jp != null) "${jp.label} JACKPOT  ${formatCoins(prize.coins)}" else formatCoins(prize.coins), 26.sp)
                }
                SkinButton("COLLECT", Modifier.width(220.dp).height(54.dp), tone = SkinTone.Green, textSize = 20.sp) {
                    vm.finishBonus(prize.coins, listOfNotNull(prize.jackpot))
                }
            }
            else -> SkinButton("SPIN THE WHEEL", Modifier.width(240.dp).height(56.dp), tone = SkinTone.Green, enabled = !spinning, textSize = 20.sp) {
                spinning = true
                graph.sound.play(Sfx.Spin)
                val target = wheel.spin(kotlin.random.Random)
                scope.launch {
                    // land so that segment [target] is under the top pointer
                    val end = 360f * 6 - target * sweep
                    angle.animateTo(end, tween(4200, easing = FastOutSlowInEasing))
                    result = target
                    graph.sound.play(Sfx.BigWin)
                }
            }
        }
    }
}

// ------------------------------------------------------------------ Pick bonus

@Composable
private fun PickBonusGame(graph: AppGraph, vm: GameViewModel, b: BonusUi.Pick) {
    val pick = b.pick
    val opened = remember { mutableStateListOf<Int>() }
    var revealAll by remember { mutableStateOf(false) }
    LaunchedEffect(opened.size) {
        if (pick.isOver && !revealAll) { delay(900); revealAll = true }
    }
    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        BonusTitle("PICK BONUS", "Pick ${com.funpixelnest.slots.engine.PickBonus.PICKS} treasures. Picks left: ${com.funpixelnest.slots.engine.PickBonus.PICKS - opened.size}")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (row in 0 until 3) Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                for (col in 0 until 3) {
                    val i = row * 3 + col
                    val isPicked = i in opened
                    val shown = isPicked || revealAll
                    Box(
                        Modifier.size(98.dp)
                            .glowPanel(18.dp, magenta = isPicked).alpha(if (shown && !isPicked) 0.7f else 1f)
                            .clickable(remember { MutableInteractionSource() }, null, enabled = !shown && !pick.isOver) {
                                pick.pick(i)?.let { opened += i; graph.sound.play(Sfx.WinCoins) }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (shown) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CoinIcon(Modifier.size(if (isPicked) 34.dp else 24.dp))
                                PrizeText(pick.hidden[i], if (isPicked) 15 else 11)
                            }
                        } else {
                            Canvas(Modifier.size(52.dp)) { drawSkinIcon(SkinIcon.Gift) }
                        }
                    }
                }
            }
        }
        com.funpixelnest.slots.ui.game.WinMeter(pick.total(), Modifier.height(46.dp), "TOTAL")
        if (revealAll) {
            SkinButton("COLLECT", Modifier.width(220.dp).height(54.dp), tone = SkinTone.Green, textSize = 20.sp) {
                vm.finishBonus(pick.total(), opened.mapNotNull { pick.hidden[it].jackpot })
            }
        }
    }
}
