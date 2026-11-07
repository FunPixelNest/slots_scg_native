package com.funpixelnest.slots.ui.game

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funpixelnest.slots.audio.Haptics
import com.funpixelnest.slots.ui.common.CoinIcon
import com.funpixelnest.slots.ui.common.Montserrat
import com.funpixelnest.slots.ui.common.formatCoins
import com.funpixelnest.slots.ui.common.formatCompact
import com.funpixelnest.slots.ui.skin.Skin
import com.funpixelnest.slots.ui.skin.SkinIcon
import com.funpixelnest.slots.ui.skin.drawSkinIcon
import kotlin.math.cos
import kotlin.math.sin

/*
 * Slot-cabinet pieces for the gameplay screen: full-width HUD strip, jackpot marquee with
 * light bulbs, gold cabinet frame around the reels, win meter and the control deck.
 */

/** Polished gold, light at the top, used for rims, knobs and keys. */
val GoldMetal = Brush.verticalGradient(listOf(Color(0xFFFFF6C8), Color(0xFFFFD44A), Color(0xFFC8801A), Color(0xFFFFE48A)))
private val DeckPurple = Brush.verticalGradient(listOf(Color(0xFF5A1A9A), Color(0xFF2A0A55), Color(0xFF1A0533)))
private val GoldEdge = Color(0xFFF5C451)

private fun style(size: TextUnit, color: Color, weight: FontWeight = FontWeight.Black, spacing: TextUnit = 0.sp) =
    TextStyle(fontFamily = Montserrat, fontWeight = weight, fontSize = size, color = color, letterSpacing = spacing)

/** Round gold key with an icon (back, menu). */
@Composable
fun GoldKey(icon: SkinIcon, size: Dp = 40.dp, onClick: () -> Unit) {
    Box(
        Modifier.size(size).shadow(6.dp, CircleShape).background(GoldMetal, CircleShape)
            .border(1.5.dp, Color(0xFF8A5208), CircleShape)
            .clickable(remember { MutableInteractionSource() }, null) { Haptics.tap(); onClick() },
        contentAlignment = Alignment.Center,
    ) { Canvas(Modifier.size(size * 0.42f)) { drawSkinIcon(icon, Color(0xFF4A2200)) } }
}

/** Gold five-point star with the player's level in it. */
@Composable
fun LevelStar(level: Int, size: Dp = 46.dp) {
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val c = Offset(this.size.width / 2, this.size.height / 2 + this.size.height * 0.04f)
            val r = this.size.minDimension / 2
            val star = Path()
            for (i in 0 until 10) {
                val a = -Math.PI / 2 + i * Math.PI / 5
                val rr = if (i % 2 == 0) r else r * 0.48f
                val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
                if (i == 0) star.moveTo(x, y) else star.lineTo(x, y)
            }
            star.close()
            drawPath(star, Color(0xFF6A3A00))
            drawPath(star, GoldMetal, alpha = 1f)
        }
        Text("$level", style = style(if (level < 100) 15.sp else 12.sp, Color(0xFF4A2200)), modifier = Modifier.offset(y = 2.dp))
    }
}

