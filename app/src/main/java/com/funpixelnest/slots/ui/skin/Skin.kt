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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funpixelnest.slots.ui.common.CoinIcon
import com.funpixelnest.slots.ui.common.Lilita
import com.funpixelnest.slots.ui.common.Montserrat
import com.funpixelnest.slots.ui.common.formatCompact
import com.funpixelnest.slots.ui.common.formatCoins
import com.funpixelnest.slots.ui.common.outlinedText
import kotlin.math.cos
import kotlin.math.sin

/**
 * "Arcade night" skin shared by every screen (same family as BingoFlash): deep-space navy with
 * stars and soft violet rays, navy panels with glowing blue rims, glossy pill buttons with a
 * dark edge, round blue icon balls and gold display lettering with a purple outline. Each
 * machine keeps its own Unity art for background, reels and symbols.
 */
object Skin {
    // surfaces (names kept from earlier skins so every screen picks up the look)
    val VelvetTop = Color(0xFF2A1AA8)
    val Velvet = Color(0xFF14107A)
    val VelvetDeep = Color(0xFF0A0A52)
    val Glass = Color(0x14FFFFFF)
    val Hairline = Color(0xFF6AB4FF)   // glowing blue rim on every panel
    val Ink = Color(0xFF3A0A8A)        // purple outline under gold lettering
    // accents
    val GoldLight = Color(0xFFFFF3A0)
    val Gold = Color(0xFFFFC21A)
    val GoldDark = Color(0xFFE07A00)
    val GoldDeep = Color(0xFF6A3A00)
    val Ruby = Color(0xFFFF3040)
    val Emerald = Color(0xFF2FC93A)
    val Sapphire = Color(0xFF2A7BFF)
    val Amethyst = Color(0xFF9A3AFF)
    val Magenta = Color(0xFFFF5CF4)
    val TextSoft = Color(0xFF9FC4FF)   // light blue captions

    val goldBorder = Brush.verticalGradient(listOf(Color(0xFFBFE2FF), Color(0xFF6AB4FF)))
    val goldFill = Brush.verticalGradient(listOf(Color(0xFFFFFBD0), Color(0xFFFFE23A), Color(0xFFFFA800), Color(0xFFFF7A00)))
    val goldRim = Brush.verticalGradient(listOf(Color(0xFFFFF7C9), Color(0xFFFFD24A), Color(0xFFB86B00), Color(0xFFFFDC6E), Color(0xFF8A4A00)))
    val velvetFill = Brush.verticalGradient(listOf(Color(0xFF1E1EA8), Color(0xFF0C0C62), Color(0xFF090A50)))
    val hairline = Brush.verticalGradient(listOf(Hairline, Hairline))
}

/** Glossy pill colours: light top, mid, dark bottom and the dark edge underneath. */
enum class SkinTone(val top: Color, val bottom: Color, val text: Color, val mid: Color, val edge: Color) {
    Gold(Color(0xFFFFE07A), Color(0xFFE35A00), Color.White, Color(0xFFFFA21A), Color(0xFF7A2A00)),
    Green(Color(0xFF8CFF6A), Color(0xFF0F8A1A), Color.White, Color(0xFF2FC93A), Color(0xFF0A5A10)),
    Red(Color(0xFFFF9A8A), Color(0xFFB00818), Color.White, Color(0xFFFF3040), Color(0xFF6A0008)),
    Purple(Color(0xFFD79AFF), Color(0xFF5A12C8), Color.White, Color(0xFF9A3AFF), Color(0xFF2A0670)),
    Blue(Color(0xFF8AD0FF), Color(0xFF1238C8), Color.White, Color(0xFF2A7BFF), Color(0xFF0A1A7A)),
    Glass(Color(0xFF3A3ACC), Color(0xFF0C0C62), Color.White, Color(0xFF1E1EA8), Color(0xFF060640)),
    Pink(Color(0xFFFF8AF0), Color(0xFF7A0A9A), Color.White, Color(0xFFE03AD0), Color(0xFF3A0A5A)),
}

