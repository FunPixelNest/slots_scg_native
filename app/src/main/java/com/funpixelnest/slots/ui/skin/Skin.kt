package com.funpixelnest.slots.ui.skin

import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funpixelnest.slots.ui.common.CoinIcon
import com.funpixelnest.slots.ui.common.formatCompact
import com.funpixelnest.slots.ui.common.outlinedText
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * "Royal Velvet" skin shared by every screen: deep velvet panels, bevelled gold frames,
 * glossy capsule buttons and marquee lights. Each machine keeps its own Unity art for
 * background, title, reels and symbols; everything around it is drawn here.
 */
object Skin {
    val VelvetTop = Color(0xFF3B0D5C)
    val Velvet = Color(0xFF240640)
    val VelvetDeep = Color(0xFF12021F)
    val GoldLight = Color(0xFFFFF1A8)
    val Gold = Color(0xFFFFC93C)
    val GoldDark = Color(0xFFB4720B)
    val GoldDeep = Color(0xFF6B3D00)
    val Ruby = Color(0xFFE53935)
    val Emerald = Color(0xFF2EBD59)
    val Sapphire = Color(0xFF2D7DF6)
    val Amethyst = Color(0xFF9B4DFF)
    val TextSoft = Color(0xFFEADCF7)

    val goldBorder = Brush.verticalGradient(listOf(GoldLight, Gold, GoldDark, Gold, GoldLight))
    val goldFill = Brush.verticalGradient(listOf(GoldLight, Gold, GoldDark))
    val velvetFill = Brush.verticalGradient(listOf(VelvetTop, Velvet, VelvetDeep))
}

enum class SkinTone(val top: Color, val bottom: Color, val text: Color) {
    Gold(Color(0xFFFFE27A), Color(0xFFE08A00), Color(0xFF4A2100)),
    Green(Color(0xFF7CF29A), Color(0xFF138A3A), Color.White),
    Red(Color(0xFFFF8A80), Color(0xFFB71C1C), Color.White),
    Purple(Color(0xFFC59BFF), Color(0xFF5B1FA8), Color.White),
    Blue(Color(0xFF8CC8FF), Color(0xFF1652B8), Color.White),
}

/** Velvet panel with a bevelled double gold frame. */
@Composable
fun SkinPanel(
    modifier: Modifier = Modifier,
    corner: Dp = 22.dp,
    padding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(corner)
    Box(
        modifier
            .shadow(14.dp, shape, ambientColor = Color.Black, spotColor = Color.Black)
            .background(Skin.goldBorder, shape)
            .padding(3.dp)
            .background(Skin.GoldDeep, RoundedCornerShape(corner - 3.dp))
            .padding(1.5.dp)
            .background(Skin.velvetFill, RoundedCornerShape(corner - 4.dp))
            .drawBehind { // soft top sheen
                drawRoundRect(
                    Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.10f), Color.Transparent), 0f, size.height * 0.35f),
                    cornerRadius = CornerRadius((corner - 4.dp).toPx()),
                )
            }
            .padding(padding),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

/** Thin gold frame around [content] (used to wrap a machine's own reel art). */
@Composable
fun GoldFrame(modifier: Modifier = Modifier, corner: Dp = 16.dp, content: @Composable BoxScope.() -> Unit) {
    val shape = RoundedCornerShape(corner)
    Box(
        modifier
            .shadow(10.dp, shape)
            .background(Skin.goldBorder, shape)
            .padding(4.dp)
            .clip(RoundedCornerShape(corner - 4.dp))
            .background(Skin.VelvetDeep),
        content = content,
    )
}

