package com.funpixelnest.slots.ui.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.funpixelnest.slots.audio.Sfx
import com.funpixelnest.slots.data.AppGraph
import com.funpixelnest.slots.data.Frame
import com.funpixelnest.slots.data.ThemeAssets
import com.funpixelnest.slots.engine.Cell
import com.funpixelnest.slots.engine.SpinOutcome
import com.funpixelnest.slots.engine.FeatureEffects
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sin


/**
 * Draws the 5x3 reel window and animates spins (Unity SlotGroupBehavior):
 * a short wind-up, fast spin with blurred symbols, then a bouncy stop, reel by reel.
 */
@Composable
fun ReelsView(
    graph: AppGraph,
    assets: ThemeAssets,
    request: SpinRequest?,
    initialStops: List<Int>,
    outcome: SpinOutcome?,
    showWins: Boolean,
    /** Feature effects of the shown spin (extra wilds), highlighted once the reels rest. */
    effects: FeatureEffects? = null,
    onStopped: () -> Unit,
    /** Theme colour of the board, used for the reel columns and win highlights. */
    accent: Color = Color(0xFFF5C451),
    modifier: Modifier = Modifier,
) {
    val cfg = assets.config
    val strips = cfg.reelStrips
    val positions = remember { initialStops.map { Animatable(it.toFloat()) } }
    val onStoppedNow by rememberUpdatedState(onStopped)

    // symbol images (and blurred variants) resolved once
    val images = remember { mutableStateMapOf<String, ImageBitmap>() }
    LaunchedEffect(assets) {
        cfg.symbols.forEach { s ->
            s.image?.let { k -> assets.load(k)?.let { images[k] = it } }
            s.blur?.let { k -> assets.load(k)?.let { images[k] = it } }
        }
    }

    // win animation frames for symbols that are part of the current win
    val frames = remember { mutableStateMapOf<Int, List<Pair<Frame, ImageBitmap>>>() }
    LaunchedEffect(outcome, showWins) {
        if (!showWins || outcome == null) return@LaunchedEffect
        val ids = outcome.winningCells.map { outcome.grid[it.reel][it.row] }.toSet()
        for (id in ids) if (id !in frames) {
            val list = assets.symbolFrames(id).mapNotNull { f -> assets.load(f.image)?.let { f to it } }
            if (list.isNotEmpty()) frames[id] = list
        }
    }

    var clock by remember { mutableLongStateOf(0L) }
    LaunchedEffect(showWins) {
        val start = withFrameMillis { it }
        while (showWins) withFrameMillis { clock = it - start }
    }

    LaunchedEffect(request?.nonce) {
        val req = request ?: return@LaunchedEffect
        if (req.instant) {
            positions.forEachIndexed { reel, anim -> anim.snapTo(req.stops[reel].toFloat()) }
            return@LaunchedEffect
        }
        coroutineScope {
            positions.forEachIndexed { reel, anim ->
                launch {
                    val n = strips[reel].size
                    val cur = anim.value
                    val target = req.stops[reel]
                    // reel moves downwards, i.e. strip index decreases
                    val dist = (((cur - target) % n) + n) % n
                    val travel = dist + n * (2 + reel)
                    delay(reel * 90L)
                    anim.animateTo(cur + 0.3f, tween(140))                     // wind-up
                    anim.animateTo(cur - travel - 0.28f, tween(900 + reel * 260, easing = LinearOutSlowInEasing))
                    anim.animateTo(cur - travel, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium))
                    anim.snapTo(target.toFloat())
                    graph.sound.play(Sfx.ReelStop, 0.6f)
                }
            }
        }
        onStoppedNow()
    }

    Box(modifier.clipToBounds()) {
        Canvas(Modifier.fillMaxSize()) {
            val cellW = size.width / cfg.reels
            val cellH = size.height / cfg.rows
            val wins = if (showWins) outcome?.winningCells.orEmpty() else emptySet()
            val pulse = 1f + 0.08f * sin(clock / 140f)

            for (reel in 0 until cfg.reels) {
                val left = reel * cellW
                // glass reel column with a thin gap between reels
                val gap = cellW * 0.025f
                drawRoundRect(
                    Brush.verticalGradient(
                        0f to Color.White.copy(alpha = 0.03f), 0.5f to Color.White.copy(alpha = 0.085f), 1f to Color.White.copy(alpha = 0.03f),
                    ),
                    Offset(left + gap, 0f), Size(cellW - 2 * gap, size.height), CornerRadius(cellW * 0.1f),
                )
                // feature glow: expanded reel / extra wild cells
                if (showWins && effects != null) {
                    val glow = Color(0xFFFFD54F).copy(alpha = 0.28f + 0.12f * sin(clock / 160f))
                    if (reel in effects.expandedReels) {
                        drawRoundRect(glow, Offset(left + 2, 2f), Size(cellW - 4, size.height - 4), CornerRadius(cellW * 0.12f))
                    }
                    (effects.randomWilds + effects.stickyWilds).filter { it.reel == reel }.forEach { c ->
                        drawRoundRect(glow, Offset(left + 2, c.row * cellH + 2), Size(cellW - 4, cellH - 4), CornerRadius(cellW * 0.12f))
                    }
                }
                val anim = positions[reel]
                val p = anim.value
                val fast = abs(anim.velocity) > 6f
                val strip = strips[reel]
                val n = strip.size
                val base = floor(p).toInt()
                val frac = p - base
                clipRect(left, 0f, left + cellW, size.height) {
                    for (r in -1..cfg.rows) {
                        // at rest, show the final grid (features may have turned symbols wild)
                        val idx = if (showWins && frac == 0f && outcome != null && r in 0 until cfg.rows) outcome.grid[reel][r]
                        else strip[(((base + r) % n) + n) % n]
                        val sym = cfg.symbols[idx]
                        val top = (r - frac) * cellH
                        val cell = Cell(reel, r)
                        val isWin = frac == 0f && cell in wins
                        val dim = wins.isNotEmpty() && !isWin && frac == 0f
                        val key = if (fast && sym.blur != null) sym.blur else sym.image
                        val anims = if (isWin) frames[idx] else null
                        if (isWin) {
                            val inset = cellW * 0.035f
                            drawRoundRect(
                                Brush.radialGradient(listOf(accent.copy(alpha = 0.45f), accent.copy(alpha = 0.08f)), Offset(left + cellW / 2, top + cellH / 2), cellW * 0.7f),
                                Offset(left + inset, top + inset), Size(cellW - 2 * inset, cellH - 2 * inset), CornerRadius(cellW * 0.12f),
                            )
                        }
                        val pad = min(cellW, cellH) * 0.04f
                        val dst = Offset(left + pad, top + pad)
                        val dstSize = Size(cellW - 2 * pad, cellH - 2 * pad)
                        if (anims != null) {
                            val (f, img) = anims[((clock / 50) % anims.size).toInt()]
                            drawFrame(img, f, dst, dstSize)
                        } else {
                            val img = key?.let { images[it] } ?: sym.image?.let { images[it] }
                            if (img != null) {
                                val s = if (isWin) pulse else 1f
                                withTransform({ scale(s, s, Offset(left + cellW / 2, top + cellH / 2)) }) {
                                    drawImageFit(img, dst, dstSize, alpha = if (dim) 0.38f else 1f)
                                }
                            }
                        }
                    }
                }
            }

            // drum shading: symbols roll in and out of the dark at the top and bottom edges
            val fade = size.height * 0.09f
            drawRect(Brush.verticalGradient(listOf(Color(0xCC05070F), Color.Transparent), 0f, fade), Offset.Zero, Size(size.width, fade))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC05070F)), size.height - fade, size.height), Offset(0f, size.height - fade), Size(size.width, fade))

            if (showWins && outcome != null && outcome.lineWins.isNotEmpty()) {
                drawWinLines(outcome, clock, cellW, cellH, accent)
            }
        }
    }
}

