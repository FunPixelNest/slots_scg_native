package com.funpixelnest.slots.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.ImageBitmap
import com.funpixelnest.slots.audio.SoundPlayer
import com.funpixelnest.slots.engine.PlayerRules
import com.funpixelnest.slots.engine.PlayerStore
import com.funpixelnest.slots.engine.ThemeConfig
import com.funpixelnest.slots.engine.ThemeIndexEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.builtins.ListSerializer

/** Coins, level, bets and settings saved in SharedPreferences (Unity used PlayerPrefs). */
class PrefsPlayerStore(private val prefs: SharedPreferences) : PlayerStore {
    private val _coins = MutableStateFlow(prefs.getLong(K_COINS, PlayerRules.START_COINS))
    val coinsFlow: StateFlow<Long> = _coins
    private val _level = MutableStateFlow(prefs.getInt(K_LEVEL, 1))
    val levelFlow: StateFlow<Int> = _level

    override var coins: Long
        get() = _coins.value
        set(v) { _coins.value = v; prefs.edit().putLong(K_COINS, v).apply() }
    override var level: Int
        get() = _level.value
        set(v) { _level.value = v; prefs.edit().putInt(K_LEVEL, v).apply() }
    override var levelProgress: Float
        get() = prefs.getFloat(K_PROGRESS, 0f)
        set(v) { prefs.edit().putFloat(K_PROGRESS, v).apply() }

    override fun lineBet(themeId: String): Int? =
        prefs.getInt("bet_$themeId", -1).takeIf { it > 0 }

    override fun setLineBet(themeId: String, bet: Int) { prefs.edit().putInt("bet_$themeId", bet).apply() }

    var soundOn: Boolean
        get() = prefs.getBoolean("sound", true)
        set(v) { prefs.edit().putBoolean("sound", v).apply() }
    var musicOn: Boolean
        get() = prefs.getBoolean("music", true)
        set(v) { prefs.edit().putBoolean("music", v).apply() }

    /** Time gift (Unity TimeGiftController): free coins every few hours. */
    var lastGiftAt: Long
        get() = prefs.getLong("gift_at", 0L)
        set(v) { prefs.edit().putLong("gift_at", v).apply() }

    companion object {
        private const val K_COINS = "coins"
        private const val K_LEVEL = "level"
        private const val K_PROGRESS = "level_progress"
        const val GIFT_COINS = 2500L
        const val GIFT_INTERVAL_MS = 3 * 60 * 60 * 1000L
    }
}

/** Process-wide singletons (kept tiny on purpose; no DI framework needed). */
class AppGraph(context: Context) {
    private val app = context.applicationContext
    val common = ApkAssetSource(app.assets, "common")
    val player = PrefsPlayerStore(app.getSharedPreferences("player", Context.MODE_PRIVATE))
    val sound = SoundPlayer(app, player)

    val themes: List<ThemeIndexEntry> by lazy {
        ThemeConfig.json.decodeFromString(ListSerializer(ThemeIndexEntry.serializer()), common.readText("themes.json"))
            .sortedBy { it.order }
    }

    private val openThemes = HashMap<String, ThemeAssets>()

    /** Every theme is packaged in the app; null only for an unknown id. */
    fun theme(id: String): ThemeAssets? = synchronized(openThemes) {
        openThemes[id] ?: run {
            if (themes.none { it.id == id }) return null
            val src = ApkAssetSource(app.assets, "themes/$id")
            if (!src.exists("theme.json")) return null
            ThemeAssets.open(src).also { openThemes[id] = it }
        }
    }

    /** Lobby / shared UI images from app/src/main/assets/common. */
    val commonImages = ThemeAssets(
        ThemeConfig(id = "common", name = "common", symbols = emptyList(), reelStrips = emptyList(), payTable = emptyList()),
        common, emptyMap(),
    )

    fun commonImage(key: String): ImageBitmap? = commonImages.loadBlocking(key)

    companion object {
        @Volatile private var instance: AppGraph? = null
        fun get(context: Context): AppGraph =
            instance ?: synchronized(this) { instance ?: AppGraph(context).also { instance = it } }
    }
}