/** Glossy capsule button with a press-down animation. */
@Composable
fun SkinButton(
    text: String,
    modifier: Modifier = Modifier,
    tone: SkinTone = SkinTone.Gold,
    enabled: Boolean = true,
    textSize: TextUnit = 16.sp,
    shape: Shape = RoundedCornerShape(50),
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.93f else 1f, label = "press")
    Box(
        modifier
            .scale(scale)
            .alpha(if (enabled) 1f else 0.45f)
            .shadow(6.dp, shape)
            .background(Skin.goldBorder, shape)
            .padding(2.5.dp)
            .background(Brush.verticalGradient(listOf(tone.top, tone.bottom)), shape)
            .drawBehind { // gloss on the upper half
                val h = size.height
                drawRoundRect(
                    Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.55f), Color.White.copy(alpha = 0.05f)), 0f, h * 0.5f),
                    topLeft = Offset(h * 0.18f, h * 0.06f),
                    size = Size(size.width - h * 0.36f, h * 0.42f),
                    cornerRadius = CornerRadius(h * 0.25f),
                )
            }
            .clickable(interaction, indication = null, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            style = if (tone == SkinTone.Gold) outlinedText(textSize, tone.text).copy(shadow = null)
            else outlinedText(textSize, tone.text),
            modifier = Modifier.padding(horizontal = 8.dp),
            maxLines = 2,
        )
    }
}

enum class SkinIcon { Menu, Back, Info, Gear, Plus, Minus, Clock, Gift, Video, Lock, Trophy, Close }

/** Small round gold-rimmed icon button (menu, info, settings, +/-). */
@Composable
fun IconRoundButton(
    icon: SkinIcon,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    tone: SkinTone = SkinTone.Purple,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.9f else 1f, label = "press")
    Box(
        modifier
            .size(size)
            .scale(scale)
            .alpha(if (enabled) 1f else 0.45f)
            .shadow(5.dp, CircleShape)
            .background(Skin.goldBorder, CircleShape)
            .padding(2.5.dp)
            .background(Brush.radialGradient(listOf(tone.top, tone.bottom)), CircleShape)
            .clickable(interaction, indication = null, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize(0.5f)) { drawSkinIcon(icon) }
    }
}

fun DrawScope.drawSkinIcon(icon: SkinIcon, c: Color = Color.White) {
    val w = size.width
    val stroke = w * 0.16f
    fun line(x1: Float, y1: Float, x2: Float, y2: Float) =
        drawLine(c, Offset(x1 * w, y1 * w), Offset(x2 * w, y2 * w), stroke, StrokeCap.Round)
    when (icon) {
        SkinIcon.Menu -> { line(0.1f, 0.2f, 0.9f, 0.2f); line(0.1f, 0.5f, 0.9f, 0.5f); line(0.1f, 0.8f, 0.9f, 0.8f) }
        SkinIcon.Back -> { line(0.65f, 0.1f, 0.25f, 0.5f); line(0.25f, 0.5f, 0.65f, 0.9f) }
        SkinIcon.Plus -> { line(0.5f, 0.1f, 0.5f, 0.9f); line(0.1f, 0.5f, 0.9f, 0.5f) }
        SkinIcon.Minus -> line(0.1f, 0.5f, 0.9f, 0.5f)
        SkinIcon.Info -> {
            drawCircle(c, stroke * 0.7f, Offset(0.5f * w, 0.14f * w))
            line(0.5f, 0.42f, 0.5f, 0.92f)
        }
        SkinIcon.Gear -> {
            val center = Offset(w / 2, w / 2)
            for (i in 0 until 8) rotate(i * 45f, center) {
                drawRoundRect(c, Offset(w * 0.42f, 0f), Size(w * 0.16f, w * 0.3f), CornerRadius(w * 0.04f))
            }
            drawCircle(c, w * 0.32f, center)
            drawCircle(Color(0xFF5B1FA8), w * 0.13f, center)
        }
        SkinIcon.Clock -> {
            drawCircle(c, w * 0.45f, Offset(w / 2, w / 2), style = Stroke(stroke * 0.8f))
            line(0.5f, 0.5f, 0.5f, 0.22f); line(0.5f, 0.5f, 0.72f, 0.6f)
        }
        SkinIcon.Gift -> {
            drawRoundRect(c, Offset(0.08f * w, 0.42f * w), Size(0.84f * w, 0.55f * w), CornerRadius(w * 0.06f))
            drawRoundRect(c, Offset(0f, 0.26f * w), Size(w, 0.2f * w), CornerRadius(w * 0.05f))
            drawRect(Color(0xFFE53935), Offset(0.42f * w, 0.26f * w), Size(0.16f * w, 0.71f * w))
            drawOval(c, Offset(0.18f * w, 0.02f * w), Size(0.3f * w, 0.26f * w), style = Stroke(stroke * 0.7f))
            drawOval(c, Offset(0.52f * w, 0.02f * w), Size(0.3f * w, 0.26f * w), style = Stroke(stroke * 0.7f))
        }
        SkinIcon.Video -> {
            drawRoundRect(c, Offset(0f, 0.18f * w), Size(w, 0.64f * w), CornerRadius(w * 0.14f))
            val tri = Path().apply {
                moveTo(0.4f * w, 0.33f * w); lineTo(0.68f * w, 0.5f * w); lineTo(0.4f * w, 0.67f * w); close()
            }
            drawPath(tri, Color(0xFFE53935))
        }
        SkinIcon.Lock -> drawPadlock(Offset(0f, 0f), w, c)
        SkinIcon.Close -> { line(0.15f, 0.15f, 0.85f, 0.85f); line(0.85f, 0.15f, 0.15f, 0.85f) }
        SkinIcon.Trophy -> {
            val cup = Path().apply {
                moveTo(0.15f * w, 0.05f * w); lineTo(0.85f * w, 0.05f * w)
                quadraticBezierTo(0.85f * w, 0.6f * w, 0.5f * w, 0.62f * w)
                quadraticBezierTo(0.15f * w, 0.6f * w, 0.15f * w, 0.05f * w); close()
            }
            drawPath(cup, c)
            line(0.5f, 0.62f, 0.5f, 0.82f)
            drawRoundRect(c, Offset(0.25f * w, 0.82f * w), Size(0.5f * w, 0.14f * w), CornerRadius(w * 0.04f))
        }
    }
}