/** Full-width HUD strip: back · level star + XP · coins · menu, with a gold lower edge. */
@Composable
fun CabinetTopBar(
    level: Int, progress: Float, coins: Long,
    onBack: () -> Unit, onCoins: () -> Unit, onMenu: () -> Unit,
    xpOverlay: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        Modifier.fillMaxWidth()
            .shadow(10.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF4A1388), Color(0xFF2A0A55))))
            .drawBehind { drawRect(GoldEdge, Offset(0f, size.height - 3.dp.toPx()), androidx.compose.ui.geometry.Size(size.width, 3.dp.toPx())) },
    ) {
        Row(
            Modifier.fillMaxWidth().height(58.dp).padding(horizontal = 8.dp).padding(bottom = 3.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GoldKey(SkinIcon.Back, 38.dp, onBack)
            LevelStar(level)
            Box(Modifier.weight(1f)) {
                Column {
                    Text("LEVEL $level", style = style(10.sp, Color(0xFFFFE08A), FontWeight.ExtraBold, 1.sp))
                    Box(
                        Modifier.padding(top = 3.dp).fillMaxWidth().height(10.dp)
                            .background(Color(0xFF1A0533), RoundedCornerShape(6.dp)).border(1.5.dp, GoldEdge, RoundedCornerShape(6.dp)).padding(1.5.dp),
                    ) {
                        Box(
                            Modifier.fillMaxHeight().fillMaxWidth((progress / 100f).coerceIn(0.03f, 1f))
                                .background(Brush.horizontalGradient(listOf(Color(0xFFFF3DB4), Color(0xFFFFB13D))), RoundedCornerShape(5.dp)),
                        )
                    }
                }
                xpOverlay()
            }
            Row(
                Modifier.height(36.dp).background(Color(0xFF1A0533), RoundedCornerShape(50)).border(2.dp, GoldEdge, RoundedCornerShape(50))
                    .clickable(remember { MutableInteractionSource() }, null, onClick = onCoins).padding(start = 3.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CoinIcon(Modifier.size(28.dp)); Spacer(Modifier.width(6.dp))
                Text(formatCompact(coins), style = style(16.sp, Color.White))
            }
            GoldKey(SkinIcon.Menu, 38.dp, onMenu)
        }
    }
}

/** GRAND jackpot marquee: gold frame, chasing light bulbs, big gold number. */
@Composable
fun JackpotMarquee(value: Long, modifier: Modifier) {
    val chase by rememberInfiniteTransition(label = "bulbs").animateFloat(
        0f, 1f, infiniteRepeatable(tween(700), RepeatMode.Restart), label = "c",
    )
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier.shadow(14.dp, shape, ambientColor = Skin.Gold, spotColor = Skin.Gold).background(GoldMetal, shape).padding(4.dp)
            .background(Brush.radialGradient(listOf(Color(0xFF7A1240), Color(0xFF2A0420)), radius = 520f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val n = 17
            val step = size.width / n
            val on = (chase * 2).toInt() % 2
            for (i in 0 until n) {
                val x = step * (i + 0.5f)
                val lit = (i % 2) == on
                for (y in listOf(6.dp.toPx(), size.height - 6.dp.toPx())) {
                    if (lit) drawCircle(Color(0x66FFD44A), 7.dp.toPx(), Offset(x, y))
                    drawCircle(if (lit) Color(0xFFFFF6C8) else Color(0xFF9A6A2A), 3.5.dp.toPx(), Offset(x, y))
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("GRAND JACKPOT", style = style(10.sp, Color(0xFFFFD0E4), FontWeight.ExtraBold, 3.sp))
            GoldText(formatCoins(value), 30.sp)
        }
    }
}

/** MAJOR / MINOR / MINI badge in its colour, with a light rim. */
@Composable
fun JackpotBadge(label: String, value: Long, rim: Color, top: Color, bottom: Color, modifier: Modifier) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier.shadow(6.dp, shape).background(Brush.verticalGradient(listOf(rim, top)), shape).padding(2.dp)
            .background(Brush.verticalGradient(listOf(top, bottom)), shape),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = style(9.sp, rim, FontWeight.ExtraBold, 1.5.sp))
            Text(formatCoins(value), maxLines = 1, style = style(14.sp, Color.White))
        }
    }
}

/**
 * Gold cabinet around the reels: thick polished rim, gold knobs at the corners and a plaque
 * on the top edge.
 */
