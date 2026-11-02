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
    shadow = Shadow(Color.Black.copy(alpha = 0.85f), blurRadius = 6f),
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
