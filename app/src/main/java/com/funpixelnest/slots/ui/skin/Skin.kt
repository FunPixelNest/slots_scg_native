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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funpixelnest.slots.ui.common.CoinIcon
import com.funpixelnest.slots.ui.common.formatCompact
import com.funpixelnest.slots.ui.common.outlinedText

/**
 * "Vegas Night" skin shared by every screen: midnight background with soft neon glow,
 * frosted-glass panels with hairline borders, clean Montserrat type, gold only for coins,
 * jackpots and the main call to action. Each machine keeps its own Unity art for
 * background, title, reels and symbols; everything around it is drawn here.
 */
object Skin {
    // surfaces (names kept from the first skin so every screen picks up the new look)
    val VelvetTop = Color(0xFF161D38)
    val Velvet = Color(0xFF0E1324)
    val VelvetDeep = Color(0xFF070B16)
    val Glass = Color(0x0FFFFFFF)
    val Hairline = Color(0x1FFFFFFF)
    // accents
    val GoldLight = Color(0xFFFFE9A8)
    val Gold = Color(0xFFF5C451)
    val GoldDark = Color(0xFFD9952B)
    val GoldDeep = Color(0xFF6B4A0E)
    val Ruby = Color(0xFFFF3D8B)      // neon pink
    val Emerald = Color(0xFF22C55E)
    val Sapphire = Color(0xFF38D6FF)  // neon cyan
    val Amethyst = Color(0xFF7C5CFF)
    val TextSoft = Color(0xFF9AA3B8)

    val goldBorder = Brush.verticalGradient(listOf(Color(0xCCFFE9A8), Color(0x99F5C451)))
    val goldFill = Brush.verticalGradient(listOf(GoldLight, Gold, GoldDark))
    val velvetFill = Brush.verticalGradient(listOf(Color(0xFF141A30), Color(0xFF0E1324)))
    val hairline = Brush.verticalGradient(listOf(Hairline, Hairline))
}

enum class SkinTone(val top: Color, val bottom: Color, val text: Color) {
    Gold(Color(0xFFFFD978), Color(0xFFF0B43C), Color(0xFF2A1800)),
    Green(Color(0xFF3BE07A), Color(0xFF16A34A), Color(0xFF03230F)),
    Red(Color(0xFFFF5C9A), Color(0xFFE11D63), Color.White),
    Purple(Color(0xFF9B7BFF), Color(0xFF6D3BEF), Color.White),
    Blue(Color(0xFF5FE0FF), Color(0xFF2563EB), Color.White),
    Glass(Color(0x1AFFFFFF), Color(0x0DFFFFFF), Color(0xFFF4F6FB)),
}

