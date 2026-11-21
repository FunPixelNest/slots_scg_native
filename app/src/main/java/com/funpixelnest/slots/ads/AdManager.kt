package com.funpixelnest.slots.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.funpixelnest.slots.BuildConfig
import com.funpixelnest.slots.data.PlaceholderRewardedAds
import com.funpixelnest.slots.data.RewardedAds
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.lang.ref.WeakReference

private const val TAG = "SlotsAds"

/**
 * AdMob (with AppLovin, Unity, Mintegral and Liftoff mediation configured in the AdMob console):
 * banner, interstitial and rewarded. One interstitial and one rewarded ad are kept loaded.
 *
 * If a rewarded video is not ready (offline, no fill) the in-app placeholder video plays so the
 * player still gets the reward; if an interstitial is not ready the game simply carries on.
 */
class AdManager(
    private val context: Context,
    private val fallback: PlaceholderRewardedAds,
    private val analytics: Analytics,
) : RewardedAds {
    private var activity = WeakReference<Activity>(null)
    private var interstitial: InterstitialAd? = null
    private var rewarded: RewardedAd? = null
    private var loadingInterstitial = false
    private var loadingRewarded = false
    /** A full-screen ad is on screen; no second one starts. */
    var showing = false
        private set
    var started = false
        private set

    /** Start the SDK (and every mediated network) once; safe to call repeatedly. */
    fun start(activity: Activity) {
        this.activity = WeakReference(activity)
        if (started || isRobolectric) return
        started = true
        MobileAds.initialize(context) { status ->
            status.adapterStatusMap.forEach { (name, s) -> Log.d(TAG, "adapter $name: ${s.initializationState} ${s.description}") }
            loadInterstitial(); loadRewarded()
        }
    }

    fun attach(activity: Activity) { this.activity = WeakReference(activity) }

    // ------------------------------------------------------------------ interstitial

    private fun loadInterstitial() {
        if (!started || loadingInterstitial || interstitial != null) return
        loadingInterstitial = true
        InterstitialAd.load(context, BuildConfig.AD_INTERSTITIAL, AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) { interstitial = ad; loadingInterstitial = false }
            override fun onAdFailedToLoad(e: LoadAdError) {
                Log.d(TAG, "interstitial failed: ${e.message}"); interstitial = null; loadingInterstitial = false
            }
        })
    }

    /** Shows an interstitial if one is ready; [onDone] always runs (right away when none is shown). */
    fun showInterstitial(placement: String, onDone: () -> Unit = {}) {
        val ad = interstitial
        val act = activity.get()
        if (ad == null || act == null || showing) { loadInterstitial(); onDone(); return }
        interstitial = null
        showing = true
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() { showing = false; loadInterstitial(); onDone() }
            override fun onAdFailedToShowFullScreenContent(e: AdError) { showing = false; loadInterstitial(); onDone() }
            override fun onAdShowedFullScreenContent() { analytics.log("ad_interstitial_shown", "placement" to placement) }
        }
        ad.show(act)
    }

    // ------------------------------------------------------------------ rewarded

    private fun loadRewarded() {
        if (!started || loadingRewarded || rewarded != null) return
        loadingRewarded = true
        RewardedAd.load(context, BuildConfig.AD_REWARDED, AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) { rewarded = ad; loadingRewarded = false }
            override fun onAdFailedToLoad(e: LoadAdError) {
                Log.d(TAG, "rewarded failed: ${e.message}"); rewarded = null; loadingRewarded = false
            }
        })
    }

    override fun show(placement: String, onReward: () -> Unit) {
        val ad = rewarded
        val act = activity.get()
        if (ad == null || act == null || showing) {
            loadRewarded()
            fallback.show(placement, onReward)
            return
        }
        rewarded = null
        showing = true
        var earned = false
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                showing = false; loadRewarded()
                if (earned) onReward()
            }
            override fun onAdFailedToShowFullScreenContent(e: AdError) { showing = false; loadRewarded(); fallback.show(placement, onReward) }
            override fun onAdShowedFullScreenContent() { analytics.log("ad_rewarded_shown", "placement" to placement) }
        }
        ad.show(act) { earned = true }
    }
}

/**
 * When interstitials appear (all numbers from Remote Config, 0 turns a trigger off):
 * every N paid spins, and every Nth home tap / level-up close / big win close / free spins win close.
 * Counters live for the app session.
 */
class InterstitialRules(private val ads: AdManager, private val remote: Remote) {
    private val counts = HashMap<String, Long>()

    private fun hit(key: String): Boolean {
        val every = remote.long(key)
        if (every <= 0) return false
        val n = (counts[key] ?: 0L) + 1
        counts[key] = n
        return n % every == 0L
    }

    /** Before a paid spin: shows the ad first when due, then runs [spin]. */
    fun onSpin(spin: () -> Unit) {
        if (hit(RemoteKeys.INTERSTITIAL_EVERY_SPINS)) ads.showInterstitial("spins", spin) else spin()
    }

    fun onHome() { if (hit(RemoteKeys.INTERSTITIAL_EVERY_HOME)) ads.showInterstitial("home") }

    /** [watchedVideo]: the player just watched a rewarded video, so no interstitial right after (still counted). */
    fun onLevelUpClose(watchedVideo: Boolean = false) { if (hit(RemoteKeys.INTERSTITIAL_EVERY_LEVEL_UP) && !watchedVideo) ads.showInterstitial("level_up") }

    fun onBigWinClose() { if (hit(RemoteKeys.INTERSTITIAL_EVERY_BIG_WIN)) ads.showInterstitial("big_win") }

    fun onFreeSpinsWinClose() { if (hit(RemoteKeys.INTERSTITIAL_EVERY_FREE_SPINS_WIN)) ads.showInterstitial("free_spins_win") }
}
