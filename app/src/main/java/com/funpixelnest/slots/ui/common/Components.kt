package com.funpixelnest.slots.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funpixelnest.slots.R
import com.funpixelnest.slots.data.ThemeAssets
import java.text.NumberFormat

val Montserrat = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold),
    Font(R.font.montserrat_bold, FontWeight.Bold),
    Font(R.font.montserrat_extrabold, FontWeight.ExtraBold),
    Font(R.font.montserrat_black, FontWeight.Black),
)

/** Display fonts that shipped with individual Unity themes. */
val ThemeFonts: Map<String, FontFamily> = mapOf(
    "christmas_eve" to FontFamily(Font(R.font.coiny_regular)),
    "valentines_day" to FontFamily(Font(R.font.lilitaone_regular)),
    "witch" to FontFamily(Font(R.font.corben_bold, FontWeight.Bold)),
)

fun themeFont(id: String): FontFamily = ThemeFonts[id] ?: Montserrat

val Gold = Color(0xFFFFD54F)

fun formatCoins(v: Long): String = NumberFormat.getIntegerInstance().format(v)

fun outlinedText(size: TextUnit, color: Color = Color.White, family: FontFamily = Montserrat) = TextStyle(
    fontFamily = family,
    fontWeight = FontWeight.ExtraBold,
    fontSize = size,
    color = color,
    textAlign = TextAlign.Center,
    shadow = Shadow(Color.Black, offset = androidx.compose.ui.geometry.Offset(0f, 2.5f), blurRadius = 3f),
)

@Composable
fun rememberImage(assets: ThemeAssets, key: String?): ImageBitmap? {
    if (key == null) return null
    val img by produceState(assets.cached(key), assets, key) { if (value == null) value = assets.load(key) }
    return img
}

/** An image from a theme (or the common folder); draws nothing while loading or if missing. */
@Composable
fun AssetImage(
    assets: ThemeAssets,
    key: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = 1f,
) {
    val img = rememberImage(assets, key) ?: return Box(modifier)
    Image(img, contentDescription = null, modifier = modifier, contentScale = contentScale, alpha = alpha)
}

/**
 * Button using the theme's button art when it exists, otherwise a drawn fallback.
 * The label is drawn on top either way (Unity buttons had separate text).
 */
@Composable
fun ArtButton(
    assets: ThemeAssets,
    key: String?,
    modifier: Modifier = Modifier,
    label: String? = null,
    labelSize: TextUnit = 16.sp,
    enabled: Boolean = true,
    fallbackColor: Color = Color(0xFF7B1FA2),
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.92f else 1f, label = "press")
    val img = rememberImage(assets, key)
    Box(
        modifier
            .scale(scale)
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(interaction, indication = null, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (img != null) {
            Image(img, null, Modifier.matchParentSize(), contentScale = ContentScale.FillBounds)
        } else {
            Box(
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(listOf(fallbackColor.copy(alpha = 0.95f), fallbackColor.darken())),
                        RoundedCornerShape(50),
                    )
                    .border(2.dp, Gold, RoundedCornerShape(50))
            )
        }
        if (label != null) Text(label, style = outlinedText(labelSize), modifier = Modifier.padding(horizontal = 6.dp))
    }
}

private fun Color.darken(f: Float = 0.55f) = Color(red * f, green * f, blue * f, alpha)