/** Frosted-glass card. */
@Composable
fun SkinPanel(
    modifier: Modifier = Modifier,
    corner: Dp = 24.dp,
    padding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(corner)
    Box(
        modifier
            .shadow(24.dp, shape, ambientColor = Color.Black, spotColor = Color.Black)
            .background(Skin.velvetFill, shape)
            .border(1.dp, Skin.Hairline, shape)
            .padding(padding),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

/** Hairline gold frame around [content] (machine art, reels). */
@Composable
fun GoldFrame(modifier: Modifier = Modifier, corner: Dp = 18.dp, content: @Composable BoxScope.() -> Unit) {
    val shape = RoundedCornerShape(corner)
    Box(
        modifier
            .shadow(16.dp, shape)
            .clip(shape)
            .background(Skin.VelvetDeep)
            .border(1.2.dp, Skin.goldBorder, shape),
        content = content,
    )
}

/** Flat gradient button with a soft glow; Glass tone for secondary actions. */
@Composable
fun SkinButton(
    text: String,
    modifier: Modifier = Modifier,
    tone: SkinTone = SkinTone.Gold,
    enabled: Boolean = true,
    textSize: TextUnit = 15.sp,
    shape: Shape = RoundedCornerShape(14.dp),
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f, label = "press")
    Box(
        modifier
            .scale(scale)
            .alpha(if (enabled) 1f else 0.45f)
            .then(if (tone == SkinTone.Glass) Modifier else Modifier.shadow(10.dp, shape, ambientColor = tone.bottom, spotColor = tone.bottom))
            .background(Brush.verticalGradient(listOf(tone.top, tone.bottom)), shape)
            .then(if (tone == SkinTone.Glass) Modifier.border(1.dp, Skin.Hairline, shape) else Modifier)
            .clickable(interaction, indication = null, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            style = outlinedText(textSize, tone.text).copy(shadow = null, fontWeight = FontWeight.ExtraBold),
            modifier = Modifier.padding(horizontal = 8.dp),
            maxLines = 2,
        )
    }
}

enum class SkinIcon { Menu, Back, Info, Gear, Plus, Minus, Clock, Gift, Video, Lock, Trophy, Close }

/** Rounded glass icon button; [tone] tints the icon. */
@Composable
fun IconRoundButton(
    icon: SkinIcon,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    tone: SkinTone = SkinTone.Glass,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.9f else 1f, label = "press")
    val shape = RoundedCornerShape(size * 0.34f)
    val glass = tone == SkinTone.Glass || tone == SkinTone.Purple || tone == SkinTone.Blue
    Box(
        modifier
            .size(size)
            .scale(scale)
            .alpha(if (enabled) 1f else 0.4f)
            .background(
                if (glass) Brush.verticalGradient(listOf(Color(0x1FFFFFFF), Color(0x0DFFFFFF)))
                else Brush.verticalGradient(listOf(tone.top, tone.bottom)),
                shape,
            )
            .border(1.dp, Skin.Hairline, shape)
            .clickable(interaction, indication = null, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        val tint = when (tone) {
            SkinTone.Blue -> Skin.Sapphire
            SkinTone.Purple -> Color(0xFFB9A6FF)
            SkinTone.Glass -> Color(0xFFF4F6FB)
            else -> tone.text
        }
        Canvas(Modifier.fillMaxSize(0.44f)) { drawSkinIcon(icon, tint) }
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


/** Avatar ring: level number in the middle, XP progress as a cyan→violet arc around it. */
@Composable
fun LevelMedallion(level: Int, progress: Float, modifier: Modifier = Modifier, size: Dp = 52.dp) {
    val anim by animateFloatAsState((progress / 100f).coerceIn(0f, 1f), tween(600), label = "xp")
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val ring = this.size.minDimension * 0.08f
            val tl = Offset(ring / 2, ring / 2)
            val sz = Size(this.size.width - ring, this.size.height - ring)
            drawArc(Color(0x22FFFFFF), -90f, 360f, false, tl, sz, style = Stroke(ring))
            drawArc(
                Brush.sweepGradient(listOf(Skin.Sapphire, Skin.Amethyst, Skin.Sapphire)), -90f, 360f * anim, false, tl, sz,
                style = Stroke(ring, cap = StrokeCap.Round),
            )
            drawCircle(Brush.radialGradient(listOf(Color(0xFF26305A), Color(0xFF121833))), this.size.minDimension / 2 - ring * 1.8f)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("LV", style = outlinedText((size.value * 0.15f).sp, Skin.TextSoft).copy(shadow = null))
            Text("$level", style = outlinedText((size.value * 0.3f).sp, Color.White).copy(shadow = null))
        }
    }
}

/** Slim XP bar: "Level n", percentage and a cyan→violet fill. */
@Composable
fun XpBar(level: Int, progress: Float, modifier: Modifier = Modifier) {
    val anim by animateFloatAsState((progress / 100f).coerceIn(0f, 1f), tween(600), label = "xpbar")
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("LEVEL $level", style = outlinedText(10.sp, Color.White).copy(shadow = null, letterSpacing = 1.sp))
            Spacer(Modifier.weight(1f))
            Text("${(anim * 100).toInt()}%", style = outlinedText(9.sp, Skin.TextSoft).copy(shadow = null))
        }
        Spacer(Modifier.height(4.dp))
        Box(Modifier.fillMaxWidth().height(5.dp).background(Color(0x1FFFFFFF), RoundedCornerShape(50))) {
            Box(
                Modifier.fillMaxHeight().fillMaxWidth(anim.coerceAtLeast(0.03f))
                    .background(Brush.horizontalGradient(listOf(Skin.Sapphire, Skin.Amethyst)), RoundedCornerShape(50))
            )
        }
    }
}

