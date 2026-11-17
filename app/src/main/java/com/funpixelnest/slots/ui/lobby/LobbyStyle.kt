package com.funpixelnest.slots.ui.lobby

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funpixelnest.slots.audio.Haptics
import com.funpixelnest.slots.data.ThemeAssets
import com.funpixelnest.slots.ui.common.CoinIcon
import com.funpixelnest.slots.ui.common.Montserrat
import com.funpixelnest.slots.ui.common.formatCoins
import com.funpixelnest.slots.ui.profile.Avatar
import com.funpixelnest.slots.ui.skin.Skin
import com.funpixelnest.slots.ui.skin.SkinIcon
import com.funpixelnest.slots.ui.skin.drawSkinIcon
import com.funpixelnest.slots.ui.skin.ball
import com.funpixelnest.slots.ui.skin.glossy
import com.funpixelnest.slots.ui.skin.glowPanel

/*
 * Lobby pieces in the arcade-night style: navy panels with glowing blue rims, glossy pills,
 * round icon balls and colourful icons.
 */

internal val RimGold = Brush.verticalGradient(listOf(Color(0xFFFFF3B0), Color(0xFFFFC93C), Color(0xFFE08A00), Color(0xFFFFD86A)))
internal val CardPurple = Brush.verticalGradient(listOf(Color(0xFF4E1590), Color(0xFF2C0A57)))
internal val GlowOrange = Color(0xFFFFA726)

internal fun ts(size: TextUnit, color: Color = Color.White, weight: FontWeight = FontWeight.ExtraBold, spacing: TextUnit = 0.sp) =
    TextStyle(fontFamily = Montserrat, fontWeight = weight, fontSize = size, color = color, letterSpacing = spacing)

/** Red glossy notification dot. */
@Composable
internal fun AlertDot(modifier: Modifier, size: Dp = 16.dp) {
    Box(
        modifier.size(size).shadow(6.dp, CircleShape, spotColor = Color.Red)
            .background(Brush.radialGradient(listOf(Color(0xFFFF8A80), Color(0xFFE53935), Color(0xFF9A0E0E))), CircleShape)
            .border(1.5.dp, Color(0xFFFFC0C0), CircleShape),
    ) { Box(Modifier.padding(start = size * 0.22f, top = size * 0.15f).size(size * 0.3f).background(Color(0xAAFFFFFF), CircleShape)) }
}

// ------------------------------------------------------------------ top strip

/** Top row: avatar with level pill, name and XP bar, blue coin bar with green +, gear ball. */
@Composable
internal fun LobbyTopStrip(
    images: ThemeAssets, avatarId: Int, name: String, level: Int, progress: Float, coins: Long,
    onProfile: () -> Unit, onPlus: () -> Unit, onSettings: () -> Unit,
) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Row(
            Modifier.weight(1f).clickable(remember { MutableInteractionSource() }, null, onClick = onProfile).testTag("open-profile"),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(50.dp), contentAlignment = Alignment.TopCenter) {
                Box(
                    Modifier.size(46.dp).shadow(10.dp, CircleShape, ambientColor = Skin.Hairline, spotColor = Skin.Hairline)
                        .border(2.5.dp, Color(0xFF8AB8FF), CircleShape),
                ) { Avatar(images, avatarId, 46.dp, Modifier.align(Alignment.Center)) }
                Text(
                    "LV $level", style = com.funpixelnest.slots.ui.skin.displayText(10.sp),
                    modifier = Modifier.align(Alignment.BottomCenter).offset(y = 4.dp)
                        .glossy(com.funpixelnest.slots.ui.skin.SkinTone.Purple).padding(horizontal = 7.dp),
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(name, style = com.funpixelnest.slots.ui.skin.displayText(15.sp).copy(textAlign = androidx.compose.ui.text.style.TextAlign.Start), maxLines = 1, softWrap = false, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                com.funpixelnest.slots.ui.skin.XpBar(level, progress, Modifier.padding(top = 4.dp).fillMaxWidth(0.9f))
            }
        }
        Spacer(Modifier.width(6.dp))
        com.funpixelnest.slots.ui.skin.CoinCapsule(coins, Modifier.width(176.dp), onPlus = { Haptics.tap(); onPlus() })
        Spacer(Modifier.width(8.dp))
        com.funpixelnest.slots.ui.skin.IconRoundButton(SkinIcon.Gear, size = 40.dp, onClick = onSettings)
    }
}

