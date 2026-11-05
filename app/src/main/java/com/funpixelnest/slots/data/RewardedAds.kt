package com.funpixelnest.slots.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Rewarded video ads (watch a video -> unlock a machine or get free coins).
 *
 * No ad network is integrated yet, so [PlaceholderRewardedAds] plays a short in-app
 * "video" with a countdown and then grants the reward. Swap it for an AdMob / LevelPlay
 * rewarded-ad implementation of this interface when the ad SDK is added.
 */
interface RewardedAds {
    /** Shows a rewarded video; [onReward] runs only if the user watched to the end. */
    fun show(placement: String, onReward: () -> Unit)
}

class PlaceholderRewardedAds : RewardedAds {
    /** The video currently on screen (drawn by RewardedVideoOverlay), or null. */
    var current by mutableStateOf<Request?>(null)
        private set

    data class Request(val placement: String, val onReward: () -> Unit)

    override fun show(placement: String, onReward: () -> Unit) {
        current = Request(placement, onReward)
    }

    fun finish(rewarded: Boolean) {
        val r = current ?: return
        current = null
        if (rewarded) r.onReward()
    }

    companion object {
        const val SECONDS = 5
    }
}