/** Glass balance pill: coin, compact amount, gold "+" that opens the store. */
@Composable
fun CoinCapsule(coins: Long, modifier: Modifier = Modifier, onPlus: (() -> Unit)? = null) {
    Row(
        modifier.height(38.dp)
            .background(Brush.verticalGradient(listOf(Color(0x1FFFFFFF), Color(0x0DFFFFFF))), RoundedCornerShape(50))
            .border(1.dp, Skin.Hairline, RoundedCornerShape(50))
            .padding(start = 6.dp, end = if (onPlus != null) 4.dp else 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoinIcon(Modifier.size(24.dp))
        Spacer(Modifier.width(8.dp))
        Text(formatCompact(coins), style = outlinedText(16.sp, Color.White).copy(shadow = null))
        if (onPlus != null) {
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier.size(30.dp).shadow(8.dp, CircleShape, spotColor = Skin.Gold)
                    .background(Brush.verticalGradient(listOf(Color(0xFFFFD978), Color(0xFFF0B43C))), CircleShape)
                    .clickable(remember { MutableInteractionSource() }, null, onClick = onPlus),
                contentAlignment = Alignment.Center,
            ) { Canvas(Modifier.size(12.dp)) { drawSkinIcon(SkinIcon.Plus, Color(0xFF2A1800)) } }
        }
    }
}

/** Kept for older call sites: medallion + bar. */
@Composable
fun LevelStar(level: Int, progress: Float, modifier: Modifier = Modifier, barWidth: Dp = 70.dp) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        LevelMedallion(level, progress, size = 40.dp)
        XpBar(level, progress, Modifier.width(barWidth).padding(start = 6.dp))
    }
}

/** Neon frame (pink glow) around [content]; used for the GRAND jackpot. */
@Composable
fun MarqueeLights(modifier: Modifier = Modifier, @Suppress("UNUSED_PARAMETER") bulbs: Int = 28, corner: Dp = 18.dp, content: @Composable BoxScope.() -> Unit) {
    val glow by rememberInfiniteTransition(label = "neon").animateFloat(
        0.55f, 1f, infiniteRepeatable(tween(1400), RepeatMode.Reverse), label = "g",
    )
    val shape = RoundedCornerShape(corner)
    Box(
        modifier
            .shadow(18.dp, shape, ambientColor = Skin.Ruby, spotColor = Skin.Ruby)
            .background(Brush.verticalGradient(listOf(Color(0x38FF3D8B), Color(0x10FF3D8B))), shape)
            .border(1.2.dp, Skin.Ruby.copy(alpha = glow), shape)
            .padding(6.dp),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

/** Soft breathing glow (replaces the old sunburst). */
@Composable
fun Sunburst(modifier: Modifier = Modifier, color: Color = Skin.Gold, @Suppress("UNUSED_PARAMETER") rays: Int = 18) {
    val pulse by rememberInfiniteTransition(label = "glow").animateFloat(
        0.7f, 1f, infiniteRepeatable(tween(2600, easing = LinearEasing), RepeatMode.Reverse), label = "p",
    )
    Canvas(modifier) {
        drawCircle(
            Brush.radialGradient(listOf(color.copy(alpha = 0.28f * pulse), Color.Transparent), center, size.minDimension * 0.6f),
            size.minDimension * 0.6f, center,
        )
    }
}

/** Title chip (replaces the old ribbon). */
@Composable
fun Ribbon(text: String, modifier: Modifier = Modifier, tone: SkinTone = SkinTone.Red, textSize: TextUnit = 18.sp) {
    val accent = when (tone) {
        SkinTone.Red -> Skin.Ruby
        SkinTone.Green -> Skin.Emerald
        SkinTone.Purple -> Skin.Amethyst
        SkinTone.Blue -> Skin.Sapphire
        else -> Skin.Gold
    }
    Box(
        modifier
            .background(accent.copy(alpha = 0.16f), RoundedCornerShape(50))
            .border(1.dp, accent.copy(alpha = 0.55f), RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = outlinedText(textSize, Color.White).copy(shadow = null, letterSpacing = 1.sp))
    }
}

/** Midnight backdrop with pink and cyan neon glows. */
@Composable
fun VelvetBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        drawRect(Skin.VelvetDeep)
        drawCircle(
            Brush.radialGradient(listOf(Color(0x47FF3D8B), Color.Transparent), Offset(size.width * 0.85f, -size.height * 0.05f), size.width * 1.1f),
            size.width * 1.1f, Offset(size.width * 0.85f, -size.height * 0.05f),
        )
        drawCircle(
            Brush.radialGradient(listOf(Color(0x2E38D6FF), Color.Transparent), Offset(0f, size.height * 0.12f), size.width * 0.9f),
            size.width * 0.9f, Offset(0f, size.height * 0.12f),
        )
    }
}

