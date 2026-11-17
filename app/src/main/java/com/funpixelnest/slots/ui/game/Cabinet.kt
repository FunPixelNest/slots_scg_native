package com.funpixelnest.slots.ui.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funpixelnest.slots.audio.Haptics
import com.funpixelnest.slots.ui.common.Lilita
import com.funpixelnest.slots.ui.common.Montserrat
import com.funpixelnest.slots.ui.common.formatCoins
import com.funpixelnest.slots.ui.skin.CoinCapsule
import com.funpixelnest.slots.ui.skin.GoldDisplay
import com.funpixelnest.slots.ui.skin.IconRoundButton
import com.funpixelnest.slots.ui.skin.LevelMedallion
import com.funpixelnest.slots.ui.skin.MarqueeLights
import com.funpixelnest.slots.ui.skin.Skin
import com.funpixelnest.slots.ui.skin.SkinIcon
import com.funpixelnest.slots.ui.skin.SkinTone
import com.funpixelnest.slots.ui.skin.SpinFace
import com.funpixelnest.slots.ui.skin.XpBar
import com.funpixelnest.slots.ui.skin.ball
import com.funpixelnest.slots.ui.skin.displayText
import com.funpixelnest.slots.ui.skin.drawSkinIcon
import com.funpixelnest.slots.ui.skin.glossy
import com.funpixelnest.slots.ui.skin.glowPanel

/*
 * Gameplay pieces in the arcade-night look: HUD row, GRAND light-bulb sign with counter
 * digits, MAJOR / MINOR / MINI plaques, glowing reel panel, WIN pill and the control deck
 * (bet + MAX BET on the left, rectangular SPIN on the right: tap to spin, hold for auto).
 */

/** Polished gold, light at the top (rims, SPIN frame). */
val GoldMetal = Skin.goldRim

private fun caption(size: TextUnit, color: Color = Skin.TextSoft, spacing: TextUnit = 1.5.sp) =
    TextStyle(fontFamily = Montserrat, fontWeight = FontWeight.Black, fontSize = size, color = color, letterSpacing = spacing)

/** Round icon ball (back, menu). */
@Composable
fun GoldKey(icon: SkinIcon, size: Dp = 42.dp, onClick: () -> Unit) = IconRoundButton(icon, size = size, onClick = onClick)

/** Gold level star. */
@Composable
fun LevelStar(level: Int, size: Dp = 40.dp) = LevelMedallion(level, 0f, size = size)

/** HUD row: back · level star + XP · coins (compact, on the right) · menu. */
@Composable
fun CabinetTopBar(
    level: Int, progress: Float, coins: Long,
    onBack: () -> Unit, onCoins: () -> Unit, onMenu: () -> Unit,
    xpOverlay: @Composable BoxScope.() -> Unit = {},
) {
    Row(
        Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GoldKey(SkinIcon.Back, 42.dp, onBack)
        Box(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LevelMedallion(level, progress, size = 40.dp)
                XpBar(level, progress, Modifier.width(62.dp).offset(x = (-8).dp))
            }
            xpOverlay()
        }
        CoinCapsule(coins, Modifier.width(184.dp).clickable(remember { MutableInteractionSource() }, null, onClick = onCoins), onPlus = onCoins)
        GoldKey(SkinIcon.Menu, 42.dp, onMenu)
    }
}

/** GRAND jackpot: light-bulb sign with counter digits and a gold "♛ GRAND ♛" tab on top. */
@Composable
fun JackpotMarquee(value: Long, modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.TopCenter) {
        MarqueeLights(Modifier.fillMaxWidth().padding(top = 11.dp).height(58.dp), bulbs = 34) { CounterDigits(formatCoins(value)) }
        Box(
            Modifier.height(22.dp).shadow(4.dp, TabShape)
                .background(Brush.verticalGradient(listOf(Color(0xFFFFE46A), Color(0xFFFFB000), Color(0xFFE07A00))), TabShape)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center,
        ) { Text("♛ GRAND ♛", style = TextStyle(fontFamily = Lilita, fontSize = 14.sp, letterSpacing = 2.5.sp, color = Color(0xFF5A0A00))) }
    }
}

