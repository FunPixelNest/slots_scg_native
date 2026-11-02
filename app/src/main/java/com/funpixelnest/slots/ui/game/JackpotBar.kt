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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funpixelnest.slots.data.ThemeAssets
import com.funpixelnest.slots.engine.JackpotTier
import com.funpixelnest.slots.ui.common.formatCoins
import com.funpixelnest.slots.ui.common.outlinedText
import com.funpixelnest.slots.ui.common.themeFont
import kotlinx.coroutines.delay
import kotlin.random.Random

private val TIER_COLORS = mapOf(
    JackpotTier.MINI to listOf(Color(0xFF26C6DA), Color(0xFF00695C)),
    JackpotTier.MINOR to listOf(Color(0xFF66BB6A), Color(0xFF1B5E20)),
    JackpotTier.MAJOR to listOf(Color(0xFFAB47BC), Color(0xFF4A148C)),
    JackpotTier.GRAND to listOf(Color(0xFFFFD54F), Color(0xFFE65100)),
)

/** Mini / Minor / Major / Grand progressive jackpot meters, ticking up like a live pool. */
@Composable
fun JackpotBar(vm: GameViewModel, assets: ThemeAssets) {
    val font = themeFont(assets.config.id)
    Row(
        Modifier.fillMaxWidth().height(40.dp).padding(horizontal = 24.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        JackpotTier.entries.forEach { t ->
            JackpotMeter(t, vm.jackpots[t] ?: 0L, font, Modifier.weight(if (t == JackpotTier.GRAND) 1.35f else 1f))
        }
    }
}

@Composable
private fun JackpotMeter(tier: JackpotTier, value: Long, font: androidx.compose.ui.text.font.FontFamily, modifier: Modifier) {
    // the shown value creeps upwards between spins, like a networked progressive
    var shown by remember { mutableLongStateOf(value) }
    LaunchedEffect(value) {
        shown = value
        val rnd = Random(tier.ordinal)
        while (true) {
            delay(400L + rnd.nextLong(400))
            shown += (value / 20_000L).coerceAtLeast(1L) * (1 + rnd.nextInt(5))
        }
    }
    val glow by rememberInfiniteTransition(label = "jp").animateFloat(
        0.55f, 1f, infiniteRepeatable(tween(900 + tier.ordinal * 150), RepeatMode.Reverse), label = "glow",
    )
    val colors = TIER_COLORS.getValue(tier)
    val shape = RoundedCornerShape(50)
    Box(
        modifier.fillMaxHeight()
            .background(Brush.verticalGradient(listOf(colors[0], colors[1])), shape)
            .border(2.dp, Color(0xFFFFE082).copy(alpha = glow), shape),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(tier.label, style = outlinedText(10.sp, Color.White, font))
            Text(formatCoins(shown), style = outlinedText(if (tier == JackpotTier.GRAND) 16.sp else 13.sp, Color(0xFFFFF8E1)), maxLines = 1)
        }
    }
}