/** Big display lettering: Lilita One, white (or [color]) with a soft dark drop shadow. */
fun displayText(size: TextUnit, color: Color = Color.White) = TextStyle(
    fontFamily = Lilita, fontWeight = FontWeight.Normal, fontSize = size, color = color, textAlign = TextAlign.Center,
    shadow = Shadow(Color(0x99000030), Offset(0f, size.value * 0.12f), 2f),
)

/** Gold gradient lettering with a thick purple outline (titles, wins, jackpots). */
@Composable
fun GoldDisplay(text: String, size: TextUnit, modifier: Modifier = Modifier, maxLines: Int = 1) {
    val base = TextStyle(fontFamily = Lilita, fontSize = size, textAlign = TextAlign.Center)
    Box(modifier, contentAlignment = Alignment.Center) {
        Text(
            text, maxLines = maxLines,
            style = base.copy(color = Skin.Ink, drawStyle = Stroke(width = size.value * 0.28f, join = StrokeJoin.Round)),
            modifier = Modifier.offset(y = (size.value * 0.06f).dp),
        )
        Text(text, maxLines = maxLines, style = base.copy(color = Skin.Ink, drawStyle = Stroke(width = size.value * 0.24f, join = StrokeJoin.Round)))
        Text(text, maxLines = maxLines, style = base.copy(brush = Skin.goldFill))
    }
}

/** Navy panel with a glowing blue rim. [magenta] swaps the rim for the "selected" pink glow. */
@Composable
fun SkinPanel(
    modifier: Modifier = Modifier,
    corner: Dp = 22.dp,
    padding: Dp = 16.dp,
    magenta: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier.glowPanel(corner, magenta).padding(padding), contentAlignment = Alignment.Center, content = content)
}

/** Navy fill + glowing rim, usable on any layout. */
fun Modifier.glowPanel(corner: Dp = 22.dp, magenta: Boolean = false, fill: Brush = Skin.velvetFill): Modifier {
    val shape = RoundedCornerShape(corner)
    val rim = if (magenta) Skin.Magenta else Skin.Hairline
    return this
        .shadow(14.dp, shape, ambientColor = rim, spotColor = rim)
        .background(fill, shape)
        .border(2.dp, rim, shape)
        .drawBehind {
            // light catching the top edge
            drawRoundRect(
                Brush.verticalGradient(0f to Color(0x55C8E6FF), 0.06f to Color.Transparent),
                cornerRadius = CornerRadius(corner.toPx()),
            )
        }
}

/** Panel around machine art and reels. */
@Composable
fun GoldFrame(modifier: Modifier = Modifier, corner: Dp = 18.dp, content: @Composable BoxScope.() -> Unit) {
    val shape = RoundedCornerShape(corner)
    Box(modifier.glowPanel(corner).clip(shape), content = content)
}

/** Glossy pill button: three-stop gradient, light top band, dark edge below. */
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
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f, label = "press")
    Box(
        modifier
            .scale(scale)
            .alpha(if (enabled) 1f else 0.45f)
            .glossy(tone, shape)
            .clickable(interaction, indication = null, enabled = enabled) { com.funpixelnest.slots.audio.Haptics.tap(); onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = displayText(textSize, tone.text), modifier = Modifier.padding(horizontal = 10.dp), maxLines = 2)
    }
}

/** The glossy pill look on any element. */
fun Modifier.glossy(tone: SkinTone, shape: Shape = RoundedCornerShape(50), glow: Boolean = true): Modifier = this
    .then(if (glow) Modifier.shadow(6.dp, shape, ambientColor = tone.mid, spotColor = tone.edge) else Modifier)
    .background(Brush.verticalGradient(0f to tone.top, 0.55f to tone.mid, 1f to tone.bottom), shape)
    .border(2.dp, tone.edge, shape)
    .drawBehind {
        // top shine band
        val inset = size.height * 0.1f
        drawRoundRect(
            Brush.verticalGradient(listOf(Color(0x88FFFFFF), Color(0x00FFFFFF)), inset, size.height * 0.48f),
            Offset(size.width * 0.08f, inset), Size(size.width * 0.84f, size.height * 0.36f), CornerRadius(size.height * 0.2f),
        )
    }

