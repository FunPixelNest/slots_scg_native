package com.funpixelnest.slots.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.ImageBitmap
import com.funpixelnest.slots.audio.SoundPlayer
import com.funpixelnest.slots.engine.Economy
import com.funpixelnest.slots.engine.JackpotTier
import com.funpixelnest.slots.engine.PlayerStore
import com.funpixelnest.slots.engine.ThemeConfig
import com.funpixelnest.slots.engine.ThemeIndexEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.builtins.ListSerializer

/** Coins, level, bets, bonuses and settings saved in SharedPreferences (Unity used PlayerPrefs). */
class PrefsPlayerStore(private val prefs: SharedPreferences) : PlayerStore {
    init {
        // move players of the first build (5,000 coins) onto the million-coin economy
        if (!prefs.getBoolean(K_ECONOMY_V2, false)) {
            prefs.edit()
                .putLong(K_COINS, maxOf(prefs.getLong(K_COINS, 0L), Economy.START_COINS))
                .putBoolean(K_ECONOMY_V2, true)
                .apply()
        }
    }

    private val _coins = MutableStateFlow(prefs.getLong(K_COINS, Economy.START_COINS))
    val coinsFlow: StateFlow<Long> = _coins
    private val _level = MutableStateFlow(prefs.getInt(K_LEVEL, 1))
    val levelFlow: StateFlow<Int> = _level
    private val _progress = MutableStateFlow(prefs.getFloat(K_PROGRESS, 0f))
    val progressFlow: StateFlow<Float> = _progress

    override var coins: Long
        get() = _coins.value
        set(v) { _coins.value = v; prefs.edit().putLong(K_COINS, v).apply() }
    override var level: Int
        get() = _level.value
        set(v) { _level.value = v; prefs.edit().putInt(K_LEVEL, v).apply() }
    override var levelProgress: Float
        get() = _progress.value
        set(v) { _progress.value = v; prefs.edit().putFloat(K_PROGRESS, v).apply() }

    override fun lineBet(themeId: String): Int? =
        prefs.getInt("bet_$themeId", -1).takeIf { it > 0 }

    override fun setLineBet(themeId: String, bet: Int) { prefs.edit().putInt("bet_$themeId", bet).apply() }

    override fun jackpotPool(themeId: String, tier: JackpotTier): Long = prefs.getLong("jp_${themeId}_$tier", 0L)
    override fun setJackpotPool(themeId: String, tier: JackpotTier, value: Long) {
        prefs.edit().putLong("jp_${themeId}_$tier", value).apply()
    }

    var soundOn: Boolean
        get() = prefs.getBoolean("sound", true)
        set(v) { prefs.edit().putBoolean("sound", v).apply() }
    var musicOn: Boolean
        get() = prefs.getBoolean("music", true)
        set(v) { prefs.edit().putBoolean("music", v).apply() }

    var lastHourlyAt: Long
        get() = prefs.getLong("hourly_at", 0L)
        set(v) { prefs.edit().putLong("hourly_at", v).apply() }
    var lastDailyAt: Long
        get() = prefs.getLong("daily_at", 0L)
        set(v) { prefs.edit().putLong("daily_at", v).apply() }
    var lastDailyDay: Int
        get() = prefs.getInt("daily_day", 0)
        set(v) { prefs.edit().putInt("daily_day", v).apply() }

    /** Milliseconds until the hourly bonus can be claimed (0 = ready). */
    fun hourlyReadyIn(now: Long): Long = (lastHourlyAt + Economy.HOURLY_INTERVAL_MS - now).coerceAtLeast(0L)

    fun claimHourly(now: Long): Long {
        if (hourlyReadyIn(now) > 0) return 0
        val amount = Economy.hourlyBonus(level)
        lastHourlyAt = now
        coins += amount
        return amount
    }

    fun nextDailyDay(now: Long): Int? = Economy.nextDailyDay(lastDailyAt, lastDailyDay, now)

    fun claimDaily(now: Long): Long {
        val day = nextDailyDay(now) ?: return 0
        val amount = Economy.dailyBonus(day, level)
        lastDailyAt = now
        lastDailyDay = day
        coins += amount
        return amount
    }

    companion object {
        private const val K_COINS = "coins"
        private const val K_LEVEL = "level"
        private const val K_PROGRESS = "level_progress"
        private const val K_ECONOMY_V2 = "economy_v2"
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

        /** Tests only: forget the singleton so the next [get] binds to a fresh Application. */
        fun resetForTests() { instance = null }
    }
}
