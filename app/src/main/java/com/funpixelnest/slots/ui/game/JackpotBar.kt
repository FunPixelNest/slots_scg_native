package com.funpixelnest.slots.ui.game

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.draw.shadow
import com.funpixelnest.slots.ui.common.Montserrat
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funpixelnest.slots.data.ThemeAssets
import com.funpixelnest.slots.engine.JackpotTier
import com.funpixelnest.slots.ui.skin.Skin
import com.funpixelnest.slots.ui.common.formatCoins
import kotlinx.coroutines.delay
import kotlin.random.Random

private val TIER_COLORS = mapOf(
    JackpotTier.MINI to Color(0xFF38D6FF),
    JackpotTier.MINOR to Color(0xFF3BE07A),
    JackpotTier.MAJOR to Color(0xFFB59CFF),
    JackpotTier.GRAND to Color(0xFFFF3D6E),
)

/** Gold text with a dark outline: readable on any machine background. */
@Composable
fun GoldText(text: String, size: TextUnit, modifier: Modifier = Modifier, family: FontFamily = Montserrat, italic: Boolean = false) {
    val base = TextStyle(
        fontFamily = family, fontWeight = FontWeight.Black, fontSize = size,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal, textAlign = TextAlign.Center,
    )
    Box(modifier, contentAlignment = Alignment.Center) {
        Text(text, maxLines = 1, style = base.copy(color = Color(0xFF2A1000), drawStyle = Stroke(width = size.value * 0.42f, join = StrokeJoin.Round)))
        Text(
            text, maxLines = 1,
            style = base.copy(brush = Brush.verticalGradient(listOf(Color(0xFFFFFBE0), Color(0xFFFFE27A), Color(0xFFF5B728), Color(0xFFD9850E)))),
        )
    }
}

/** Progressive jackpots: GRAND marquee with chasing bulbs over MAJOR · MINOR · MINI badges. */
@Composable
fun JackpotBar(vm: GameViewModel, @Suppress("UNUSED_PARAMETER") assets: ThemeAssets) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        JackpotMarquee(creeping(JackpotTier.GRAND, vm.jackpots[JackpotTier.GRAND] ?: 0L), Modifier.fillMaxWidth(0.9f).height(72.dp).align(Alignment.CenterHorizontally))
        Row(Modifier.fillMaxWidth().height(44.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            JackpotBadge("MAJOR", creeping(JackpotTier.MAJOR, vm.jackpots[JackpotTier.MAJOR] ?: 0L), Color(0xFFE3D2FF), Color(0xFF5B2BB0), Color(0xFF2E0F66), Modifier.weight(1f).fillMaxHeight())
            JackpotBadge("MINOR", creeping(JackpotTier.MINOR, vm.jackpots[JackpotTier.MINOR] ?: 0L), Color(0xFFC8FFD6), Color(0xFF1F8A44), Color(0xFF0B4A22), Modifier.weight(1f).fillMaxHeight())
            JackpotBadge("MINI", creeping(JackpotTier.MINI, vm.jackpots[JackpotTier.MINI] ?: 0L), Color(0xFFC8F4FF), Color(0xFF1F6FB0), Color(0xFF0B3A66), Modifier.weight(1f).fillMaxHeight())
        }
    }
}

/** Shown value creeps upwards between spins, like a networked progressive. */
@Composable
private fun creeping(tier: JackpotTier, value: Long): Long {
    var shown by remember { mutableLongStateOf(value) }
    LaunchedEffect(value) {
        shown = value
        val rnd = Random(tier.ordinal)
        while (true) {
            delay(400L + rnd.nextLong(400))
            shown += (value / 20_000L).coerceAtLeast(1L) * (1 + rnd.nextInt(5))
        }
    }
    return shown
}