enum class SkinIcon { Menu, Back, Info, Gear, Plus, Minus, Clock, Gift, Video, Lock, Trophy, Close, Home, Cart, Tasks, Target, Piggy }

/** Glossy round icon ball (blue unless [tone] says otherwise) with a white icon. */
@Composable
fun IconRoundButton(
    icon: SkinIcon,
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    tone: SkinTone = SkinTone.Blue,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.9f else 1f, label = "press")
    val t = if (tone == SkinTone.Glass) SkinTone.Blue else tone
    Box(
        modifier
            .size(size)
            .scale(scale)
            .alpha(if (enabled) 1f else 0.4f)
            .ball(t)
            .clickable(interaction, indication = null, enabled = enabled) { com.funpixelnest.slots.audio.Haptics.tap(); onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize(0.46f)) { drawSkinIcon(icon, Color.White) }
    }
}

/** Glossy ball: radial fill, light inner ring, dark edge, soft glow. */
fun Modifier.ball(tone: SkinTone): Modifier = this
    .shadow(8.dp, CircleShape, ambientColor = tone.mid, spotColor = tone.mid)
    .background(Brush.radialGradient(listOf(tone.top, tone.mid, tone.bottom)), CircleShape)
    .border(2.5.dp, tone.edge, CircleShape)
    .drawBehind {
        drawCircle(tone.top.copy(alpha = 0.8f), size.minDimension / 2 - 3.5.dp.toPx(), style = Stroke(1.5.dp.toPx()))
        drawOval(
            Brush.verticalGradient(listOf(Color(0x99FFFFFF), Color(0x00FFFFFF)), size.height * 0.08f, size.height * 0.46f),
            Offset(size.width * 0.2f, size.height * 0.08f), Size(size.width * 0.6f, size.height * 0.36f),
        )
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
            drawCircle(Skin.VelvetDeep, w * 0.13f, center)
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
        SkinIcon.Home -> {
            val roof = Path().apply { moveTo(0.02f * w, 0.5f * w); lineTo(0.5f * w, 0.06f * w); lineTo(0.98f * w, 0.5f * w); close() }
            drawPath(roof, c)
            drawRoundRect(c, Offset(0.16f * w, 0.42f * w), Size(0.68f * w, 0.54f * w), CornerRadius(w * 0.06f))
            drawRoundRect(Skin.VelvetDeep, Offset(0.4f * w, 0.62f * w), Size(0.2f * w, 0.34f * w), CornerRadius(w * 0.04f))
        }
        SkinIcon.Cart -> {
            line(0.02f, 0.12f, 0.18f, 0.12f)
            val basket = Path().apply {
                moveTo(0.18f * w, 0.12f * w); lineTo(0.95f * w, 0.24f * w); lineTo(0.85f * w, 0.62f * w); lineTo(0.28f * w, 0.62f * w); close()
            }
            drawPath(basket, c)
            drawCircle(c, w * 0.09f, Offset(0.34f * w, 0.85f * w)); drawCircle(c, w * 0.09f, Offset(0.8f * w, 0.85f * w))
        }
        SkinIcon.Tasks -> {
            drawRoundRect(c, Offset(0.12f * w, 0.04f * w), Size(0.76f * w, 0.92f * w), CornerRadius(w * 0.1f), style = Stroke(stroke * 0.7f))
            for (i in 0..2) {
                val y = 0.28f + i * 0.24f
                drawCircle(c, w * 0.05f, Offset(0.32f * w, y * w))
                line(0.46f, y, 0.72f, y)
            }
        }
        SkinIcon.Target -> {
            drawCircle(c, w * 0.44f, Offset(w / 2, w / 2), style = Stroke(stroke * 0.7f))
            drawCircle(c, w * 0.27f, Offset(w / 2, w / 2), style = Stroke(stroke * 0.7f))
            drawCircle(c, w * 0.1f, Offset(w / 2, w / 2))
        }
        SkinIcon.Piggy -> {
            drawOval(c, Offset(0.1f * w, 0.22f * w), Size(0.78f * w, 0.6f * w))
            drawCircle(c, w * 0.1f, Offset(0.9f * w, 0.5f * w))
            val ear = Path().apply { moveTo(0.3f * w, 0.28f * w); lineTo(0.36f * w, 0.08f * w); lineTo(0.5f * w, 0.24f * w); close() }
            drawPath(ear, c)
            drawRect(c, Offset(0.24f * w, 0.72f * w), Size(0.12f * w, 0.22f * w))
            drawRect(c, Offset(0.6f * w, 0.72f * w), Size(0.12f * w, 0.22f * w))
            drawRoundRect(Skin.VelvetDeep, Offset(0.4f * w, 0.26f * w), Size(0.2f * w, 0.05f * w), CornerRadius(w * 0.02f))
            drawCircle(Skin.VelvetDeep, w * 0.04f, Offset(0.7f * w, 0.42f * w))
        }
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


/** Gold five-point star with the level number (HUD). */
@Composable
fun LevelMedallion(level: Int, @Suppress("UNUSED_PARAMETER") progress: Float, modifier: Modifier = Modifier, size: Dp = 42.dp) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val c = Offset(this.size.width / 2, this.size.height / 2 + this.size.height * 0.04f)
            val r = this.size.minDimension / 2
            val star = Path()
            for (i in 0 until 10) {
                val a = -Math.PI / 2 + i * Math.PI / 5
                val rr = if (i % 2 == 0) r else r * 0.5f
                val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
                if (i == 0) star.moveTo(x, y) else star.lineTo(x, y)
            }
            star.close()
            drawPath(star, Brush.verticalGradient(listOf(Color(0xFFFFF3A0), Skin.Gold, Skin.GoldDark)))
            drawPath(star, Color(0xFF7A3A00), style = Stroke(1.2.dp.toPx(), join = StrokeJoin.Round))
        }
        Text(
            "$level",
            style = displayText(if (level < 100) (size.value * 0.36f).sp else (size.value * 0.28f).sp).copy(shadow = Shadow(Color(0xFF7A3A00), Offset(0f, 2f), 0f)),
            modifier = Modifier.offset(y = 2.dp),
        )
    }
}

