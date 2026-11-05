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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Full-screen bonus game over the machine. */
@Composable
fun BonusOverlay(graph: AppGraph, vm: GameViewModel) {
    val b = vm.bonus ?: return
    Box(
        Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.82f))
            .clickable(remember { MutableInteractionSource() }, null) { }
            .testTag("bonus-game"),
        contentAlignment = Alignment.Center,
    ) {
        Sunburst(Modifier.fillMaxSize(), color = Skin.Gold)
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
        Ribbon(title, tone = SkinTone.Red, textSize = 24.sp)
        Spacer(Modifier.height(6.dp))
        Text(sub, style = outlinedText(13.sp, Skin.TextSoft))
    }
}

@Composable
private fun PrizeText(p: Prize, size: Int = 14) {
    val jp = p.jackpot
    if (jp != null) Text(jp.label, style = outlinedText(size.sp, Color(0xFFFF8A80)))
    else Text(formatCompact(p.coins), style = outlinedText(size.sp, Skin.GoldLight))
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
        Text(
            if (game.isOver && board >= 0) "BONUS COMPLETE" else "RESPINS LEFT: ${game.respinsLeft}",
            style = outlinedText(20.sp, if (game.isOver) Color(0xFF7CF29A) else Color.White),
        )
        GoldFrame(Modifier.fillMaxWidth().aspectRatio(5f / 3.3f), corner = 18.dp) {
            Column(Modifier.fillMaxSize().padding(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (row in 0 until game.rows) Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (reel in 0 until game.reels) {
                        val c = Cell(reel, row)
                        val prize = game.coins[c]
                        Box(
                            Modifier.weight(1f).fillMaxSize()
                                .background(
                                    if (prize != null) Brush.radialGradient(listOf(Color(0xFF8E44E8), Color(0xFF2A0846)))
                                    else Brush.verticalGradient(listOf(Color(0xFF1A0530), Color(0xFF0B0114))),
                                    RoundedCornerShape(10.dp),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (prize != null) {
                                Column(
                                    Modifier.scale(if (c in fresh) pulse else 1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    CoinIcon(Modifier.size(30.dp))
                                    PrizeText(prize, 12)
                                }
                            }
                        }
                    }
                }
            }
        }
        val total = game.coins.values.sumOf { it.coins }
        Text("TOTAL ${formatCoins(total)}", style = outlinedText(22.sp, Skin.GoldLight))
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

private val WHEEL_COLORS = listOf(Color(0xFFE53935), Color(0xFF5B1FA8), Color(0xFF138A3A), Color(0xFF1652B8))

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
                    Text(if (jp != null) "${jp.label} JACKPOT  ${formatCoins(prize.coins)}" else formatCoins(prize.coins), style = outlinedText(24.sp, Skin.GoldLight))
                }
                SkinButton("COLLECT", Modifier.width(220.dp).height(54.dp), tone = SkinTone.Green, textSize = 20.sp) {
                    vm.finishBonus(prize.coins, listOfNotNull(prize.jackpot))
                }
            }
            else -> SkinButton("SPIN THE WHEEL", Modifier.width(240.dp).height(56.dp), tone = SkinTone.Red, enabled = !spinning, textSize = 20.sp) {
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
                            .background(Skin.goldBorder, RoundedCornerShape(16.dp)).padding(2.5.dp)
                            .background(
                                when {
                                    isPicked -> Brush.radialGradient(listOf(Color(0xFFFFE27A), Color(0xFFE08A00)))
                                    shown -> Brush.verticalGradient(listOf(Color(0xFF3A1B5C), Color(0xFF1A0530)))
                                    else -> Brush.verticalGradient(listOf(Color(0xFF8E44E8), Color(0xFF3B0D5C)))
                                },
                                RoundedCornerShape(14.dp),
                            )
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
        Text("WON ${formatCoins(pick.total())}", style = outlinedText(22.sp, Skin.GoldLight))
        if (revealAll) {
            SkinButton("COLLECT", Modifier.width(220.dp).height(54.dp), tone = SkinTone.Green, textSize = 20.sp) {
                vm.finishBonus(pick.total(), opened.mapNotNull { pick.hidden[it].jackpot })
            }
        }
    }
}