@Composable
fun CabinetFrame(plaque: String, modifier: Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(modifier) {
        val outer = RoundedCornerShape(24.dp)
        Box(
            Modifier.fillMaxSize().padding(top = 10.dp)
                .shadow(18.dp, outer, ambientColor = Skin.Gold, spotColor = Color.Black)
                .background(GoldMetal, outer).border(1.5.dp, Color(0xFF6A3A00), outer).padding(7.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF2A0B4A), Color(0xFF14052A))), RoundedCornerShape(18.dp))
                .border(1.dp, Color(0x88000000), RoundedCornerShape(18.dp))
                .padding(start = 4.dp, end = 4.dp, top = 16.dp, bottom = 6.dp),
            content = content,
        )
        // corner knobs
        listOf(Alignment.TopStart, Alignment.TopEnd, Alignment.BottomStart, Alignment.BottomEnd).forEach { a ->
            val dy = if (a == Alignment.TopStart || a == Alignment.TopEnd) 6.dp else 0.dp
            Box(
                Modifier.align(a).offset(y = dy).size(24.dp).shadow(4.dp, CircleShape).background(GoldMetal, CircleShape)
                    .border(1.dp, Color(0xFF6A3A00), CircleShape),
            )
        }
        Text(
            plaque, style = style(12.sp, Color(0xFF3A1800), FontWeight.Black, 1.sp),
            modifier = Modifier.align(Alignment.TopCenter).shadow(6.dp, RoundedCornerShape(10.dp))
                .background(GoldMetal, RoundedCornerShape(10.dp)).border(1.dp, Color(0xFF6A3A00), RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 3.dp),
        )
    }
}

/** WIN read-out under the reels. */
@Composable
fun WinMeter(win: Long, modifier: Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier.shadow(8.dp, shape).background(GoldMetal, shape).padding(3.dp)
            .background(Color(0xFF0E0220), RoundedCornerShape(9.dp)).padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center,
    ) {
        Text("WIN", style = style(12.sp, Color(0xFFFFD0E4), FontWeight.ExtraBold, 3.sp))
        Spacer(Modifier.width(12.dp))
        GoldText(formatCoins(win), 24.sp)
    }
}

/** Control deck: TOTAL BET with −/+ keys · raised SPIN (hold for auto) · MAX BET and AUTO. */
@Composable
fun ControlDeck(
    totalBet: Long, betLocked: Boolean, spinEnabled: Boolean, freeSpins: Int, autoLeft: Int,
    onMinus: () -> Unit, onPlus: () -> Unit, onSpin: () -> Unit, onAuto: () -> Unit, onMaxBet: () -> Unit,
) {
    Box(Modifier.fillMaxWidth().height(150.dp)) {
        Box(
            Modifier.fillMaxSize().shadow(14.dp).background(DeckPurple)
                .drawBehind { drawRect(GoldEdge, Offset.Zero, androidx.compose.ui.geometry.Size(size.width, 4.dp.toPx())) },
        )
        // total bet
        Column(
            Modifier.align(Alignment.CenterStart).padding(start = 10.dp, top = 8.dp).width(128.dp).height(84.dp)
                .background(Color(0xFF14052A), RoundedCornerShape(14.dp)).border(2.dp, GoldEdge, RoundedCornerShape(14.dp)).padding(top = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("TOTAL BET", style = style(9.sp, Skin.TextSoft, FontWeight.ExtraBold, 2.sp))
            Text(formatCoins(totalBet), style = style(20.sp, Color(0xFFFFE27A)), maxLines = 1)
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                DeckKey("−", !betLocked, onMinus); DeckKey("+", !betLocked, onPlus)
            }
        }
        // max bet + auto
        Column(
            Modifier.align(Alignment.CenterEnd).padding(end = 10.dp, top = 8.dp).width(120.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GlossyButton("MAX BET", Color(0xFFFF8A3D), Color(0xFFE0420E), Modifier.fillMaxWidth().height(50.dp), 16.sp, !betLocked, onMaxBet)
            GlossyButton(
                if (autoLeft > 0) "STOP $autoLeft" else "AUTO",
                if (autoLeft > 0) Color(0xFFFF5C9A) else Color(0xFF9B5CFF), if (autoLeft > 0) Color(0xFFC2185B) else Color(0xFF5A1FC2),
                Modifier.fillMaxWidth().height(36.dp), 13.sp, true, onAuto,
            )
        }
        // spin, raised above the deck
        CabinetSpin(
            if (freeSpins > 0) "FREE" else "SPIN", if (freeSpins > 0) "$freeSpins LEFT" else "HOLD FOR AUTO",
            spinEnabled, Modifier.align(Alignment.TopCenter).offset(y = (-26).dp).size(124.dp), onSpin, onAuto,
        )
    }
}

@Composable
private fun DeckKey(label: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.width(44.dp).height(24.dp).alpha(if (enabled) 1f else 0.45f).shadow(3.dp, RoundedCornerShape(8.dp))
            .background(GoldMetal, RoundedCornerShape(8.dp)).border(1.dp, Color(0xFF8A5208), RoundedCornerShape(8.dp))
            .clickable(remember { MutableInteractionSource() }, null, enabled = enabled) { Haptics.tap(); onClick() },
        contentAlignment = Alignment.Center,
    ) { Text(label, style = style(17.sp, Color(0xFF3A1800))) }
}