/** Gold padlock (shackle + body + keyhole) inside a [size]-wide square at [topLeft]. */
fun DrawScope.drawPadlock(topLeft: Offset, size: Float, c: Color = Skin.Gold) {
    val x = topLeft.x
    val y = topLeft.y
    drawArc(
        c, 180f, 180f, false, Offset(x + size * 0.22f, y + size * 0.04f), Size(size * 0.56f, size * 0.6f),
        style = Stroke(size * 0.12f, cap = StrokeCap.Round),
    )
    drawLine(c, Offset(x + size * 0.22f, y + size * 0.34f), Offset(x + size * 0.22f, y + size * 0.46f), size * 0.12f)
    drawLine(c, Offset(x + size * 0.78f, y + size * 0.34f), Offset(x + size * 0.78f, y + size * 0.46f), size * 0.12f)
    drawRoundRect(c, Offset(x + size * 0.1f, y + size * 0.42f), Size(size * 0.8f, size * 0.56f), CornerRadius(size * 0.1f))
    drawCircle(Skin.GoldDeep, size * 0.08f, Offset(x + size * 0.5f, y + size * 0.62f))
    drawRect(Skin.GoldDeep, Offset(x + size * 0.47f, y + size * 0.64f), Size(size * 0.06f, size * 0.18f))
}

/**
 * Player medallion: velvet disc with the level number, ringed by a circular XP bar
 * that fills towards the next level.
 */