/** Cycle through the distinct winning lines (best first): a glowing gold line and gold-rimmed cells. */
private fun DrawScope.drawWinLines(outcome: SpinOutcome, clock: Long, cellW: Float, cellH: Float, accent: Color) {
    val lines = outcome.lineWins.sortedByDescending { it.pay }.take(24)
    val i = ((clock / 900) % lines.size).toInt()
    val w = lines[i]
    val gold = Color(0xFFFFD66B)
    val path = Path()
    w.cells.forEachIndexed { k, c ->
        val x = (c.reel + 0.5f) * cellW
        val y = (c.row + 0.5f) * cellH
        if (k == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    val t = 0.75f + 0.25f * sin(clock / 120f)
    w.cells.forEach { c -> drawCellRim(c, cellW, cellH, gold, accent, t) }
    drawPath(path, accent.copy(alpha = 0.28f * t), style = Stroke(cellH * 0.14f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawPath(path, Color.Black.copy(alpha = 0.45f), style = Stroke(cellH * 0.065f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawPath(path, gold, style = Stroke(cellH * 0.04f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawPath(path, Color.White.copy(alpha = 0.8f), style = Stroke(cellH * 0.012f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

private fun DrawScope.drawCellRim(c: Cell, cellW: Float, cellH: Float, gold: Color, accent: Color, t: Float) {
    val inset = cellW * 0.035f
    val tl = Offset(c.reel * cellW + inset, c.row * cellH + inset)
    val sz = Size(cellW - 2 * inset, cellH - 2 * inset)
    val r = CornerRadius(cellW * 0.12f)
    drawRoundRect(accent.copy(alpha = 0.35f * t), tl, sz, r, style = Stroke(cellW * 0.06f))
    drawRoundRect(gold, tl, sz, r, style = Stroke(cellW * 0.022f))
}

/** Draw [img] inside the box keeping its aspect ratio (or stretched). */
fun DrawScope.drawImageFit(img: ImageBitmap, topLeft: Offset, box: Size, alpha: Float = 1f, stretch: Boolean = false) {
    if (stretch) {
        drawImage(
            img, IntOffset.Zero, IntSize(img.width, img.height),
            IntOffset(topLeft.x.toInt(), topLeft.y.toInt()), IntSize(box.width.toInt(), box.height.toInt()), alpha = alpha,
        )
        return
    }
    val s = min(box.width / img.width, box.height / img.height)
    val w = img.width * s
    val h = img.height * s
    val x = topLeft.x + (box.width - w) / 2
    val y = topLeft.y + (box.height - h) / 2
    drawImage(
        img, IntOffset.Zero, IntSize(img.width, img.height),
        IntOffset(x.toInt(), y.toInt()), IntSize(w.toInt().coerceAtLeast(1), h.toInt().coerceAtLeast(1)), alpha = alpha,
    )
}

private fun DrawScope.drawFrame(img: ImageBitmap, f: Frame, topLeft: Offset, box: Size) {
    val srcSize = f.size ?: IntSize(img.width, img.height)
    val s = min(box.width / srcSize.width, box.height / srcSize.height)
    val w = srcSize.width * s
    val h = srcSize.height * s
    drawImage(
        img, f.offset, srcSize,
        IntOffset((topLeft.x + (box.width - w) / 2).toInt(), (topLeft.y + (box.height - h) / 2).toInt()),
        IntSize(w.toInt().coerceAtLeast(1), h.toInt().coerceAtLeast(1)),
    )
}