/** Ribbon with notched ends. */
private val TabShape = GenericShape { size, _ ->
    val n = size.width * 0.06f
    moveTo(0f, 0f); lineTo(size.width, 0f); lineTo(size.width - n, size.height / 2); lineTo(size.width, size.height)
    lineTo(0f, size.height); lineTo(n, size.height / 2); close()
}

/** Odometer-style digits: each digit on its own dark tile, glowing gold. */
@Composable
fun CounterDigits(text: String, digitWidth: Dp = 21.dp, height: Dp = 32.dp, size: TextUnit = 22.sp) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
        text.forEach { ch ->
            if (!ch.isDigit()) {
                Text("$ch", style = TextStyle(fontFamily = Lilita, fontSize = size * 0.6f, color = Color(0xFFFFE46A)))
            } else {
                Box(
                    Modifier.width(digitWidth).height(height)
                        .background(Brush.verticalGradient(0f to Color(0xFF1A0430), 0.48f to Color(0xFF3A0A5A), 0.52f to Color(0xFF12021F), 1f to Color(0xFF2A0646)), RoundedCornerShape(5.dp))
                        .border(1.dp, Color(0x73FFC850), RoundedCornerShape(5.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("$ch", style = TextStyle(fontFamily = Lilita, fontSize = size, color = Color(0xFFFFE46A), shadow = Shadow(Color(0xE6FFBE28), Offset.Zero, 12f)))
                }
            }
        }
    }
}

/** Feature sign: glowing navy panel with a caption on top and [content] below. */
@Composable
fun Marquee(title: String, modifier: Modifier, content: @Composable () -> Unit) {
    Box(modifier.glowPanel(20.dp).padding(horizontal = 10.dp, vertical = 6.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = caption(11.sp, Color(0xFFFFD98A), 2.sp), maxLines = 1)
            content()
        }
    }
}

/** Glossy pill with a caption and a value; [lit] = the current step (orange, glowing). */
@Composable
fun TextBadge(label: String, value: String, @Suppress("UNUSED_PARAMETER") rim: Color, @Suppress("UNUSED_PARAMETER") top: Color, @Suppress("UNUSED_PARAMETER") bottom: Color, modifier: Modifier, lit: Boolean = false) {
    Box(
        modifier.scale(if (lit) 1.06f else 1f).glossy(if (lit) SkinTone.Gold else SkinTone.Blue),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (label.isNotEmpty()) Text(label, style = caption(8.sp, Color.White, 1.sp), maxLines = 1)
            Text(value, maxLines = 1, style = displayText(if (label.isEmpty()) 17.sp else 13.sp))
        }
    }
}

/** MAJOR / MINOR / MINI plaque: glowing border in its colour, label tab on top. */
@Composable
fun JackpotBadge(label: String, value: Long, rim: Color, top: Color, bottom: Color, modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.TopCenter) {
        val shape = RoundedCornerShape(12.dp)
        Box(
            Modifier.fillMaxSize().padding(top = 8.dp)
                .shadow(10.dp, shape, ambientColor = rim, spotColor = rim)
                .background(Brush.verticalGradient(listOf(top, bottom)), shape)
                .border(2.dp, rim, shape),
            contentAlignment = Alignment.Center,
        ) {
            Text(formatCoins(value), maxLines = 1, style = displayText(16.sp).copy(shadow = Shadow(rim, Offset.Zero, 10f)), modifier = Modifier.padding(top = 3.dp))
        }
        Text(
            label, style = caption(9.sp, Color.White, 1.5.sp),
            modifier = Modifier.background(Brush.verticalGradient(listOf(rim, top)), RoundedCornerShape(8.dp))
                .border(1.5.dp, Color(0xB3FFFFFF), RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 1.dp),
        )
    }
}

/** Glowing navy panel around the reels; symbols sit straight on it. */
@Composable
fun CabinetFrame(@Suppress("UNUSED_PARAMETER") plaque: String, modifier: Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier.glowPanel(24.dp, fill = Brush.verticalGradient(listOf(Color(0xEB100E6E), Color(0xF208083C)))).padding(8.dp),
        content = content,
    )
}

/** WIN read-out under the reels. */
@Composable
fun WinMeter(win: Long, modifier: Modifier, label: String = "WIN") {
    Row(
        modifier.glowPanel(22.dp).padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center,
    ) {
        Text(label, style = caption(13.sp, Skin.TextSoft, 2.sp))
        Spacer(Modifier.width(10.dp))
        GoldDisplay(formatCoins(win), 26.sp)
    }
}