/** Panel used behind popups when a theme has no matching art. */
@Composable
fun FallbackPanel(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier
            .background(
                Brush.verticalGradient(listOf(Color(0xFF3A1060), Color(0xFF14052A))),
                RoundedCornerShape(24.dp),
            )
            .border(3.dp, Brush.verticalGradient(listOf(Gold, Color(0xFFB8860B))), RoundedCornerShape(24.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

/** 1,234 -> "1,234"; 1,250,000 -> "1.25M"; 3,400,000,000 -> "3.4B" (social-casino style). */
fun formatCompact(v: Long): String {
    val a = kotlin.math.abs(v)
    fun f(d: Double, s: String): String {
        val digits = if (d >= 100) 0 else if (d >= 10) 1 else 2
        val t = String.format(java.util.Locale.US, "%.${digits}f", d)
        return (if (digits > 0) t.trimEnd('0').trimEnd('.') else t) + s
    }
    return when {
        a >= 1_000_000_000_000L -> f(v / 1e12, "T")
        a >= 1_000_000_000L -> f(v / 1e9, "B")
        a >= 1_000_000L -> f(v / 1e6, "M")
        a >= 100_000L -> f(v / 1e3, "K")
        else -> formatCoins(v)
    }
}

/** A drawn gold coin, used in the balance HUD, bonuses and the coin shower. */
@Composable
fun CoinIcon(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier) { drawCoin(center, size.minDimension / 2f, 0f) }
}

/** Gold coin seen at an angle ([turn] 0..1 spins it around its vertical axis). */
fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCoin(c: androidx.compose.ui.geometry.Offset, r: Float, turn: Float) {
    val squeeze = kotlin.math.abs(kotlin.math.cos(turn * Math.PI.toFloat())).coerceAtLeast(0.15f)
    val size = androidx.compose.ui.geometry.Size(2 * r * squeeze, 2 * r)
    val tl = androidx.compose.ui.geometry.Offset(c.x - size.width / 2, c.y - r)
    drawOval(Color(0xFF8D5A00), tl.copy(y = tl.y + r * 0.08f), size)
    drawOval(
        Brush.radialGradient(listOf(Color(0xFFFFF59D), Color(0xFFFFC107), Color(0xFFE08A00)), c.copy(x = c.x - r * 0.3f * squeeze, y = c.y - r * 0.3f), r * 1.4f),
        tl, size,
    )
    val inner = androidx.compose.ui.geometry.Size(size.width * 0.72f, size.height * 0.72f)
    drawOval(
        Color(0xFFB8860B), androidx.compose.ui.geometry.Offset(c.x - inner.width / 2, c.y - inner.height / 2), inner,
        style = androidx.compose.ui.graphics.drawscope.Stroke(r * 0.1f),
    )
}

/**
 * Coins bursting out and raining down after a win; [trigger] changes start a new shower,
 * [count] scales with the win size.
 */
@Composable
fun CoinShower(trigger: Int, count: Int, modifier: Modifier = Modifier) {
    if (trigger == 0 || count <= 0) return
    val progress = remember(trigger) { androidx.compose.animation.core.Animatable(0f) }
    androidx.compose.runtime.LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(1f, androidx.compose.animation.core.tween(2200, easing = androidx.compose.animation.core.LinearEasing))
    }
    val coins = remember(trigger, count) {
        val rnd = kotlin.random.Random(trigger)
        List(count) {
            floatArrayOf(
                rnd.nextFloat(),                 // start x (fraction)
                -0.6f + rnd.nextFloat() * 1.2f,  // horizontal drift
                0.7f + rnd.nextFloat() * 0.9f,   // upward kick
                rnd.nextFloat() * 0.35f,         // delay
                0.6f + rnd.nextFloat() * 0.8f,   // size
                rnd.nextFloat(),                 // spin phase
            )
        }
    }
    androidx.compose.foundation.Canvas(modifier) {
        val p = progress.value
        if (p >= 1f) return@Canvas
        val r = size.minDimension * 0.024f
        for (c in coins) {
            val t = ((p - c[3]) / (1f - c[3])).coerceIn(0f, 1f)
            if (t <= 0f) continue
            // launched from the bottom middle, arc up, fall under gravity
            val x = size.width * (0.5f + (c[0] - 0.5f) * 0.4f + c[1] * t * 0.6f)
            val y = size.height * (1.05f - c[2] * 2.2f * t + 2.6f * t * t)
            drawCoin(androidx.compose.ui.geometry.Offset(x, y), r * c[4], c[5] + t * 4f)
        }
    }
}