@Composable
fun LevelMedallion(level: Int, progress: Float, modifier: Modifier = Modifier, size: Dp = 52.dp) {
    val anim by animateFloatAsState((progress / 100f).coerceIn(0f, 1f), tween(600), label = "xp")
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val r = this.size.minDimension / 2
            val ring = r * 0.2f
            drawCircle(Color.Black.copy(alpha = 0.5f), r)
            drawArc(
                Color(0xFF2A0846), -90f, 360f, false,
                Offset(ring / 2, ring / 2), Size(this.size.width - ring, this.size.height - ring), style = Stroke(ring),
            )
            drawArc(
                Brush.sweepGradient(listOf(Color(0xFF7CF29A), Color(0xFF16A34A), Color(0xFF7CF29A))), -90f, 360f * anim, false,
                Offset(ring / 2, ring / 2), Size(this.size.width - ring, this.size.height - ring), style = Stroke(ring, cap = StrokeCap.Round),
            )
            drawCircle(Skin.goldBorder, r - ring * 1.05f)
            drawCircle(Brush.radialGradient(listOf(Color(0xFF8E44E8), Color(0xFF3B0D5C))), r - ring * 1.35f)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("LV", style = outlinedText((size.value * 0.16f).sp, Skin.GoldLight))
            Text("$level", style = outlinedText((size.value * 0.3f).sp, Color.White))
        }
    }
}

/** Slim XP bar with "LEVEL n" and percentage, used next to the medallion. */
@Composable
fun XpBar(level: Int, progress: Float, modifier: Modifier = Modifier) {
    val anim by animateFloatAsState((progress / 100f).coerceIn(0f, 1f), tween(600), label = "xpbar")
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("LEVEL $level", style = outlinedText(10.sp, Skin.GoldLight))
            Spacer(Modifier.weight(1f))
            Text("${(anim * 100).toInt()}%", style = outlinedText(9.sp, Skin.TextSoft))
        }
        Box(
            Modifier.fillMaxWidth().height(12.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(50))
                .border(1.5.dp, Skin.goldBorder, RoundedCornerShape(50))
                .padding(2.dp)
        ) {
            Box(
                Modifier.fillMaxHeight().fillMaxWidth(anim.coerceAtLeast(0.03f))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF7CF29A), Color(0xFF16A34A))), RoundedCornerShape(50))
            )
        }
    }
}

/** Balance capsule: spinning coin, compact amount, optional gold "+" (opens bonuses). */
@Composable
fun CoinCapsule(coins: Long, modifier: Modifier = Modifier, onPlus: (() -> Unit)? = null) {
    Row(
        modifier
            .height(40.dp)
            .shadow(6.dp, RoundedCornerShape(50))
            .background(Skin.goldBorder, RoundedCornerShape(50))
            .padding(2.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF1E0533), Color(0xFF0B0114))), RoundedCornerShape(50))
            .padding(start = 2.dp, end = if (onPlus != null) 2.dp else 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoinIcon(Modifier.size(34.dp))
        Spacer(Modifier.width(6.dp))
        Text(formatCompact(coins), style = outlinedText(19.sp, Skin.GoldLight))
        if (onPlus != null) {
            Spacer(Modifier.width(8.dp))
            IconRoundButton(SkinIcon.Plus, size = 32.dp, tone = SkinTone.Green, onClick = onPlus)
        }
    }
}

/** Gold star with the level number and an XP bar beside it. */
@Composable
fun LevelStar(level: Int, progress: Float, modifier: Modifier = Modifier, barWidth: Dp = 70.dp) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(42.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val star = starPath(size.width / 2, size.height / 2, size.minDimension * 0.5f, size.minDimension * 0.24f)
                drawPath(star, Skin.goldFill)
                drawPath(star, Skin.GoldDeep, style = Stroke(size.minDimension * 0.05f))
            }
            Text("$level", style = outlinedText(13.sp))
        }
        Column(Modifier.padding(start = 2.dp)) {
            Text("LEVEL", style = outlinedText(8.sp, Skin.TextSoft))
            Box(
                Modifier.width(barWidth).height(11.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(50))
                    .border(1.5.dp, Skin.goldBorder, RoundedCornerShape(50))
                    .padding(2.dp)
            ) {
                Box(
                    Modifier.fillMaxHeight().fillMaxWidth((progress / 100f).coerceIn(0.02f, 1f))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF7CF29A), Color(0xFF16A34A))), RoundedCornerShape(50))
                )
            }
        }
    }
}