/** Chunky XP bar that tucks under the level star. */
@Composable
fun XpBar(@Suppress("UNUSED_PARAMETER") level: Int, progress: Float, modifier: Modifier = Modifier) {
    val anim by animateFloatAsState((progress / 100f).coerceIn(0f, 1f), tween(600), label = "xpbar")
    val shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
    Box(
        modifier.height(14.dp).shadow(6.dp, shape, ambientColor = Skin.Hairline, spotColor = Skin.Hairline)
            .background(Color(0xFF070840), shape).border(1.5.dp, Skin.Hairline, shape),
    ) {
        Box(
            Modifier.fillMaxHeight().fillMaxWidth(anim.coerceAtLeast(0.04f)).padding(1.5.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFFB4FF8A), Skin.Emerald)), shape),
        )
    }
}

/** Blue glossy coin bar: big coin overlapping the left end, amount, green "+" for the store. */
@Composable
fun CoinCapsule(coins: Long, modifier: Modifier = Modifier, onPlus: (() -> Unit)? = null) {
    Box(modifier.height(40.dp), contentAlignment = Alignment.CenterStart) {
        Row(
            Modifier.fillMaxSize().padding(start = 8.dp)
                .shadow(8.dp, RoundedCornerShape(50), ambientColor = Skin.Hairline, spotColor = Skin.Hairline)
                .background(Brush.verticalGradient(listOf(Color(0xFF4A6AFF), Color(0xFF2A3AD8), Color(0xFF1A22A8))), RoundedCornerShape(50))
                .border(2.dp, Color(0xFF8AB8FF), RoundedCornerShape(50))
                .padding(start = 34.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(formatCoins(coins), style = displayText(17.sp), maxLines = 1, modifier = Modifier.weight(1f))
            if (onPlus != null) {
                Box(
                    Modifier.size(28.dp).ball(SkinTone.Green)
                        .clickable(remember { MutableInteractionSource() }, null, onClick = onPlus),
                    contentAlignment = Alignment.Center,
                ) { Canvas(Modifier.size(12.dp)) { drawSkinIcon(SkinIcon.Plus, Color.White) } }
            }
        }
        BigCoin(Modifier.size(44.dp))
    }
}

/** Gold coin with a star, ringed in light gold (coin bar, rewards). */
@Composable
fun BigCoin(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val r = size.minDimension / 2
        drawCircle(Color(0xFFA04A00), r)
        drawCircle(Color(0xFFFFEB8A), r * 0.93f)
        drawCircle(Brush.radialGradient(listOf(Color(0xFFFFF3A0), Skin.Gold, Skin.GoldDark), center.copy(x = center.x - r * 0.25f, y = center.y - r * 0.3f), r * 1.2f), r * 0.82f)
        val star = Path()
        for (i in 0 until 10) {
            val a = -Math.PI / 2 + i * Math.PI / 5
            val rr = if (i % 2 == 0) r * 0.48f else r * 0.22f
            val x = center.x + (rr * cos(a)).toFloat(); val y = center.y + (rr * sin(a)).toFloat()
            if (i == 0) star.moveTo(x, y) else star.lineTo(x, y)
        }
        star.close()
        drawPath(star, Color(0xFFFFE46A))
        drawPath(star, Color(0xFFC46A00), style = Stroke(r * 0.06f, join = StrokeJoin.Round))
    }
}