@Composable
private fun GlossyButton(text: String, top: Color, bottom: Color, modifier: Modifier, size: TextUnit, enabled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier.alpha(if (enabled) 1f else 0.5f).shadow(8.dp, shape).background(Brush.verticalGradient(listOf(top, bottom)), shape)
            .border(2.dp, Color(0xFFFFE08A), shape)
            .clickable(remember { MutableInteractionSource() }, null, enabled = enabled) { Haptics.tap(); onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.fillMaxSize().padding(3.dp).background(Brush.verticalGradient(0f to Color(0x66FFFFFF), 0.5f to Color(0x10FFFFFF), 0.52f to Color.Transparent), shape))
        Text(text, style = style(size, Color.White).copy(shadow = androidx.compose.ui.graphics.Shadow(Color(0x99000000), Offset(0f, 3f), 2f)))
    }
}

/** Big round SPIN in a gold ring; tap to spin, hold to start auto-spin. */
@Composable
private fun CabinetSpin(label: String, sub: String, enabled: Boolean, modifier: Modifier, onSpin: () -> Unit, onHold: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.93f else 1f, label = "spin")
    val glow by rememberInfiniteTransition(label = "spinGlow").animateFloat(
        0.4f, 0.9f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "g",
    )
    Box(
        modifier.scale(scale).alpha(if (enabled) 1f else 0.6f)
            .shadow(18.dp, CircleShape, ambientColor = Color(0xFF50FF8C).copy(alpha = glow), spotColor = Color(0xFF50FF8C).copy(alpha = glow))
            .background(GoldMetal, CircleShape).border(1.5.dp, Color(0xFF6A3A00), CircleShape).padding(7.dp)
            .background(Brush.radialGradient(listOf(Color(0xFF8DFFB0), Color(0xFF1FBF55), Color(0xFF0B6E2E))), CircleShape)
            .pointerInput(enabled) {
                detectTapGestures(
                    onPress = { pressed = true; tryAwaitRelease(); pressed = false },
                    onTap = { if (enabled) { Haptics.tap(); onSpin() } },
                    onLongPress = { onHold() },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.fillMaxSize().padding(6.dp).background(Brush.verticalGradient(0f to Color(0x77FFFFFF), 0.45f to Color(0x11FFFFFF), 0.5f to Color.Transparent), CircleShape))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = style(28.sp, Color.White).copy(shadow = androidx.compose.ui.graphics.Shadow(Color(0xFF0B5A24), Offset(0f, 4f), 1f)))
            Text(sub, style = style(8.sp, Color.White, FontWeight.ExtraBold, 1.sp))
        }
    }
}
