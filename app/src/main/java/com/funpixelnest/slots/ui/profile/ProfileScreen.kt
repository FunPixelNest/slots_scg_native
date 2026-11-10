package com.funpixelnest.slots.ui.profile

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funpixelnest.slots.audio.Haptics
import com.funpixelnest.slots.audio.Sfx
import com.funpixelnest.slots.data.AppGraph
import com.funpixelnest.slots.data.MAX_NAME
import com.funpixelnest.slots.engine.QuestStat
import com.funpixelnest.slots.ui.common.Montserrat
import com.funpixelnest.slots.ui.common.formatCoins
import com.funpixelnest.slots.ui.flow.Modal
import com.funpixelnest.slots.ui.skin.IconRoundButton
import com.funpixelnest.slots.ui.skin.Skin
import com.funpixelnest.slots.ui.skin.SkinButton
import com.funpixelnest.slots.ui.skin.SkinDialog
import com.funpixelnest.slots.ui.skin.SkinIcon
import com.funpixelnest.slots.ui.skin.SkinTone
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

private fun txt(size: Int, color: Color = Color.White, weight: FontWeight = FontWeight.ExtraBold, spacing: Float = 0f) =
    TextStyle(fontFamily = Montserrat, fontWeight = weight, fontSize = size.sp, color = color, letterSpacing = spacing.sp)

// =========================================================================== avatars

private enum class Glyph { Crown, Star, Diamond, Heart, Clover, Seven, Spade, Moon, Bolt, Coin, Horseshoe, Cherry }

/** 12 avatars: a coloured medallion with a lucky symbol. */
private val AVATARS: List<Triple<Color, Color, Glyph>> = listOf(
    Triple(Color(0xFFFFC94A), Color(0xFFC2700E), Glyph.Crown),
    Triple(Color(0xFFFF5FA2), Color(0xFF9D1747), Glyph.Heart),
    Triple(Color(0xFF38D6FF), Color(0xFF1F4FB0), Glyph.Diamond),
    Triple(Color(0xFF7CF29A), Color(0xFF15803D), Glyph.Clover),
    Triple(Color(0xFFB45CFF), Color(0xFF4A1580), Glyph.Star),
    Triple(Color(0xFFFF6B3D), Color(0xFFB3261E), Glyph.Seven),
    Triple(Color(0xFF9AA8C8), Color(0xFF2A3350), Glyph.Spade),
    Triple(Color(0xFF6A5CFF), Color(0xFF1A1060), Glyph.Moon),
    Triple(Color(0xFFFFE45A), Color(0xFFE08A00), Glyph.Bolt),
    Triple(Color(0xFFFFE9A8), Color(0xFFB8740F), Glyph.Coin),
    Triple(Color(0xFF3FD3A0), Color(0xFF0B6E5A), Glyph.Horseshoe),
    Triple(Color(0xFFFF8AA8), Color(0xFFC2185B), Glyph.Cherry),
)
const val AVATAR_COUNT = 12

/** Player avatar: coloured medallion with a lucky symbol inside a gold ring. */
@Composable
fun Avatar(id: Int, size: Dp, modifier: Modifier = Modifier, ring: Boolean = true) {
    val (top, bottom, glyph) = AVATARS[id.mod(AVATARS.size)]
    Box(
        modifier.size(size).shadow(if (ring) 8.dp else 0.dp, CircleShape)
            .background(if (ring) Brush.verticalGradient(listOf(Color(0xFFFFF6C8), Skin.Gold, Color(0xFFB8740F))) else SolidColor(Color.Transparent), CircleShape)
            .padding(if (ring) size * 0.05f else 0.dp)
            .background(Brush.radialGradient(listOf(top, bottom)), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (glyph == Glyph.Seven) {
            Text("7", style = txt((size.value * 0.5f).toInt(), Color.White, FontWeight.Black))
        } else {
            Canvas(Modifier.size(size * 0.52f)) { drawGlyph(glyph) }
        }
        // glossy highlight
        Canvas(Modifier.fillMaxSize()) {
            drawOval(Color(0x33FFFFFF), Offset(this.size.width * 0.18f, this.size.height * 0.08f), Size(this.size.width * 0.64f, this.size.height * 0.36f))
        }
    }
}

/** Avatar with the player's level on a small gold tag (lobby top bar). */
@Composable
fun AvatarBadge(id: Int, level: Int, size: Dp, modifier: Modifier = Modifier) {
    Box(modifier.size(size + 6.dp), contentAlignment = Alignment.TopCenter) {
        Avatar(id, size)
        Text(
            "$level", style = txt(9, Color(0xFF3A1800), FontWeight.Black),
            modifier = Modifier.align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(Color(0xFFFFF6C8), Skin.Gold)), RoundedCornerShape(50))
                .border(1.dp, Color(0xFF8A5208), RoundedCornerShape(50)).padding(horizontal = 6.dp, vertical = 0.dp),
        )
    }
}