/** Level star with the XP bar tucked under its right point. */
@Composable
fun LevelStar(level: Int, progress: Float, modifier: Modifier = Modifier, barWidth: Dp = 64.dp) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        LevelMedallion(level, progress, Modifier.offset(x = 0.dp), size = 40.dp)
        XpBar(level, progress, Modifier.width(barWidth).offset(x = (-8).dp))
    }
}

/** Light-bulb sign: ruby fill, gold rim, a ring of glowing bulbs that chase. */
@Composable
fun MarqueeLights(modifier: Modifier = Modifier, bulbs: Int = 28, corner: Dp = 16.dp, content: @Composable BoxScope.() -> Unit) {
    val chase by rememberInfiniteTransition(label = "bulbs").animateFloat(
        0f, 1f, infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart), label = "c",
    )
    val shape = RoundedCornerShape(corner)
    Box(
        modifier
            .shadow(16.dp, shape, ambientColor = Skin.Gold, spotColor = Skin.GoldDark)
            .background(Skin.goldRim, shape).padding(3.dp)
            .background(Brush.radialGradient(listOf(Color(0xFFC4123A), Color(0xFF5A0420)), radius = 600f), RoundedCornerShape(corner - 3.dp))
            .drawBehind {
                // bulbs along the inner edge
                val inset = 6.dp.toPx()
                val w = size.width - 2 * inset
                val h = size.height - 2 * inset
                val per = (2 * (w + h)) / bulbs
                val phase = (chase * 2).toInt() % 2
                for (i in 0 until bulbs) {
                    var d = i * per
                    val p = when {
                        d < w -> Offset(inset + d, inset)
                        d < w + h -> { d -= w; Offset(inset + w, inset + d) }
                        d < 2 * w + h -> { d -= w + h; Offset(inset + w - d, inset + h) }
                        else -> { d -= 2 * w + h; Offset(inset, inset + h - d) }
                    }
                    val lit = i % 2 == phase
                    if (lit) drawCircle(Color(0x66FFCC33), 5.dp.toPx(), p)
                    drawCircle(if (lit) Color(0xFFFFF8D0) else Color(0xFFB07A2A), 2.dp.toPx(), p)
                }
            }
            .padding(6.dp),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

/** Slowly turning violet light rays fading out from the centre (behind titles and prizes). */
@Composable
fun Sunburst(modifier: Modifier = Modifier, color: Color = Color(0xFFC890FF), rays: Int = 22) {
    val turn by rememberInfiniteTransition(label = "rays").animateFloat(
        0f, 360f, infiniteRepeatable(tween(40_000, easing = LinearEasing)), label = "t",
    )
    Canvas(modifier.rotate(turn)) {
        val r = size.maxDimension * 0.75f
        val step = 360f / rays
        for (i in 0 until rays) {
            val a0 = Math.toRadians((i * step).toDouble())
            val a1 = Math.toRadians((i * step + step * 0.4f).toDouble())
            val p = Path().apply {
                moveTo(center.x, center.y)
                lineTo(center.x + (r * cos(a0)).toFloat(), center.y + (r * sin(a0)).toFloat())
                lineTo(center.x + (r * cos(a1)).toFloat(), center.y + (r * sin(a1)).toFloat())
                close()
            }
            drawPath(p, Brush.radialGradient(listOf(color.copy(alpha = 0.42f), color.copy(alpha = 0f)), center, r * 0.62f))
        }
    }
}

/** Small glossy tag (feature name, badges). */
@Composable
fun Ribbon(text: String, modifier: Modifier = Modifier, tone: SkinTone = SkinTone.Gold, textSize: TextUnit = 14.sp) {
    Box(modifier.glossy(tone).padding(horizontal = 14.dp, vertical = 3.dp), contentAlignment = Alignment.Center) {
        Text(text, style = displayText(textSize), maxLines = 1)
    }
}

/** Deep-space background: blue to navy, violet glow and rays at the top, a field of stars. */
@Composable
fun VelvetBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        drawRect(Brush.verticalGradient(0f to Color(0xFF2A1AA8), 0.35f to Color(0xFF14107A), 0.7f to Color(0xFF0A0A52), 1f to Color(0xFF070838)))
        val top = Offset(size.width / 2, size.height * 0.08f)
        drawCircle(Brush.radialGradient(listOf(Color(0x8C8C5AFF), Color.Transparent), top, size.width * 0.9f), size.width * 0.9f, top)
        val r = size.maxDimension
        for (i in 0 until 28) {
            val a0 = Math.toRadians(i * 360.0 / 28)
            val a1 = Math.toRadians(i * 360.0 / 28 + 4.5)
            drawPath(
                Path().apply {
                    moveTo(top.x, top.y)
                    lineTo(top.x + (r * cos(a0)).toFloat(), top.y + (r * sin(a0)).toFloat())
                    lineTo(top.x + (r * cos(a1)).toFloat(), top.y + (r * sin(a1)).toFloat()); close()
                },
                Brush.radialGradient(listOf(Color(0x1F9678FF), Color.Transparent), top, r * 0.8f),
            )
        }
        val bottom = Offset(size.width / 2, size.height)
        drawCircle(Brush.radialGradient(listOf(Color(0x733C78FF), Color.Transparent), bottom, size.width * 0.8f), size.width * 0.8f, bottom)
        // stars (fixed pattern)
        val rnd = kotlin.random.Random(7)
        repeat(90) {
            val p = Offset(rnd.nextFloat() * size.width, rnd.nextFloat() * size.height)
            val gold = it % 5 == 0
            drawCircle(if (gold) Color(0xE6FFDC78) else Color(0xB3FFFFFF), (if (gold) 1.6f else 1.1f).dp.toPx() * (0.6f + rnd.nextFloat() * 0.6f), p)
        }
    }
}