private fun starPath(cx: Float, cy: Float, outer: Float, inner: Float): Path = Path().apply {
    for (i in 0 until 10) {
        val r = if (i % 2 == 0) outer else inner
        val a = -PI / 2 + i * PI / 5
        val x = cx + (r * cos(a)).toFloat()
        val y = cy + (r * sin(a)).toFloat()
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

/** Chasing light bulbs around the edge of [content] (jackpot marquee). */
@Composable
fun MarqueeLights(modifier: Modifier = Modifier, bulbs: Int = 28, corner: Dp = 18.dp, content: @Composable BoxScope.() -> Unit) {
    val phase by rememberInfiniteTransition(label = "marquee").animateFloat(
        0f, 3f, infiniteRepeatable(tween(900, easing = LinearEasing)), label = "phase",
    )
    Box(
        modifier.drawBehind {
            val r = corner.toPx()
            drawRoundRect(Skin.goldBorder, cornerRadius = CornerRadius(r))
            drawRoundRect(
                Skin.GoldDeep, topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
                size = Size(size.width - 6.dp.toPx(), size.height - 6.dp.toPx()), cornerRadius = CornerRadius(r - 3.dp.toPx()),
            )
            // bulbs along the rectangle's perimeter
            val inset = 7.dp.toPx()
            val w = size.width - 2 * inset
            val h = size.height - 2 * inset
            val per = 2 * (w + h)
            for (i in 0 until bulbs) {
                var d = per * i / bulbs
                val p = when {
                    d < w -> Offset(inset + d, inset)
                    d < w + h -> { d -= w; Offset(inset + w, inset + d) }
                    d < 2 * w + h -> { d -= w + h; Offset(inset + w - d, inset + h) }
                    else -> { d -= 2 * w + h; Offset(inset, inset + h - d) }
                }
                val lit = (i + phase.toInt()) % 3 == 0
                drawCircle(if (lit) Color(0xFFFFFDE7) else Color(0xFFB98A2A), 2.6.dp.toPx(), p)
                if (lit) drawCircle(Color(0x66FFF59D), 5.dp.toPx(), p)
            }
        }.padding(10.dp),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

/** Slowly turning sunburst used behind the lobby header and big-win popups. */
@Composable
fun Sunburst(modifier: Modifier = Modifier, color: Color = Skin.Gold, rays: Int = 18) {
    val angle by rememberInfiniteTransition(label = "sun").animateFloat(
        0f, 360f, infiniteRepeatable(tween(40_000, easing = LinearEasing), RepeatMode.Restart), label = "a",
    )
    Canvas(modifier.rotate(angle)) {
        val c = center
        val r = size.maxDimension
        val step = 360f / rays
        for (i in 0 until rays) {
            val a1 = Math.toRadians((i * step).toDouble())
            val a2 = Math.toRadians((i * step + step / 2).toDouble())
            val path = Path().apply {
                moveTo(c.x, c.y)
                lineTo(c.x + (r * cos(a1)).toFloat(), c.y + (r * sin(a1)).toFloat())
                lineTo(c.x + (r * cos(a2)).toFloat(), c.y + (r * sin(a2)).toFloat())
                close()
            }
            drawPath(path, Brush.radialGradient(listOf(color.copy(alpha = 0.22f), Color.Transparent), c, r * 0.7f))
        }
    }
}

/** Ribbon banner with a title, e.g. on top of popups. */
@Composable
fun Ribbon(text: String, modifier: Modifier = Modifier, tone: SkinTone = SkinTone.Red, textSize: TextUnit = 20.sp) {
    Box(
        modifier
            .shadow(6.dp, RoundedCornerShape(10.dp))
            .background(Skin.goldBorder, RoundedCornerShape(10.dp))
            .padding(2.5.dp)
            .background(Brush.verticalGradient(listOf(tone.top, tone.bottom)), RoundedCornerShape(8.dp))
            .padding(horizontal = 22.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = outlinedText(textSize, Color.White))
    }
}

/** Velvet backdrop for the lobby: radial glow plus a faint diamond lattice. */
@Composable
fun VelvetBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        drawRect(Brush.radialGradient(listOf(Color(0xFF4E1580), Skin.Velvet, Skin.VelvetDeep), Offset(size.width / 2, size.height * 0.25f), size.maxDimension * 0.9f))
        val step = 46.dp.toPx()
        val lattice = Color.White.copy(alpha = 0.035f)
        var x = -size.height
        while (x < size.width + size.height) {
            drawLine(lattice, Offset(x, 0f), Offset(x + size.height, size.height), 1.dp.toPx())
            drawLine(lattice, Offset(x + size.height, 0f), Offset(x, size.height), 1.dp.toPx())
            x += step
        }
    }
}

/** Gold "value" well: small caption over a big number (bet, win). */
@Composable
fun ValueWell(caption: String, value: String, modifier: Modifier = Modifier, highlight: Boolean = false, footnote: String? = null) {
    Box(
        modifier
            .background(Skin.goldBorder, RoundedCornerShape(14.dp))
            .padding(2.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF0B0114), Color(0xFF240640))), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(caption, style = outlinedText(9.sp, Skin.TextSoft))
            Text(value, style = outlinedText(if (highlight) 22.sp else 20.sp, if (highlight) Skin.GoldLight else Color.White), maxLines = 1)
            if (footnote != null) Text(footnote, style = outlinedText(8.sp, Color(0xFFFFCC80)))
        }
    }
}

