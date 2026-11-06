package com.funpixelnest.slots.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp

/** Accent colour of each machine's board (frame, glow, win highlight). */
fun boardAccent(themeId: String): Color = when (themeId) {
    "christmas_eve" -> Color(0xFFE5394F)
    "valentines_day" -> Color(0xFFFF5FA2)
    "witch" -> Color(0xFF9B5CFF)
    "modern" -> Color(0xFFF5C451)
    "forest" -> Color(0xFF52C46B)
    "neon" -> Color(0xFF38D6FF)
    "underwater" -> Color(0xFF2EC4D6)
    "wolf" -> Color(0xFF7FB4FF)
    "candy" -> Color(0xFFFF7EB6)
    "pirates" -> Color(0xFFF2A33A)
    "tropical" -> Color(0xFF3FD3A0)
    "gold_mine" -> Color(0xFFF5B041)
    "farm" -> Color(0xFF8BD450)
    "most_wanted" -> Color(0xFFD98A4A)
    "gems" -> Color(0xFFB37BFF)
    "egypt" -> Color(0xFFF5C451)
    "egypt_2" -> Color(0xFF3FA7FF)
    "china" -> Color(0xFFE84A3A)
    "greek" -> Color(0xFF5AA9FF)
    "viking" -> Color(0xFF8FB8D8)
    "india" -> Color(0xFFFF9F2E)
    "arabian" -> Color(0xFFC77DFF)
    "wild_west" -> Color(0xFFD9904A)
    else -> Color(0xFFF5C451)
}

private val GoldHi = Color(0xFFFFE7A3)
private val GoldLo = Color(0xFF8A5A12)

/**
 * The reel board shared by every machine: a metallic gold/accent rim with a soft coloured glow,
 * a deep glass well inside, and room for the reels.
 */
@Composable
fun ReelBoard(accent: Color, modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val outer = RoundedCornerShape(22.dp)
    val inner = RoundedCornerShape(18.dp)
    Box(
        modifier
            .shadow(22.dp, outer, ambientColor = accent, spotColor = accent)
            .background(
                Brush.linearGradient(listOf(GoldHi, lerp(accent, GoldHi, 0.15f), GoldLo, lerp(accent, GoldLo, 0.3f), GoldHi)),
                outer,
            )
            .padding(3.dp)
            .background(Color(0xFF0E0220), inner)
            .padding(1.dp)
            .clip(inner)
            .background(
                Brush.verticalGradient(
                    listOf(lerp(Color(0xFF331058), accent, 0.12f), Color(0xFF1C0738), lerp(Color(0xFF1C0738), accent, 0.08f)),
                ),
            )
            .border(1.dp, accent.copy(alpha = 0.35f), inner),
    ) {
        Box(Modifier.fillMaxSize().padding(5.dp), content = content)
    }
}
