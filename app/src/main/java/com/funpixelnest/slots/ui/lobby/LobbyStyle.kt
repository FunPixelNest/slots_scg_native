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

/*
 * Lobby pieces in the "casino app" reference style: purple glass, thick glowing gold rims,
 * glossy pills and colourful icons.
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

/** Fixed top strip: avatar with crown + level, name and XP, divider, coins with green +, gear. */
@Composable
internal fun LobbyTopStrip(
    images: ThemeAssets, avatarId: Int, name: String, level: Int, progress: Float, coins: Long,
    onProfile: () -> Unit, onPlus: () -> Unit, onSettings: () -> Unit,
) {
    Box(
        Modifier.fillMaxWidth().shadow(12.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF3A0A68), Color(0xFF240645))))
            .drawBehind {
                drawRect(Brush.horizontalGradient(listOf(Color(0x00FF3DB4), Color(0xFFFF3DB4), Color(0xFFFFC93C), Color(0xFFFF3DB4), Color(0x00FF3DB4))),
                    Offset(0f, size.height - 2.dp.toPx()), Size(size.width, 2.dp.toPx()))
            }
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                Modifier.weight(1f).clickable(remember { MutableInteractionSource() }, null, onClick = onProfile).testTag("open-profile"),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(58.dp), contentAlignment = Alignment.Center) {
                    Avatar(images, avatarId, 50.dp, Modifier.align(Alignment.BottomCenter))
                    Canvas(Modifier.align(Alignment.TopCenter).offset(y = (-4).dp).size(24.dp, 15.dp)) { drawCrown() }
                    Text(
                        "Lv.$level", style = ts(9.sp, Color(0xFF3A1800), FontWeight.Black),
                        modifier = Modifier.align(Alignment.BottomCenter).offset(y = 4.dp)
                            .background(RimGold, RoundedCornerShape(50)).border(1.dp, Color(0xFF8A5208), RoundedCornerShape(50))
                            .padding(horizontal = 6.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(name, style = ts(17.sp, Color.White, FontWeight.Black), maxLines = 1)
                    Text("LEVEL $level - ${progress.toInt()}%", style = ts(11.sp, Color(0xFFCDB8EC)), maxLines = 1)
                    Box(
                        Modifier.padding(top = 4.dp).fillMaxWidth(0.95f).height(9.dp)
                            .background(Color(0xFF1A0533), RoundedCornerShape(50)).border(1.dp, Color(0xFF7B3FC0), RoundedCornerShape(50)).padding(1.5.dp),
                    ) {
                        Box(
                            Modifier.fillMaxHeight().fillMaxWidth((progress / 100f).coerceIn(0.04f, 1f))
                                .background(Brush.horizontalGradient(listOf(Color(0xFF9B5CFF), Color(0xFFD08BFF))), RoundedCornerShape(50)),
                        )
                    }
                }
            }
            Box(Modifier.padding(horizontal = 8.dp).width(1.dp).height(44.dp).background(Color(0x55CDB8EC)))
            // coins
            Row(
                Modifier.height(42.dp).shadow(8.dp, RoundedCornerShape(50), spotColor = GlowOrange)
                    .background(RimGold, RoundedCornerShape(50)).padding(2.5.dp)
                    .background(Brush.verticalGradient(listOf(Color(0xFF2A0B4A), Color(0xFF14052A))), RoundedCornerShape(50))
                    .padding(start = 3.dp, end = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CoinIcon(Modifier.size(30.dp))
                Spacer(Modifier.width(6.dp))
                Text(formatCoins(coins), style = ts(14.sp, Color.White, FontWeight.Black), maxLines = 1)
                Spacer(Modifier.width(6.dp))
                Box(
                    Modifier.size(32.dp).shadow(6.dp, CircleShape, spotColor = Color(0xFF22C55E))
                        .background(Brush.radialGradient(listOf(Color(0xFF7CF29A), Color(0xFF16A34A), Color(0xFF0B6E2E))), CircleShape)
                        .border(1.5.dp, Color(0xFFC8FFD6), CircleShape)
                        .clickable(remember { MutableInteractionSource() }, null) { Haptics.tap(); onPlus() },
                    contentAlignment = Alignment.Center,
                ) { Canvas(Modifier.size(14.dp)) { drawSkinIcon(SkinIcon.Plus, Color.White) } }
            }
            Spacer(Modifier.width(8.dp))
            // gear
            Box(
                Modifier.size(42.dp).shadow(8.dp, CircleShape, spotColor = Color(0xFF9B5CFF))
                    .background(RimGold, CircleShape).padding(2.5.dp)
                    .background(Brush.radialGradient(listOf(Color(0xFF8A4FE0), Color(0xFF4A1580))), CircleShape)
                    .clickable(remember { MutableInteractionSource() }, null) { Haptics.tap(); onSettings() },
                contentAlignment = Alignment.Center,
            ) { Canvas(Modifier.size(22.dp)) { drawSkinIcon(SkinIcon.Gear, Color.White) } }
        }
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

/** Glossy purple tile with a colourful icon, a label and a red dot when something is ready. */
@Composable
internal fun RefQuickTile(label: String, alert: Boolean, modifier: Modifier, onClick: () -> Unit, icon: @Composable () -> Unit) {
    Box(modifier.height(92.dp)) {
        Column(
            Modifier.fillMaxSize().padding(top = 4.dp, end = 4.dp)
                .shadow(10.dp, RoundedCornerShape(18.dp), ambientColor = Color(0xFFFF3DB4), spotColor = Color(0xFFFF3DB4))
                .background(RimGold, RoundedCornerShape(18.dp)).padding(2.dp)
                .background(CardPurple, RoundedCornerShape(16.dp))
                .clickable(remember { MutableInteractionSource() }, null) { Haptics.tap(); onClick() },
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
        ) {
            Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) { icon() }
            Spacer(Modifier.height(4.dp))
            Text(label, style = ts(14.sp, Color.White, FontWeight.Black), maxLines = 1)
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

/** Filter chip: gold pill when selected, purple outline otherwise. */
@Composable
internal fun RefChip(text: String, on: Boolean, onClick: () -> Unit) {
    Text(
        text, style = ts(13.sp, if (on) Color(0xFF3A1800) else Color.White, FontWeight.ExtraBold), maxLines = 1,
        modifier = Modifier.height(36.dp)
            .then(if (on) Modifier.shadow(8.dp, RoundedCornerShape(50), spotColor = GlowOrange) else Modifier)
            .background(if (on) RimGold else Brush.verticalGradient(listOf(Color(0xFF3A0E6A), Color(0xFF240645))), RoundedCornerShape(50))
            .border(1.5.dp, if (on) Color(0xFFFFE08A) else Color(0xFF8A4FD0), RoundedCornerShape(50))
            .clickable { Haptics.tap(); onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

// ------------------------------------------------------------------ footer

/** Footer: purple bar with a gold-magenta rim, dividers, and a raised gold Lobby tab in the middle. */
@Composable
internal fun RefTabBar(
    modifier: Modifier, taskAlert: Boolean, missionAlert: Boolean,
    onStore: () -> Unit, onTasks: () -> Unit, onMissions: () -> Unit, onPiggy: () -> Unit,
) {
    val shape = RoundedCornerShape(26.dp)
    Box(modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp), contentAlignment = Alignment.BottomCenter) {
        Row(
            Modifier.fillMaxWidth().height(72.dp)
                .shadow(18.dp, shape, ambientColor = Color(0xFFFF3DB4), spotColor = Color(0xFFFF3DB4))
                .background(Brush.horizontalGradient(listOf(Color(0xFFFFC93C), Color(0xFFFF3DB4), Color(0xFFB45CFF), Color(0xFFFF3DB4), Color(0xFFFFC93C))), shape)
                .padding(2.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF3E0E70), Color(0xFF1E0538))), RoundedCornerShape(24.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FootItem(SkinIcon.Cart, "Store", false, Modifier.weight(1f), onStore)
            FootDivider()
            FootItem(SkinIcon.Tasks, "Tasks", taskAlert, Modifier.weight(1f), onTasks)
            Spacer(Modifier.weight(1.15f))
            FootItem(SkinIcon.Target, "Missions", missionAlert, Modifier.weight(1f), onMissions)
            FootDivider()
            FootItem(SkinIcon.Piggy, "Piggy", false, Modifier.weight(1f), onPiggy)
        }
        // raised Lobby tab (current screen)
        Column(
            Modifier.padding(bottom = 4.dp).width(96.dp).height(90.dp)
                .shadow(16.dp, RoundedCornerShape(24.dp), ambientColor = GlowOrange, spotColor = GlowOrange)
                .background(RimGold, RoundedCornerShape(24.dp)).padding(3.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF6A1FC2), Color(0xFF3A0A68))), RoundedCornerShape(21.dp)),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
        ) {
            Canvas(Modifier.size(38.dp)) { drawHome() }
            Text("Lobby", style = ts(15.sp, Color(0xFFFFD54A), FontWeight.Black), modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
private fun FootDivider() = Box(Modifier.width(1.dp).height(40.dp).background(Color(0x40CDB8EC)))

@Composable
private fun FootItem(icon: SkinIcon, label: String, alert: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier.fillMaxHeight().clickable(remember { MutableInteractionSource() }, null) { Haptics.tap(); onClick() },
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
    ) {
        Box {
            Canvas(Modifier.size(26.dp)) { drawSkinIcon(icon, Color(0xFFF1E6FF)) }
            if (alert) AlertDot(Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-6).dp), 13.dp)
        }
        Spacer(Modifier.height(4.dp))
        Text(label, style = ts(12.sp, Color.White, FontWeight.ExtraBold), maxLines = 1)
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
    this.shadow(16.dp, RoundedCornerShape(corner), ambientColor = GlowOrange, spotColor = GlowOrange)
        .background(RimGold, RoundedCornerShape(corner)).padding(width)