private fun DrawScope.drawCrown() {
    val w = size.width; val h = size.height
    val p = Path().apply {
        moveTo(0f, h); lineTo(0.05f * w, 0.25f * h); lineTo(0.3f * w, 0.6f * h); lineTo(0.5f * w, 0f)
        lineTo(0.7f * w, 0.6f * h); lineTo(0.95f * w, 0.25f * h); lineTo(w, h); close()
    }
    drawPath(p, Color(0xFF8A5208))
    drawPath(p, Brush.verticalGradient(listOf(Color(0xFFFFF3B0), Color(0xFFFFC93C), Color(0xFFE08A00))), alpha = 0.95f)
    drawCircle(Color(0xFFE53935), h * 0.12f, Offset(0.5f * w, 0.62f * h))
}

// ------------------------------------------------------------------ quick tiles

enum class QuickIcon { Gift, Clock, Video }

/** Glowing navy tile with a colourful icon, a label and a red dot when something is ready. */
@Composable
internal fun RefQuickTile(label: String, alert: Boolean, modifier: Modifier, onClick: () -> Unit, icon: @Composable () -> Unit) {
    Box(modifier.height(92.dp)) {
        Column(
            Modifier.fillMaxSize().padding(top = 5.dp, end = 4.dp)
                .glowPanel(20.dp)
                .clickable(remember { MutableInteractionSource() }, null) { Haptics.tap(); onClick() },
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
        ) {
            Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) { icon() }
            Spacer(Modifier.height(4.dp))
            Text(label, style = com.funpixelnest.slots.ui.skin.displayText(14.sp), maxLines = 1)
        }
        if (alert) AlertDot(Modifier.align(Alignment.TopEnd))
    }
}

/** Colourful drawn icons for the quick tiles. */
@Composable
internal fun QuickIconArt(icon: QuickIcon) {
    Canvas(Modifier.size(38.dp)) {
        val w = size.width
        when (icon) {
            QuickIcon.Gift -> {
                val pink = Brush.verticalGradient(listOf(Color(0xFFFF8AD0), Color(0xFFE91E8C)), 0.35f * w, w)
                drawRoundRect(pink, Offset(0.12f * w, 0.42f * w), Size(0.76f * w, 0.56f * w), CornerRadius(0.06f * w))
                drawRoundRect(Brush.verticalGradient(listOf(Color(0xFFFFB3E0), Color(0xFFFF5FB4))), Offset(0.04f * w, 0.28f * w), Size(0.92f * w, 0.18f * w), CornerRadius(0.05f * w))
                drawRect(RimGold, Offset(0.43f * w, 0.28f * w), Size(0.14f * w, 0.7f * w))
                drawOval(Color(0xFFFFC93C), Offset(0.16f * w, 0.04f * w), Size(0.32f * w, 0.26f * w), style = Stroke(0.08f * w))
                drawOval(Color(0xFFFFC93C), Offset(0.52f * w, 0.04f * w), Size(0.32f * w, 0.26f * w), style = Stroke(0.08f * w))
            }
            QuickIcon.Clock -> {
                drawCircle(Brush.radialGradient(listOf(Color(0xFF8FF0FF), Color(0xFF1F9AD6))), 0.48f * w)
                drawCircle(Color(0xFF0B3A66), 0.36f * w)
                drawCircle(Color.White, 0.31f * w)
                drawLine(Color(0xFF0B3A66), Offset(0.5f * w, 0.5f * w), Offset(0.5f * w, 0.28f * w), 0.07f * w)
                drawLine(Color(0xFF0B3A66), Offset(0.5f * w, 0.5f * w), Offset(0.68f * w, 0.58f * w), 0.07f * w)
                drawCircle(Color(0xFF0B3A66), 0.05f * w)
            }
            QuickIcon.Video -> {
                drawRoundRect(RimGold, Offset(0.02f * w, 0.2f * w), Size(0.7f * w, 0.6f * w), CornerRadius(0.12f * w))
                drawPath(Path().apply { moveTo(0.72f * w, 0.42f * w); lineTo(0.98f * w, 0.26f * w); lineTo(0.98f * w, 0.74f * w); lineTo(0.72f * w, 0.58f * w); close() }, RimGold)
                drawPath(Path().apply { moveTo(0.28f * w, 0.34f * w); lineTo(0.52f * w, 0.5f * w); lineTo(0.28f * w, 0.66f * w); close() }, Color(0xFF6A1FC2))
            }
        }
    }
}

// ------------------------------------------------------------------ chips

