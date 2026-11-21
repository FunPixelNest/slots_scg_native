package com.funpixelnest.slots.ads

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

private const val TAG = "SlotsFirebase"

/** True under Robolectric screenshot tests: no SDKs are started there. */
val isRobolectric: Boolean get() = Build.FINGERPRINT == "robolectric"

/**
 * Firebase is optional at build time: it only starts when app/google-services.json was present
 * (the Gradle build applies the google-services plugin then). Every call here is safe without it.
 */
object FirebaseHub {
    fun available(context: Context): Boolean =
        !isRobolectric && runCatching { FirebaseApp.getApps(context).isNotEmpty() }.getOrDefault(false)
}

/** Remote Config keys and their in-app defaults (used until a fetch succeeds or without Firebase). */
object RemoteKeys {
    /** Show an interstitial every N paid spins (0 = never). */
    const val INTERSTITIAL_EVERY_SPINS = "interstitial_every_spins"
    /** Every Nth tap on the home (back to lobby) button. */
    const val INTERSTITIAL_EVERY_HOME = "interstitial_every_home"
    /** Every Nth close of the level-up popup. */
    const val INTERSTITIAL_EVERY_LEVEL_UP = "interstitial_every_level_up_close"
    /** Every Nth close of the big / mega / huge win popup. */
    const val INTERSTITIAL_EVERY_BIG_WIN = "interstitial_every_big_win_close"
    /** Every Nth close of the free spins win popup. */
    const val INTERSTITIAL_EVERY_FREE_SPINS_WIN = "interstitial_every_free_spins_win_close"

    val DEFAULTS: Map<String, Long> = mapOf(
        INTERSTITIAL_EVERY_SPINS to 10L,
        INTERSTITIAL_EVERY_HOME to 2L,
        INTERSTITIAL_EVERY_LEVEL_UP to 2L,
        INTERSTITIAL_EVERY_BIG_WIN to 2L,
        INTERSTITIAL_EVERY_FREE_SPINS_WIN to 2L,
    )
}

/** Remote Config with defaults; fetches once at start-up (hourly minimum between fetches). */
class Remote(private val context: Context) {
    private val rc: FirebaseRemoteConfig? by lazy {
        if (!FirebaseHub.available(context)) null else runCatching {
            FirebaseRemoteConfig.getInstance().apply {
                setConfigSettingsAsync(FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600).build())
                setDefaultsAsync(RemoteKeys.DEFAULTS.mapValues { it.value as Any })
            }
        }.onFailure { Log.w(TAG, "Remote Config unavailable", it) }.getOrNull()
    }

    fun fetch() {
        rc?.fetchAndActivate()?.addOnCompleteListener { Log.d(TAG, "Remote Config fetched: ${it.isSuccessful}") }
    }

    fun long(key: String): Long = rc?.let { runCatching { it.getLong(key) }.getOrNull() } ?: RemoteKeys.DEFAULTS[key] ?: 0L
}

/** Firebase Analytics events; no-ops without Firebase. */
class Analytics(private val context: Context) {
    private val fa: FirebaseAnalytics? by lazy {
        if (!FirebaseHub.available(context)) null else runCatching { FirebaseAnalytics.getInstance(context) }.getOrNull()
    }

    fun log(event: String, vararg params: Pair<String, Any>) {
        val a = fa ?: return
        val b = Bundle()
        params.forEach { (k, v) ->
            when (v) {
                is Int -> b.putLong(k, v.toLong())
                is Long -> b.putLong(k, v)
                is Double -> b.putDouble(k, v)
                else -> b.putString(k, v.toString())
            }
        }
        a.logEvent(event, b)
    }

    fun screen(name: String) = log(FirebaseAnalytics.Event.SCREEN_VIEW, FirebaseAnalytics.Param.SCREEN_NAME to name)
}

/** Crashlytics breadcrumbs and non-fatal reports; no-ops without Firebase. */
class Crashes(private val context: Context) {
    private val c: FirebaseCrashlytics? by lazy {
        if (!FirebaseHub.available(context)) null else runCatching { FirebaseCrashlytics.getInstance() }.getOrNull()
    }

    fun breadcrumb(msg: String) { c?.log(msg) }
    fun report(t: Throwable) { c?.recordException(t) }
    fun key(k: String, v: String) { c?.setCustomKey(k, v) }
}
