package com.funpixelnest.slots.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.funpixelnest.slots.audio.SoundPlayer
import com.funpixelnest.slots.engine.Economy
import com.funpixelnest.slots.engine.Rewards
import com.funpixelnest.slots.engine.JackpotTier
import com.funpixelnest.slots.engine.PlayerStore
import com.funpixelnest.slots.engine.Quest
import com.funpixelnest.slots.engine.QuestStat
import com.funpixelnest.slots.engine.Quests
import com.funpixelnest.slots.engine.ThemeConfig
import com.funpixelnest.slots.engine.ThemeIndexEntry
import com.funpixelnest.slots.engine.Unlocks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.builtins.ListSerializer

/** Coins, level, bets, bonuses and settings saved in SharedPreferences (Unity used PlayerPrefs). */
const val DEFAULT_NAME = "Lucky Player"
const val MAX_NAME = 16

class PrefsPlayerStore(private val prefs: SharedPreferences) : PlayerStore {
    init {
        // brand-new players start at 0 and get the welcome bonus; players of earlier builds
        // (5,000 coins) are moved onto the million-coin economy and skip the welcome
        if (!prefs.contains(K_COINS)) {
            prefs.edit().putLong(K_COINS, 0L).putBoolean(K_ECONOMY_V2, true).apply()
        } else if (!prefs.getBoolean(K_ECONOMY_V2, false)) {
            prefs.edit().putBoolean("welcome_claimed", true).apply()
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
        prefs.getInt("tbet_$themeId", -1).takeIf { it > 0 }

    override fun setLineBet(themeId: String, bet: Int) { prefs.edit().putInt("tbet_$themeId", bet).apply() }

    override fun jackpotPool(themeId: String, tier: JackpotTier): Long = prefs.getLong("jp_${themeId}_$tier", 0L)
    override fun setJackpotPool(themeId: String, tier: JackpotTier, value: Long) {
        prefs.edit().putLong("jp_${themeId}_$tier", value).apply()
    }

    var soundOn: Boolean
        get() = prefs.getBoolean("sound", true)
        set(v) { prefs.edit().putBoolean("sound", v).apply() }
    var hapticsOn: Boolean
        get() = prefs.getBoolean("haptics", true)
        set(v) { prefs.edit().putBoolean("haptics", v).apply() }
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

    // ---------------------------------------------------------------- first launch

    var disclaimerAccepted: Boolean
        get() = prefs.getBoolean("disclaimer_ok", false)
        set(v) { prefs.edit().putBoolean("disclaimer_ok", v).apply() }
    var welcomeClaimed: Boolean
        get() = prefs.getBoolean("welcome_claimed", false)
        set(v) { prefs.edit().putBoolean("welcome_claimed", v).apply() }

    // ---------------------------------------------------------------- daily wheel (once a day)

    var lastWheelAt: Long
        get() = prefs.getLong("wheel_at", 0L)
        set(v) { prefs.edit().putLong("wheel_at", v).apply() }
    fun wheelReadyIn(now: Long): Long = (lastWheelAt + Economy.DAY_MS - now).coerceAtLeast(0L)

    // ---------------------------------------------------------------- piggy bank

    private val _piggy = MutableStateFlow(prefs.getLong("piggy", 0L))
    val piggyFlow: StateFlow<Long> = _piggy
    var piggy: Long
        get() = _piggy.value
        set(v) { _piggy.value = v; prefs.edit().putLong("piggy", v).apply() }
    fun piggyCap(): Long = Rewards.piggyCap(level)
    fun feedPiggy(totalBet: Long) { piggy = (piggy + (totalBet * Rewards.PIGGY_SHARE).toLong()).coerceAtMost(piggyCap()) }
    fun canBreakPiggy(): Boolean = piggy >= piggyCap() * Rewards.PIGGY_MIN_FILL

    /** Hand out coins (any source); returns the amount for chaining. */
    fun grant(amount: Long): Long { coins += amount; return amount }

    // ------------------------------------------------------------------ profile
    /** Bumped when the name, avatar or stats change, so screens recompose. */
    val profileFlow = MutableStateFlow(0)

    var playerName: String
        get() = prefs.getString("player_name", null) ?: DEFAULT_NAME
        set(v) { prefs.edit().putString("player_name", v.trim().take(MAX_NAME).ifEmpty { DEFAULT_NAME }).apply(); profileFlow.value++ }

    var avatarId: Int
        get() = prefs.getInt("avatar_id", 0)
        set(v) { prefs.edit().putInt("avatar_id", v).apply(); profileFlow.value++ }

    /** When the player first opened the game (set on first read). */
    val memberSince: Long
        get() = prefs.getLong("member_since", 0L).takeIf { it > 0 }
            ?: System.currentTimeMillis().also { prefs.edit().putLong("member_since", it).apply() }

    /** Lifetime counter used for tasks/missions (spins, wins, coins won, big wins, bonus games). */
    fun lifetime(stat: QuestStat): Long = prefs.getLong("ql_${stat.name}", 0)
    val biggestWin: Long get() = prefs.getLong("stat_biggest_win", 0)
    val jackpotsWon: Long get() = prefs.getLong("stat_jackpots", 0)
    fun machineSpins(themeId: String): Long = prefs.getLong("stat_spins_$themeId", 0)
    val machinesPlayed: Int get() = prefs.getStringSet("played_machines", emptySet()).orEmpty().size

    /** Per-spin profile stats: biggest win, jackpots, spins per machine (for the favourite). */
    fun recordSpin(themeId: String, win: Long, jackpot: Boolean) {
        val e = prefs.edit().putLong("stat_spins_$themeId", machineSpins(themeId) + 1)
        if (win > biggestWin) e.putLong("stat_biggest_win", win)
        if (jackpot) e.putLong("stat_jackpots", jackpotsWon + 1)
        e.apply()
        profileFlow.value++
    }

    // ------------------------------------------------------------------ tasks & missions
    /** Bumped whenever quest progress changes, so lobby badges recompose. */
    val questsFlow = MutableStateFlow(0)

    private fun rollQuestDay(now: Long) {
        val today = Quests.day(now)
        if (prefs.getLong("qd_day", -1) != today) {
            val e = prefs.edit().putLong("qd_day", today).remove("qd_claimed")
            QuestStat.entries.forEach { e.remove("qd_${it.name}") }
            e.apply()
        }
    }

    /** Count [amount] towards today's tasks and lifetime missions. */
    fun track(stat: QuestStat, amount: Long = 1, now: Long = System.currentTimeMillis()) {
        if (amount <= 0) return
        rollQuestDay(now)
        prefs.edit()
            .putLong("qd_${stat.name}", prefs.getLong("qd_${stat.name}", 0) + amount)
            .putLong("ql_${stat.name}", prefs.getLong("ql_${stat.name}", 0) + amount)
            .apply()
        questsFlow.value++
    }

    fun trackMachinePlayed(themeId: String) {
        val played = prefs.getStringSet("played_machines", emptySet()).orEmpty()
        if (themeId !in played) {
            prefs.edit().putStringSet("played_machines", played + themeId).apply()
            questsFlow.value++
        }
    }

    fun questProgress(q: Quest, daily: Boolean, now: Long = System.currentTimeMillis()): Long {
        rollQuestDay(now)
        return when (q.stat) {
            QuestStat.LEVEL -> level.toLong()
            QuestStat.MACHINES -> prefs.getStringSet("played_machines", emptySet()).orEmpty().size.toLong()
            else -> prefs.getLong(if (daily) "qd_${q.stat.name}" else "ql_${q.stat.name}", 0)
        }.coerceAtMost(q.target)
    }

    private fun claimedKey(daily: Boolean) = if (daily) "qd_claimed" else "ql_claimed"

    fun isClaimed(q: Quest, daily: Boolean, now: Long = System.currentTimeMillis()): Boolean {
        rollQuestDay(now)
        return q.id in prefs.getStringSet(claimedKey(daily), emptySet()).orEmpty()
    }

    fun canClaim(q: Quest, daily: Boolean, now: Long = System.currentTimeMillis()) =
        !isClaimed(q, daily, now) && questProgress(q, daily, now) >= q.target

    fun markClaimed(q: Quest, daily: Boolean) {
        val set = prefs.getStringSet(claimedKey(daily), emptySet()).orEmpty()
        prefs.edit().putStringSet(claimedKey(daily), set + q.id).apply()
        questsFlow.value++
    }

    fun claimableTasks(now: Long = System.currentTimeMillis()) = Quests.DAILY_TASKS.count { canClaim(it, true, now) }
    fun claimableMissions(now: Long = System.currentTimeMillis()) = Quests.MISSIONS.count { canClaim(it, false, now) }


    // ---------------------------------------------------------------- machine unlocks

    fun videosWatched(themeId: String): Int = prefs.getInt("video_$themeId", 0)
    fun addVideoWatched(themeId: String) { prefs.edit().putInt("video_$themeId", videosWatched(themeId) + 1).apply() }

    fun isOpen(entry: ThemeIndexEntry): Boolean = Unlocks.isOpen(entry.order, level, videosWatched(entry.id))

    // ---------------------------------------------------------------- free coins for a video

    var lastVideoCoinsAt: Long
        get() = prefs.getLong("video_coins_at", 0L)
        set(v) { prefs.edit().putLong("video_coins_at", v).apply() }

    fun videoCoinsReadyIn(now: Long): Long = (lastVideoCoinsAt + VIDEO_COINS_INTERVAL_MS - now).coerceAtLeast(0L)
    fun videoCoinsAmount(): Long = 100_000L + 20_000L * (level - 1)

    fun claimVideoCoins(now: Long): Long {
        if (videoCoinsReadyIn(now) > 0) return 0
        lastVideoCoinsAt = now
        val amount = videoCoinsAmount()
        coins += amount
        return amount
    }

    companion object {
        private const val K_COINS = "coins"
        private const val K_LEVEL = "level"
        private const val K_PROGRESS = "level_progress"
        private const val K_ECONOMY_V2 = "economy_v2"
        const val VIDEO_COINS_INTERVAL_MS = 10 * 60 * 1000L
    }
}

/** Process-wide singletons (kept tiny on purpose; no DI framework needed). */
class AppGraph(context: Context) {
    private val app = context.applicationContext
    val common = ApkAssetSource(app.assets, "common")
    val player = PrefsPlayerStore(app.getSharedPreferences("player", Context.MODE_PRIVATE))
    val haptics = com.funpixelnest.slots.audio.Haptics(app, player)
    val sound = SoundPlayer(app, player, haptics)
    val ads = PlaceholderRewardedAds()
    val billing: Billing = PlaceholderBilling()

    /** Store / piggy bank sheet shown above the current screen. */
    var overlay by androidx.compose.runtime.mutableStateOf<Overlay?>(null)
    fun openStore() { overlay = Overlay.Store }
    fun openPiggy() { overlay = Overlay.Piggy }
    fun openProfile() { overlay = Overlay.Profile }

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
