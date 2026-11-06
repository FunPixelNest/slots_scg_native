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

/** Progressive jackpots: a solid GRAND plate over MAJOR · MINOR · MINI, all opaque so they read on any art. */
@Composable
fun JackpotBar(vm: GameViewModel, @Suppress("UNUSED_PARAMETER") assets: ThemeAssets) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        GrandPlate(vm.jackpots[JackpotTier.GRAND] ?: 0L, Modifier.fillMaxWidth(0.88f).height(66.dp).align(Alignment.CenterHorizontally))
        Row(Modifier.fillMaxWidth().height(44.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(JackpotTier.MAJOR, JackpotTier.MINOR, JackpotTier.MINI).forEach { t ->
                SmallPlate(t, vm.jackpots[t] ?: 0L, Modifier.weight(1f).fillMaxHeight())
            }
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

@Composable
private fun GrandPlate(value: Long, modifier: Modifier) {
    val shown = creeping(JackpotTier.GRAND, value)
    val glow by rememberInfiniteTransition(label = "grand").animateFloat(
        0.5f, 1f, infiniteRepeatable(tween(1100), RepeatMode.Reverse), label = "g",
    )
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier
            .shadow(16.dp, shape, ambientColor = Skin.Gold, spotColor = Skin.Gold)
            .background(Brush.linearGradient(listOf(Color(0xFFFFF0B0), Skin.Gold, Color(0xFFB8740F), Skin.Gold)), shape)
            .padding(2.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF3D0C26), Color(0xFF1A0620), Color(0xFF0E0414))), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFFF3D6E).copy(alpha = 0.35f + 0.4f * glow), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "GRAND JACKPOT",
                style = TextStyle(fontFamily = Montserrat, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, letterSpacing = 2.sp, color = Color.White),
                modifier = Modifier.background(Brush.horizontalGradient(listOf(Color(0xFFFF3D6E), Color(0xFFC2185B))), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 1.dp),
            )
            GoldText(formatCoins(shown), 28.sp)
        }
    }
}

@Composable
private fun SmallPlate(tier: JackpotTier, value: Long, modifier: Modifier) {
    val shown = creeping(tier, value)
    val c = TIER_COLORS.getValue(tier)
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier
            .shadow(8.dp, shape)
            .background(Brush.verticalGradient(listOf(Color(0xFF3E1468), Color(0xFF1C0738))), shape)
            .border(1.5.dp, c.copy(alpha = 0.85f), shape),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(tier.label, style = TextStyle(fontFamily = Montserrat, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, letterSpacing = 1.5.sp, color = c))
            Text(formatCoins(shown), maxLines = 1, style = TextStyle(fontFamily = Montserrat, fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color.White))
        }
    }
}
