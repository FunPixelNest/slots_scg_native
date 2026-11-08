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
import com.funpixelnest.slots.engine.Features
import com.funpixelnest.slots.engine.Feature
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

/**
 * Panel above the reels: progressive jackpots on jackpot machines, otherwise the machine's
 * feature (multiplier ladder, diamond meter, coin collector, pick bonus).
 */
@Composable
fun FeatureHeader(vm: GameViewModel, assets: ThemeAssets) {
    val f = vm.feature
    if (f == null || f.hasJackpots) { JackpotBar(vm, assets); return }
    val purple = Triple(Color(0xFFE3D2FF), Color(0xFF5B2BB0), Color(0xFF2E0F66))
    val green = Triple(Color(0xFFC8FFD6), Color(0xFF1F8A44), Color(0xFF0B4A22))
    val blue = Triple(Color(0xFFC8F4FF), Color(0xFF1F6FB0), Color(0xFF0B3A66))
    val orange = Triple(Color(0xFFFFE0B8), Color(0xFFE0700E), Color(0xFF7A3206))
    Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        val signMod = Modifier.fillMaxWidth(0.9f).height(72.dp).align(Alignment.CenterHorizontally)
        when (f) {
            Feature.FREE_SPINS_MULTIPLIER -> {
                val active = vm.freeSpins > 0
                Marquee(if (active) "FREE SPINS · ${vm.freeSpins} LEFT" else "FREE SPINS MULTIPLIER", signMod) {
                    GoldText(if (active) "x${vm.freeMultiplier}" else "UP TO x10", 30.sp)
                }
                val ladder = Features.FREE_MULTIPLIERS
                Badges(ladder.map { "" to "x$it" }, listOf(purple, blue, green, orange, purple), lit = if (active) ladder.indexOf(vm.freeMultiplier) else -1)
            }
            Feature.CASCADE -> {
                Marquee("CASCADE MULTIPLIER", signMod) { GoldText("x${vm.cascadeMultiplier}", 30.sp) }
                val ladder = Features.CASCADE_MULTIPLIERS
                Badges(ladder.mapIndexed { i, m -> (if (i == 0) "WIN" else "CASCADE $i") to "x$m" }, listOf(purple, blue, green, orange),
                    lit = ladder.indexOf(vm.cascadeMultiplier).takeIf { vm.cascadeMultiplier > 1 } ?: -1)
            }
            Feature.COLLECT_METER -> {
                Marquee("DIAMOND COLLECTION", signMod) { DiamondMeter(vm.meter, Features.METER_TARGET) }
                Badges(
                    listOf("FULL METER" to "${Features.SUPER_SPINS} SPINS", "SUPER SPINS" to "x${Features.SUPER_MULTIPLIER} WINS", "COLLECTED" to "${vm.meter} / ${Features.METER_TARGET}"),
                    listOf(blue, purple, green),
                )
            }
            Feature.COIN_COLLECTOR -> {
                Marquee("COIN COLLECTOR", signMod) {
                    GoldText(if (vm.lastCollected > 0) formatCoins(vm.lastCollected) else "COLLECT ALL COINS", if (vm.lastCollected > 0) 30.sp else 22.sp)
                }
                Badges(
                    listOf("COINS" to "REELS 1-4", "COLLECTOR" to "REEL 5", "TOP COIN" to "x25 BET"),
                    listOf(orange, purple, green),
                )
            }
            else -> { // pick bonus
                Marquee("PICK BONUS", signMod) { GoldText("OPEN THE CHESTS", 22.sp) }
                Badges(
                    listOf("TRIGGER" to "4+ SCATTERS", "PICK" to "3 OF 9", "PRIZES" to "COINS + MINI"),
                    listOf(purple, orange, blue),
                )
            }
        }
    }
}

/** Row of feature badges under the sign; [lit] highlights the current step. */
@Composable
private fun Badges(items: List<Pair<String, String>>, colors: List<Triple<Color, Color, Color>>, lit: Int = -1) {
    Row(Modifier.fillMaxWidth().height(44.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEachIndexed { i, (label, value) ->
            val c = colors[i % colors.size]
            TextBadge(label, value, c.first, c.second, c.third, Modifier.weight(1f).fillMaxHeight(), lit = i == lit)
        }
    }
}

/** Diamond meter bar for the Diamond Collection sign. */
@Composable
private fun DiamondMeter(value: Int, target: Int) {
    Box(
        Modifier.padding(top = 4.dp).fillMaxWidth(0.82f).height(20.dp)
            .background(Color(0xFF14052A), RoundedCornerShape(50)).border(1.5.dp, Skin.Gold, RoundedCornerShape(50)).padding(2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier.align(Alignment.CenterStart).fillMaxHeight().fillMaxWidth((value.toFloat() / target).coerceIn(0.04f, 1f))
                .background(Brush.horizontalGradient(listOf(Color(0xFF1F6FD0), Color(0xFF38D6FF), Color(0xFFE6FBFF))), RoundedCornerShape(50)),
        )
        Text("◆ $value / $target", style = TextStyle(fontFamily = Montserrat, fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color.White))
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