/**
 * Control deck: TOTAL BET stepper and MAX BET on the left, rectangular SPIN on the right.
 * Tap SPIN to spin; hold it to start auto-spin; while auto runs it reads STOP.
 */
@Composable
fun ControlDeck(
    totalBet: Long, betLocked: Boolean, spinEnabled: Boolean, freeSpins: Int, autoLeft: Int,
    onMinus: () -> Unit, onPlus: () -> Unit, onSpin: () -> Unit, onAuto: () -> Unit, onMaxBet: () -> Unit,
    @Suppress("UNUSED_PARAMETER") showAuto: Boolean = false,
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 10.dp).height(128.dp).glowPanel(26.dp).padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("TOTAL BET", style = caption(11.sp, Skin.TextSoft, 2.sp))
            Row(
                Modifier.fillMaxWidth().background(Color(0xFF070840), RoundedCornerShape(50)).padding(3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StepKey(SkinIcon.Minus, SkinTone.Red, !betLocked, onMinus)
                Text(formatCoins(totalBet), style = displayText(19.sp), maxLines = 1, modifier = Modifier.weight(1f))
                StepKey(SkinIcon.Plus, SkinTone.Green, !betLocked, onPlus)
            }
            Box(
                Modifier.fillMaxWidth().height(34.dp).alpha(if (betLocked) 0.5f else 1f).glossy(SkinTone.Gold)
                    .clickable(remember { MutableInteractionSource() }, null, enabled = !betLocked) { Haptics.tap(); onMaxBet() },
                contentAlignment = Alignment.Center,
            ) { Text("MAX BET", style = displayText(15.sp)) }
        }
        Spacer(Modifier.width(12.dp))
        val auto = autoLeft > 0
        DeckSpin(
            label = when { freeSpins > 0 -> "FREE"; auto -> "STOP"; else -> "SPIN" },
            sub = when { freeSpins > 0 -> "$freeSpins LEFT"; auto -> "AUTO · $autoLeft LEFT"; else -> "HOLD FOR AUTO" },
            tone = if (auto) SkinTone.Gold else SkinTone.Green,
            enabled = spinEnabled || auto,
            modifier = Modifier.width(146.dp).height(100.dp),
            onTap = { if (auto) onAuto() else onSpin() },
            onHold = { if (!auto && freeSpins == 0) onAuto() },
        )
    }
}

@Composable
private fun StepKey(icon: SkinIcon, tone: SkinTone, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.size(34.dp).alpha(if (enabled) 1f else 0.45f).ball(tone)
            .clickable(remember { MutableInteractionSource() }, null, enabled = enabled) { Haptics.tap(); onClick() },
        contentAlignment = Alignment.Center,
    ) { Canvas(Modifier.size(13.dp)) { drawSkinIcon(icon, Color.White) } }
}

/** Rectangular SPIN with tap + long-press. */
@Composable
private fun DeckSpin(label: String, sub: String, tone: SkinTone, enabled: Boolean, modifier: Modifier, onTap: () -> Unit, onHold: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f, label = "spin")
    SpinFace(
        label, sub, tone,
        modifier.scale(scale).alpha(if (enabled) 1f else 0.6f)
            .pointerInput(enabled, label) {
                detectTapGestures(
                    onPress = { pressed = true; tryAwaitRelease(); pressed = false },
                    onTap = { if (enabled) { Haptics.tap(); onTap() } },
                    onLongPress = { if (enabled) { Haptics.tap(); onHold() } },
                )
            },
    )
}

/** Small purple "i" ball (paytable). */
@Composable
fun InfoKey(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier.size(30.dp).ball(SkinTone.Purple).clickable(remember { MutableInteractionSource() }, null) { Haptics.tap(); onClick() },
        contentAlignment = Alignment.Center,
    ) { Text("i", style = displayText(18.sp)) }
}

/** Feature tag under the machine title. */
@Composable
fun FeatureTag(text: String, modifier: Modifier = Modifier) {
    Box(modifier.height(26.dp).glossy(SkinTone.Gold).padding(horizontal = 14.dp), contentAlignment = Alignment.Center) {
        Text("★ $text ★", style = displayText(13.sp), maxLines = 1)
    }
}