/** Caption over a value in a navy glow pill (bet, win). */
@Composable
fun ValueWell(caption: String, value: String, modifier: Modifier = Modifier, highlight: Boolean = false, footnote: String? = null) {
    Box(modifier.glowPanel(18.dp).padding(horizontal = 10.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(caption.uppercase(), style = outlinedText(10.sp, Skin.TextSoft).copy(shadow = null, letterSpacing = 1.5.sp))
            if (highlight) GoldDisplay(value, 24.sp) else Text(value, style = displayText(20.sp), maxLines = 1)
            if (footnote != null) Text(footnote, style = outlinedText(9.sp, Skin.TextSoft).copy(shadow = null))
        }
    }
}

/** Rectangular SPIN: gold frame, glossy green face, big label and a small caption. */
@Composable
fun SpinButton(
    label: String,
    modifier: Modifier = Modifier,
    sub: String? = null,
    enabled: Boolean = true,
    tone: SkinTone = SkinTone.Green,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f, label = "press")
    SpinFace(
        label, sub, tone,
        modifier.scale(scale).alpha(if (enabled) 1f else 0.55f)
            .clickable(interaction, indication = null, enabled = enabled) { com.funpixelnest.slots.audio.Haptics.tap(); onClick() },
    )
}

/** The look of the rectangular SPIN, without input handling (the game screen adds tap + hold). */
@Composable
fun SpinFace(label: String, sub: String?, tone: SkinTone, modifier: Modifier) {
    val glow by rememberInfiniteTransition(label = "spinGlow").animateFloat(
        0.55f, 1f, infiniteRepeatable(tween(1100), RepeatMode.Reverse), label = "g",
    )
    val outer = RoundedCornerShape(22.dp)
    val inner = RoundedCornerShape(17.dp)
    Box(
        modifier
            .shadow(18.dp, outer, ambientColor = Skin.Gold.copy(alpha = glow), spotColor = Skin.Gold.copy(alpha = glow))
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF3A0), Skin.Gold, Skin.GoldDark, Color(0xFFFFE46A))), outer)
            .border(2.dp, Skin.Ink, outer)
            .padding(5.dp)
            .glossy(tone, inner, glow = false),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = displayText(if (label.length <= 5) 36.sp else 26.sp).copy(shadow = Shadow(tone.edge, Offset(0f, 5f), 0f)), maxLines = 1)
            if (sub != null) Text(sub, style = TextStyle(fontFamily = Montserrat, fontWeight = FontWeight.Black, fontSize = 9.sp, color = Color.White, letterSpacing = 1.sp, shadow = Shadow(tone.edge, Offset(0f, 2f), 0f)))
        }
    }
}