private fun DrawScope.drawGlyph(g: Glyph) {
    val w = size.width
    val white = Color.White
    val shade = Color(0x55000000)
    fun p(vararg pts: Float) = Path().apply { moveTo(pts[0] * w, pts[1] * w); for (i in 2 until pts.size step 2) lineTo(pts[i] * w, pts[i + 1] * w); close() }
    when (g) {
        Glyph.Crown -> {
            drawPath(p(0.05f, 0.75f, 0.05f, 0.3f, 0.3f, 0.5f, 0.5f, 0.15f, 0.7f, 0.5f, 0.95f, 0.3f, 0.95f, 0.75f), white)
            drawRect(white, Offset(0.05f * w, 0.8f * w), Size(0.9f * w, 0.12f * w))
            listOf(0.05f, 0.5f, 0.95f).forEachIndexed { i, x -> drawCircle(white, 0.07f * w, Offset(x * w, if (i == 1) 0.12f * w else 0.27f * w)) }
        }
        Glyph.Star -> {
            val path = Path()
            for (i in 0 until 10) {
                val a = -Math.PI / 2 + i * Math.PI / 5
                val r = if (i % 2 == 0) 0.5f else 0.22f
                val x = (0.5f + r * cos(a).toFloat()) * w; val y = (0.53f + r * sin(a).toFloat()) * w
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close(); drawPath(path, white)
        }
        Glyph.Diamond -> {
            drawPath(p(0.0f, 0.35f, 0.22f, 0.08f, 0.78f, 0.08f, 1.0f, 0.35f, 0.5f, 0.95f), white)
            drawPath(p(0.0f, 0.35f, 1.0f, 0.35f, 0.5f, 0.95f), shade)
        }
        Glyph.Heart -> drawPath(Path().apply {
            moveTo(0.5f * w, 0.92f * w)
            cubicTo(-0.2f * w, 0.45f * w, 0.15f * w, -0.05f * w, 0.5f * w, 0.28f * w)
            cubicTo(0.85f * w, -0.05f * w, 1.2f * w, 0.45f * w, 0.5f * w, 0.92f * w); close()
        }, white)
        Glyph.Clover -> {
            listOf(Offset(0.5f, 0.27f), Offset(0.27f, 0.5f), Offset(0.73f, 0.5f), Offset(0.5f, 0.73f)).forEach { drawCircle(white, 0.22f * w, Offset(it.x * w, it.y * w)) }
            drawLine(white, Offset(0.5f * w, 0.6f * w), Offset(0.62f * w, 1.0f * w), 0.08f * w)
        }
        Glyph.Spade -> {
            drawPath(Path().apply {
                moveTo(0.5f * w, 0.02f * w)
                cubicTo(1.15f * w, 0.5f * w, 0.75f * w, 0.95f * w, 0.5f * w, 0.62f * w)
                cubicTo(0.25f * w, 0.95f * w, -0.15f * w, 0.5f * w, 0.5f * w, 0.02f * w); close()
            }, white)
            drawPath(p(0.5f, 0.6f, 0.68f, 0.98f, 0.32f, 0.98f), white)
        }
        Glyph.Moon -> {
            drawCircle(white, 0.45f * w, Offset(0.5f * w, 0.5f * w))
            drawCircle(Color(0xFF3A2F9A), 0.38f * w, Offset(0.68f * w, 0.4f * w))
            drawCircle(white, 0.05f * w, Offset(0.82f * w, 0.78f * w))
        }
        Glyph.Bolt -> drawPath(p(0.6f, 0.0f, 0.15f, 0.58f, 0.45f, 0.58f, 0.35f, 1.0f, 0.85f, 0.38f, 0.55f, 0.38f), white)
        Glyph.Coin -> {
            drawCircle(white, 0.48f * w, Offset(0.5f * w, 0.5f * w))
            drawCircle(Color(0xFFC8801A), 0.36f * w, Offset(0.5f * w, 0.5f * w), style = Stroke(0.07f * w))
            drawRect(Color(0xFFC8801A), Offset(0.44f * w, 0.28f * w), Size(0.12f * w, 0.44f * w))
        }
        Glyph.Horseshoe -> {
            drawArc(white, 160f, 220f, false, Offset(0.1f * w, 0.08f * w), Size(0.8f * w, 0.8f * w), style = Stroke(0.18f * w))
            drawRect(white, Offset(0.06f * w, 0.62f * w), Size(0.16f * w, 0.3f * w))
            drawRect(white, Offset(0.78f * w, 0.62f * w), Size(0.16f * w, 0.3f * w))
        }
        Glyph.Cherry -> {
            drawLine(white, Offset(0.3f * w, 0.62f * w), Offset(0.65f * w, 0.05f * w), 0.06f * w)
            drawLine(white, Offset(0.72f * w, 0.62f * w), Offset(0.65f * w, 0.05f * w), 0.06f * w)
            drawCircle(white, 0.22f * w, Offset(0.28f * w, 0.74f * w))
            drawCircle(white, 0.22f * w, Offset(0.74f * w, 0.74f * w))
        }
        Glyph.Seven -> Unit
    }
}

// =========================================================================== profile sheet

/** Profile: avatar, editable name, level, avatar picker and lifetime statistics. */
@Composable
fun ProfileSheet(graph: AppGraph, startEditingName: Boolean = false, onClose: () -> Unit) {
    val p = graph.player
    val tick by p.profileFlow.collectAsState()
    val level by p.levelFlow.collectAsState()
    val progress by p.progressFlow.collectAsState()
    var editing by remember { mutableStateOf(startEditingName) }
    val name = remember(tick) { p.playerName }
    val avatar = remember(tick) { p.avatarId }
    Box(
        Modifier.fillMaxSize().background(Color(0x990E0220))
            .clickable(remember { MutableInteractionSource() }, null, onClick = onClose)
            .testTag("profile"),
        contentAlignment = Alignment.BottomCenter,
    ) {
        val sheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        Column(
            Modifier.fillMaxWidth().fillMaxHeight(0.94f)
                .shadow(30.dp, sheet).background(Skin.velvetFill, sheet).border(2.dp, Skin.goldBorder, sheet)
                .clickable(remember { MutableInteractionSource() }, null) { }
                .padding(horizontal = 18.dp),
        ) {
            Box(Modifier.padding(top = 10.dp).align(Alignment.CenterHorizontally).width(42.dp).height(5.dp).background(Color(0x33FFFFFF), RoundedCornerShape(50)))
            Row(Modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("PROFILE", style = txt(18, spacing = 2f), modifier = Modifier.weight(1f))
                IconRoundButton(SkinIcon.Close, size = 38.dp, onClick = onClose)
            }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(top = 12.dp, bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // ---- header
                Row(
                    Modifier.fillMaxWidth().background(Color(0x1AFFFFFF), RoundedCornerShape(20.dp)).border(1.dp, Skin.Hairline, RoundedCornerShape(20.dp)).padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Avatar(avatar, 84.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(name, style = txt(20, Color.White, FontWeight.Black), maxLines = 1, modifier = Modifier.weight(1f, fill = false))
                            Spacer(Modifier.width(8.dp))
                            Box(
                                Modifier.size(30.dp).background(Brush.verticalGradient(listOf(Color(0xFFFFF6C8), Skin.Gold)), CircleShape)
                                    .clickable { Haptics.tap(); editing = true }.testTag("edit-name"),
                                contentAlignment = Alignment.Center,
                            ) { Canvas(Modifier.size(14.dp)) { drawPencil() } }
                        }
                        Text("LEVEL $level", style = txt(11, Skin.GoldLight, spacing = 1.5f), modifier = Modifier.padding(top = 4.dp))
                        Box(Modifier.padding(top = 4.dp).fillMaxWidth().height(8.dp).background(Color(0xFF1A0533), RoundedCornerShape(50)).border(1.dp, Skin.Gold, RoundedCornerShape(50))) {
                            Box(Modifier.fillMaxHeight().fillMaxWidth((progress / 100f).coerceIn(0.03f, 1f)).background(Brush.horizontalGradient(listOf(Color(0xFFFF3DB4), Color(0xFFFFB13D))), RoundedCornerShape(50)))
                        }
                        Text(
                            "Playing since " + SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(p.memberSince)),
                            style = txt(10, Skin.TextSoft, FontWeight.SemiBold), modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
                // ---- avatars
                Text("CHOOSE AVATAR", style = txt(12, Skin.TextSoft, spacing = 2f))
                (0 until AVATAR_COUNT).chunked(6).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { id ->
                            val on = id == avatar
                            Box(
                                Modifier.weight(1f).aspectRatio(1f)
                                    .then(if (on) Modifier.border(3.dp, Skin.Gold, CircleShape).padding(3.dp) else Modifier.padding(3.dp))
                                    .clickable(remember { MutableInteractionSource() }, null) { graph.sound.play(Sfx.Choose); p.avatarId = id },
                                contentAlignment = Alignment.Center,
                            ) {
                                Avatar(id, 48.dp, ring = false)
                                if (on) Box(
                                    Modifier.align(Alignment.BottomEnd).offset(x = 2.dp, y = 2.dp).size(16.dp).background(Skin.Emerald, CircleShape).border(1.5.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) { Text("✓", style = txt(9, Color.White, FontWeight.Black)) }
                            }
                        }
                    }
                }
                // ---- statistics
                Text("STATISTICS", style = txt(12, Skin.TextSoft, spacing = 2f))
                val spins = p.lifetime(QuestStat.SPINS)
                val wins = p.lifetime(QuestStat.WINS)
                val favourite = remember(tick) { graph.themes.maxByOrNull { p.machineSpins(it.id) }?.takeIf { p.machineSpins(it.id) > 0 }?.name ?: "—" }
                val stats = listOf(
                    "Biggest win" to formatCoins(p.biggestWin),
                    "Total coins won" to formatCoins(p.lifetime(QuestStat.COINS_WON)),
                    "Total spins" to formatCoins(spins),
                    "Winning spins" to formatCoins(wins),
                    "Win rate" to (if (spins > 0) "${wins * 100 / spins}%" else "—"),
                    "Big wins" to formatCoins(p.lifetime(QuestStat.BIG_WINS)),
                    "Bonus games" to formatCoins(p.lifetime(QuestStat.BONUS_GAMES)),
                    "Jackpots won" to formatCoins(p.jackpotsWon),
                    "Machines played" to "${p.machinesPlayed} / ${graph.themes.size}",
                    "Favourite machine" to favourite,
                )
                stats.chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { (label, value) -> StatTile(label, value, Modifier.weight(1f)) }
                    }
                }
            }
        }
        if (editing) NameDialog(name, onSave = { p.playerName = it; graph.sound.play(Sfx.Choose); editing = false }) { editing = false }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier) {
    Column(
        modifier.background(Brush.verticalGradient(listOf(Color(0xFF4A1580), Color(0xFF2A0B4A))), RoundedCornerShape(16.dp))
            .border(1.5.dp, Skin.goldBorder, RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(label.uppercase(), style = txt(9, Skin.TextSoft, spacing = 1f), maxLines = 1)
        Text(value, style = txt(17, Skin.GoldLight, FontWeight.Black), maxLines = 1, modifier = Modifier.padding(top = 3.dp))
    }
}

/** Rename dialog: up to [MAX_NAME] characters. */
@Composable
private fun NameDialog(current: String, onSave: (String) -> Unit, onCancel: () -> Unit) {
    var text by remember { mutableStateOf(current) }
    Modal(onCancel, tag = "name-dialog") {
        SkinDialog("Your name", Modifier.fillMaxWidth(), tone = SkinTone.Purple) {
            BasicTextField(
                value = text,
                onValueChange = { text = it.replace("\n", "").take(MAX_NAME) },
                singleLine = true,
                textStyle = txt(20, Color.White, FontWeight.Black).copy(textAlign = TextAlign.Center),
                cursorBrush = SolidColor(Skin.Gold),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth().background(Color(0xFF14052A), RoundedCornerShape(14.dp))
                    .border(2.dp, Skin.Gold, RoundedCornerShape(14.dp)).padding(horizontal = 14.dp, vertical = 12.dp).testTag("name-field"),
            )
            Text("${text.length} / $MAX_NAME", style = txt(10, Skin.TextSoft, FontWeight.SemiBold), modifier = Modifier.padding(top = 6.dp))
            Spacer(Modifier.height(14.dp))
            SkinButton("SAVE", Modifier.fillMaxWidth().height(48.dp), tone = SkinTone.Green, enabled = text.isNotBlank()) { onSave(text) }
            Spacer(Modifier.height(8.dp))
            SkinButton("CANCEL", Modifier.fillMaxWidth().height(42.dp), tone = SkinTone.Glass, textSize = 13.sp, onClick = onCancel)
        }
    }
}

private fun DrawScope.drawPencil() {
    val w = size.width
    val c = Color(0xFF4A2200)
    drawLine(c, Offset(0.15f * w, 0.85f * w), Offset(0.8f * w, 0.2f * w), 0.22f * w)
    drawPath(Path().apply { moveTo(0.02f * w, 0.98f * w); lineTo(0.08f * w, 0.7f * w); lineTo(0.3f * w, 0.92f * w); close() }, c)
}
