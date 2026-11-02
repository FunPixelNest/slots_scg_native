package com.funpixelnest.slots.engine

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * One slot machine theme, as produced by tools/unity_import (theme.json).
 * Image fields are asset keys relative to the theme folder, without extension (".webp").
 */
@Serializable
data class ThemeConfig(
    val id: String,
    val name: String,
    val order: Int = 0,
    val source: String = "",
    val reels: Int = 5,
    val rows: Int = 3,
    /** "ways": every row combination is a line (3^5 = 243). "lines": only [lines]. */
    val mode: String = "ways",
    val background: String? = null,
    /** Vertical background for the portrait layout, when the Unity theme has one. */
    val backgroundPortrait: String? = null,
    val frame: String? = null,
    val reelBackground: String? = null,
    val title: String? = null,
    val symbols: List<SymbolDef>,
    val wildId: Int = -1,
    val scatterId: Int = -1,
    val reelStrips: List<List<Int>>,
    val payTable: List<PayLineDef>,
    val scatterPays: List<ScatterPayDef> = emptyList(),
    val wildInFirstPosition: Boolean = false,
    val wildSubstitute: List<Boolean> = emptyList(),
    val bet: BetConfig = BetConfig(),
    /** Explicit lines (row index per reel) when mode == "lines". */
    val lines: List<List<Int>> = emptyList(),
    val ui: Map<String, String> = emptyMap(),
    /** UI keys whose art already shows its own text, so no label is drawn over it. */
    val uiLabeled: List<String> = emptyList(),
) {
    fun canSubstitute(symbolId: Int): Boolean =
        symbolId != wildId && symbolId != scatterId && wildSubstitute.getOrElse(symbolId) { true }

    companion object {
        val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
        fun parse(text: String): ThemeConfig = json.decodeFromString(serializer(), text)
    }
}

@Serializable
data class SymbolDef(
    val key: String,
    val name: String = key,
    val image: String? = null,
    val blur: String? = null,
    val anim: SymbolAnim? = null,
)

/** Either a sprite sheet (frames listed in sprites.json) or a list of frame images. */
@Serializable
data class SymbolAnim(val sheet: String? = null, val frames: List<String> = emptyList())

/** Pays when the symbols on a line match [line] from the left; -1 means "any symbol". */
@Serializable
data class PayLineDef(
    val line: List<Int>,
    val pay: Int,
    val freeSpins: Int = 0,
    val payMult: Int = 1,
    val freeSpinsMult: Int = 1,
)

@Serializable
data class ScatterPayDef(
    val count: Int,
    val pay: Int = 0,
    val freeSpins: Int = 0,
    val payMult: Int = 1,
    val freeSpinsMult: Int = 1,
)

@Serializable
data class BetConfig(
    val defaultLineBet: Int = 1,
    val maxLineBet: Int = 20,
    /** Unity multiplied won free spins by the line bet; off by default. */
    val freeSpinsScaleWithBet: Boolean = false,
)

/** Entry of common/themes.json used by the lobby. */
@Serializable
data class ThemeIndexEntry(
    val id: String,
    val name: String,
    val order: Int,
    val tile: String,
    val pack: String,
    val sizeBytes: Long = 0,
    val source: String = "",
)
