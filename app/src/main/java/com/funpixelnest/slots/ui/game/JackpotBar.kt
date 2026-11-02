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
import com.funpixelnest.slots.ui.skin.MarqueeLights
import com.funpixelnest.slots.ui.skin.Skin
import com.funpixelnest.slots.ui.common.formatCoins
import com.funpixelnest.slots.ui.common.outlinedText
import com.funpixelnest.slots.ui.common.themeFont
import kotlinx.coroutines.delay
import kotlin.random.Random

private val TIER_COLORS = mapOf(
    JackpotTier.MINI to listOf(Color(0xFF26C6DA), Color(0xFF00695C)),
    JackpotTier.MINOR to listOf(Color(0xFF66BB6A), Color(0xFF1B5E20)),
    JackpotTier.MAJOR to listOf(Color(0xFFAB47BC), Color(0xFF4A148C)),
    JackpotTier.GRAND to listOf(Color(0xFFE53935), Color(0xFF6A0D0D)),
)

/** Progressive jackpots for the portrait layout: a wide GRAND meter over MAJOR · MINOR · MINI. */
@Composable
fun JackpotBar(vm: GameViewModel, assets: ThemeAssets) {
    val font = themeFont(assets.config.id)
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        // GRAND sits in a light-bulb marquee
        MarqueeLights(Modifier.fillMaxWidth().height(62.dp), bulbs = 34) {
            JackpotMeter(JackpotTier.GRAND, vm.jackpots[JackpotTier.GRAND] ?: 0L, font, Modifier.fillMaxSize())
        }
        Row(Modifier.fillMaxWidth().height(42.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(JackpotTier.MAJOR, JackpotTier.MINOR, JackpotTier.MINI).forEach { t ->
                JackpotMeter(t, vm.jackpots[t] ?: 0L, font, Modifier.weight(1f).fillMaxHeight())
            }
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
        modifier
            .background(Skin.goldBorder, shape)
            .padding(2.dp)
            .background(Brush.verticalGradient(listOf(colors[0], colors[1])), shape)
            .border(1.5.dp, Color(0xFFFFFDE7).copy(alpha = glow * 0.7f), shape),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(if (tier == JackpotTier.GRAND) "GRAND JACKPOT" else tier.label, style = outlinedText(if (tier == JackpotTier.GRAND) 12.sp else 10.sp, Color.White, font))
            Text(formatCoins(shown), style = outlinedText(if (tier == JackpotTier.GRAND) 22.sp else 14.sp, Color(0xFFFFF8E1)), maxLines = 1)
        }
    }
}