/** Glass value read-out: small caption over a big number (bet, win). */
@Composable
fun ValueWell(caption: String, value: String, modifier: Modifier = Modifier, highlight: Boolean = false, footnote: String? = null) {
    Box(modifier.padding(horizontal = 4.dp, vertical = 2.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(caption.uppercase(), style = outlinedText(10.sp, Skin.TextSoft).copy(shadow = null, letterSpacing = 1.5.sp))
            Text(value, style = outlinedText(if (highlight) 26.sp else 22.sp, if (highlight) Skin.Gold else Color.White).copy(shadow = null), maxLines = 1)
            if (footnote != null) Text(footnote, style = outlinedText(9.sp, Skin.TextSoft).copy(shadow = null))
        }
    }
}

/** Round green SPIN button inside a gold ring, with a refresh arrow. */
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
    val scale by animateFloatAsState(if (pressed) 0.93f else 1f, label = "press")
    Box(
        modifier
            .scale(scale)
            .alpha(if (enabled) 1f else 0.55f)
            .clickable(interaction, indication = null, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val r = size.minDimension / 2
            drawCircle(Brush.radialGradient(listOf(Color(0x66F5C451), Color.Transparent), center, r * 1.05f), r)
            drawCircle(Color(0xFF070B16), r * 0.96f)
            drawCircle(Brush.sweepGradient(listOf(Skin.GoldLight, Skin.Gold, Skin.GoldDark, Skin.Gold, Skin.GoldLight)), r * 0.88f)
            drawCircle(Brush.radialGradient(listOf(Color(0xFF3BE07A), Color(0xFF15803D)), center.copy(y = center.y - r * 0.3f), r), r * 0.8f)
            // refresh arrow
            val ar = r * 0.26f
            val c = center.copy(y = center.y - r * 0.12f)
            drawArc(Color.White, 300f, 290f, false, Offset(c.x - ar, c.y - ar), Size(ar * 2, ar * 2), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
            val tip = Offset(c.x + ar * 0.5f, c.y - ar * 0.87f)
            drawPath(Path().apply {
                moveTo(tip.x + r * 0.13f, tip.y - r * 0.02f); lineTo(tip.x - r * 0.02f, tip.y - r * 0.12f); lineTo(tip.x - r * 0.01f, tip.y + r * 0.09f); close()
            }, Color.White)
        }
        Column(Modifier.padding(top = 46.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = outlinedText(13.sp, Color.White).copy(shadow = null, letterSpacing = 2.sp))
            if (sub != null) Text(sub, style = outlinedText(9.sp, Skin.GoldLight).copy(shadow = null))
        }
    }
}

/** Shared dialog card: title chip on a glass panel. */
@Composable
fun SkinDialog(
    title: String,
    modifier: Modifier = Modifier,
    tone: SkinTone = SkinTone.Red,
    sunburst: Boolean = false,
    content: @Composable () -> Unit,
) {
    Box(modifier, contentAlignment = Alignment.TopCenter) {
        if (sunburst) Sunburst(Modifier.matchParentSize().scale(1.5f), color = if (tone == SkinTone.Gold) Skin.Gold else Skin.Ruby)
        SkinPanel(Modifier.fillMaxWidth(), padding = 18.dp) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title.uppercase(), style = outlinedText(20.sp, Color.White).copy(shadow = null, letterSpacing = 2.sp))
                Box(Modifier.padding(top = 8.dp, bottom = 12.dp).width(36.dp).height(3.dp).background(
                    when (tone) { SkinTone.Green -> Skin.Emerald; SkinTone.Purple -> Skin.Amethyst; SkinTone.Blue -> Skin.Sapphire; SkinTone.Gold -> Skin.Gold; else -> Skin.Ruby },
                    RoundedCornerShape(50),
                ))
                content()
            }
        }
    }
}