/** Popup frame: violet rays behind, gold title riding on the top edge of a glowing navy panel. */
@Composable
fun SkinDialog(
    title: String,
    modifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER") tone: SkinTone = SkinTone.Red,
    sunburst: Boolean = false,
    onClose: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Box(modifier, contentAlignment = Alignment.TopCenter) {
        if (sunburst) Sunburst(Modifier.matchParentSize().scale(1.6f))
        SkinPanel(Modifier.fillMaxWidth().padding(top = 22.dp), padding = 16.dp, corner = 26.dp) {
            Column(Modifier.fillMaxWidth().padding(top = 26.dp), horizontalAlignment = Alignment.CenterHorizontally) { content() }
        }
        GoldDisplay(title.uppercase(), 32.sp)
        if (onClose != null) {
            IconRoundButton(
                SkinIcon.Close, Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = 10.dp), size = 38.dp, tone = SkinTone.Red, onClick = onClose,
            )
        }
    }
}

/**
 * Rewarded-video button: magenta glossy bar in a gold-pink-blue frame, white play disc,
 * "WATCH VIDEO & GET" over the doubled [amount], small AD tag and a gold x2 starburst.
 */
@Composable
fun Watch2xButton(amount: Long, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.96f else 1f, label = "press")
    val shine by rememberInfiniteTransition(label = "shine").animateFloat(
        -0.3f, 1.3f, infiniteRepeatable(tween(2200, easing = LinearEasing)), label = "s",
    )
    Box(
        modifier.height(66.dp).scale(scale).alpha(if (enabled) 1f else 0.5f)
            .clickable(interaction, indication = null, enabled = enabled) { com.funpixelnest.slots.audio.Haptics.tap(); onClick() },
    ) {
        val outer = RoundedCornerShape(20.dp)
        Box(
            Modifier.fillMaxSize().padding(top = 4.dp, end = 10.dp)
                .shadow(14.dp, outer, ambientColor = Skin.Magenta, spotColor = Skin.Magenta)
                .background(Brush.horizontalGradient(listOf(Color(0xFFFFE46A), Color(0xFFFF9AF0), Color(0xFF8AD0FF), Color(0xFFFFE46A))), outer)
                .border(2.dp, Color(0xFF3A0A5A), outer)
                .padding(3.dp)
                .glossy(SkinTone.Pink, RoundedCornerShape(17.dp), glow = false)
                .drawBehind {
                    val x = size.width * shine
                    drawPath(
                        Path().apply {
                            moveTo(x, 0f); lineTo(x + size.height * 0.35f, 0f); lineTo(x + size.height * 0.05f, size.height); lineTo(x - size.height * 0.3f, size.height); close()
                        },
                        Brush.horizontalGradient(listOf(Color.Transparent, Color(0x66FFFFFF), Color.Transparent), x - size.height * 0.3f, x + size.height * 0.35f),
                    )
                }
                .padding(start = 8.dp, end = 66.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(42.dp).shadow(4.dp, CircleShape)
                        .background(Brush.radialGradient(listOf(Color.White, Color(0xFFFFE0FA))), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(Modifier.size(20.dp).offset(x = 2.dp)) {
                        drawPath(Path().apply { moveTo(size.width * 0.15f, 0f); lineTo(size.width, size.height / 2); lineTo(size.width * 0.15f, size.height); close() }, Color(0xFFC41AB8))
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "WATCH VIDEO & GET",
                            style = TextStyle(fontFamily = Montserrat, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.sp, color = Color(0xFFFFF0FC), shadow = Shadow(Color(0x8C3C0050), Offset(0f, 1.5f), 3f)),
                            maxLines = 1,
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "AD", style = TextStyle(fontFamily = Montserrat, fontWeight = FontWeight.Black, fontSize = 8.sp, color = Color.White),
                            modifier = Modifier.background(Color(0xFF5A0A6A), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 1.dp),
                        )
                    }
                    Text("${formatCoins(amount)} COINS", style = displayText(22.sp).copy(textAlign = TextAlign.Start, shadow = Shadow(Color(0xFF5A0A6A), Offset(0f, 3f), 0f)), maxLines = 1)
                }
            }
        }
        X2Burst(Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-10).dp).size(74.dp).rotate(12f))
    }
}