/** Round SPIN button: gold rim, glossy ruby dome, rotating light ring while idle. */
@Composable
fun SpinButton(
    label: String,
    modifier: Modifier = Modifier,
    sub: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.92f else 1f, label = "press")
    val ring by rememberInfiniteTransition(label = "ring").animateFloat(
        0f, 360f, infiniteRepeatable(tween(2600, easing = LinearEasing)), label = "ring",
    )
    Box(
        modifier
            .scale(scale)
            .alpha(if (enabled) 1f else 0.6f)
            .clickable(interaction, indication = null, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val r = size.minDimension / 2
            drawCircle(Color.Black.copy(alpha = 0.45f), r, center.copy(y = center.y + r * 0.06f))
            drawCircle(Skin.goldFill, r)
            drawCircle(Skin.GoldDeep, r * 0.86f)
            // light ring
            val n = 16
            for (i in 0 until n) {
                val a = Math.toRadians((ring + i * 360.0 / n))
                val p = Offset(center.x + (r * 0.93f * cos(a)).toFloat(), center.y + (r * 0.93f * sin(a)).toFloat())
                val lit = i % 2 == 0
                drawCircle(if (lit && enabled) Color(0xFFFFFDE7) else Color(0xFFB98A2A), r * 0.045f, p)
            }
            drawCircle(Brush.radialGradient(listOf(Color(0xFFFF6E62), Color(0xFFC62828), Color(0xFF7F0000)), center.copy(y = center.y - r * 0.2f), r * 0.95f), r * 0.82f)
            drawOval(
                Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.6f), Color.Transparent), center.y - r * 0.75f, center.y),
                topLeft = Offset(center.x - r * 0.55f, center.y - r * 0.72f), size = Size(r * 1.1f, r * 0.62f),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = outlinedText(26.sp, Color.White))
            if (sub != null) Text(sub, style = outlinedText(11.sp, Skin.GoldLight))
        }
    }
}

/** Shared "dialog" wrapper used by every popup in the new skin. */
@Composable
fun SkinDialog(
    title: String,
    modifier: Modifier = Modifier,
    tone: SkinTone = SkinTone.Red,
    sunburst: Boolean = false,
    content: @Composable () -> Unit,
) {
    Box(modifier, contentAlignment = Alignment.TopCenter) {
        if (sunburst) Sunburst(Modifier.matchParentSize().scale(1.6f))
        SkinPanel(Modifier.padding(top = 20.dp).fillMaxWidth(), padding = 18.dp) {
            Column(
                Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) { content() }
        }
        Ribbon(title, tone = tone)
    }
}