/** Filter chip: orange glossy pill when selected, blue otherwise. */
@Composable
internal fun RefChip(text: String, on: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.height(32.dp).glossy(if (on) com.funpixelnest.slots.ui.skin.SkinTone.Gold else com.funpixelnest.slots.ui.skin.SkinTone.Blue)
            .clickable { Haptics.tap(); onClick() }.padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) { Text(text.uppercase(), style = com.funpixelnest.slots.ui.skin.displayText(13.sp), maxLines = 1) }
}

// ------------------------------------------------------------------ footer

/** Footer: navy bar with a glowing blue top edge, blue icon balls and a raised green home ball in a gold ring. */
@Composable
internal fun RefTabBar(
    modifier: Modifier, taskAlert: Boolean, missionAlert: Boolean,
    onStore: () -> Unit, onTasks: () -> Unit, onMissions: () -> Unit, onPiggy: () -> Unit,
) {
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
        Row(
            Modifier.fillMaxWidth().height(78.dp)
                .shadow(16.dp, ambientColor = Skin.Hairline, spotColor = Skin.Hairline)
                .background(Brush.verticalGradient(listOf(Color(0xFF1A1A96), Color(0xFF0A0A52))))
                .drawBehind { drawRect(Skin.Hairline, Offset.Zero, Size(size.width, 2.5.dp.toPx())) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FootItem(SkinIcon.Cart, "STORE", false, Modifier.weight(1f), onStore)
            FootItem(SkinIcon.Tasks, "TASKS", taskAlert, Modifier.weight(1f), onTasks)
            Spacer(Modifier.weight(1.1f))
            FootItem(SkinIcon.Target, "MISSIONS", missionAlert, Modifier.weight(1f), onMissions)
            FootItem(SkinIcon.Piggy, "PIGGY", false, Modifier.weight(1f), onPiggy)
        }
        // raised home ball (current screen)
        Box(
            Modifier.padding(bottom = 26.dp).size(86.dp)
                .shadow(18.dp, CircleShape, ambientColor = Skin.Gold, spotColor = Skin.Gold)
                .background(Brush.sweepGradient(listOf(Color(0xFFFFE46A), Color(0xFFFF9A00), Color(0xFFFFE46A), Color(0xFFFF9A00), Color(0xFFFFE46A))), CircleShape)
                .padding(4.dp).ball(com.funpixelnest.slots.ui.skin.SkinTone.Green),
            contentAlignment = Alignment.Center,
        ) { Canvas(Modifier.size(36.dp)) { drawSkinIcon(SkinIcon.Home, Color.White) } }
    }
}

@Composable
private fun FootItem(icon: SkinIcon, label: String, alert: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier.fillMaxHeight().clickable(remember { MutableInteractionSource() }, null) { Haptics.tap(); onClick() },
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
    ) {
        Box {
            Box(Modifier.size(44.dp).ball(com.funpixelnest.slots.ui.skin.SkinTone.Blue), contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(20.dp)) { drawSkinIcon(icon, Color.White) }
            }
            if (alert) AlertDot(Modifier.align(Alignment.TopEnd).offset(x = 4.dp, y = (-4).dp), 14.dp)
        }
        Spacer(Modifier.height(3.dp))
        Text(label, style = com.funpixelnest.slots.ui.skin.displayText(10.sp), maxLines = 1)
    }
}

private fun DrawScope.drawHome() {
    val w = size.width
    val gold = Brush.verticalGradient(listOf(Color(0xFFFFF3B0), Color(0xFFFFC93C), Color(0xFFE08A00)))
    val roof = Path().apply { moveTo(0.02f * w, 0.52f * w); lineTo(0.5f * w, 0.06f * w); lineTo(0.98f * w, 0.52f * w); lineTo(0.86f * w, 0.6f * w); lineTo(0.5f * w, 0.26f * w); lineTo(0.14f * w, 0.6f * w); close() }
    drawRoundRect(gold, Offset(0.2f * w, 0.42f * w), Size(0.6f * w, 0.54f * w), CornerRadius(0.05f * w))
    drawPath(roof, Color(0xFFFF7A1A))
    drawRoundRect(Color(0xFF8A3A00), Offset(0.42f * w, 0.62f * w), Size(0.16f * w, 0.34f * w), CornerRadius(0.03f * w))
}

/** Thick glowing gold frame used by the hero banner and machine cards. */
internal fun Modifier.goldGlowFrame(corner: Dp, width: Dp = 3.dp): Modifier =
    this.shadow(14.dp, RoundedCornerShape(corner), ambientColor = Skin.Hairline, spotColor = Skin.Hairline)
        .background(Brush.verticalGradient(listOf(Color(0xFFBFE2FF), Skin.Hairline)), RoundedCornerShape(corner)).padding(width)