/** Gold starburst badge with "x2". */
@Composable
fun X2Burst(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val r = size.minDimension / 2
            val p = Path()
            for (i in 0 until 24) {
                val a = -Math.PI / 2 + i * Math.PI / 12
                val rr = if (i % 2 == 0) r else r * 0.78f
                val x = center.x + (rr * cos(a)).toFloat(); val y = center.y + (rr * sin(a)).toFloat()
                if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            p.close()
            drawPath(p, Color(0x66FFB000), style = Stroke(r * 0.18f, join = StrokeJoin.Round))
            drawPath(p, Color(0xFF7A3A00), style = Stroke(r * 0.07f, join = StrokeJoin.Round))
            drawPath(p, Brush.radialGradient(listOf(Color(0xFFFFF6B0), Skin.Gold, Skin.GoldDark), center.copy(x = center.x - r * 0.1f, y = center.y - r * 0.2f), r))
        }
        Box(contentAlignment = Alignment.Center) {
            Text("x2", style = TextStyle(fontFamily = Lilita, fontSize = 28.sp, color = Color(0xFFA03A00), drawStyle = Stroke(width = 9f, join = StrokeJoin.Round)))
            Text("x2", style = TextStyle(fontFamily = Lilita, fontSize = 28.sp, color = Color.White))
        }
    }
}

/** Compact glowing chip for numbers inside panels ("PICKS LEFT 2"). */
@Composable
fun StatChip(caption: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier.glowPanel(16.dp).padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(caption.uppercase(), style = outlinedText(11.sp, Skin.TextSoft).copy(shadow = null, letterSpacing = 1.sp))
        Spacer(Modifier.width(8.dp))
        GoldDisplay(value, 20.sp)
    }
}

/** Kept for older call sites: compact amount in a glow chip. */
@Composable
fun CompactCoins(coins: Long, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        CoinIcon(Modifier.size(20.dp)); Spacer(Modifier.width(4.dp))
        Text(formatCompact(coins), style = displayText(15.sp))
    }
}
