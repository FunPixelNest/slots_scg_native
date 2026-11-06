package com.funpixelnest.slots.audio

import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.funpixelnest.slots.data.PrefsPlayerStore

/** Strength of a vibration, from a soft button tick to a big-win celebration. */
enum class Haptic { Tick, Light, Medium, Heavy, Success, Celebrate }

/**
 * Game haptics (Settings > Vibration). Sound effects trigger the matching vibration through
 * [forSfx]; skin buttons tick on press through [Haptics.tap].
 */
class Haptics(context: Context, private val prefs: PrefsPlayerStore) {
    private val vibrator: Vibrator? = runCatching {
        if (Build.VERSION.SDK_INT >= 31) context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        else @Suppress("DEPRECATION") context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }.getOrNull()
    private var lastSoft = 0L

    init { current = this }

    fun play(h: Haptic) {
        if (!prefs.hapticsOn) return
        val v = vibrator?.takeIf { it.hasVibrator() } ?: return
        if (h <= Haptic.Light) {
            // a button press also plays its click sound: one tick, not two
            val now = SystemClock.uptimeMillis()
            if (now - lastSoft < 60) return
            lastSoft = now
        }
        runCatching {
            if (Build.VERSION.SDK_INT >= 26) v.vibrate(effect(h))
            else @Suppress("DEPRECATION") v.vibrate(LEGACY_MS.getValue(h))
        }
    }

    fun forSfx(s: Sfx) {
        when (s) {
            Sfx.Button -> play(Haptic.Tick)
            Sfx.ReelStop -> play(Haptic.Tick)
            Sfx.Choose -> play(Haptic.Light)
            Sfx.Spin -> play(Haptic.Medium)
            Sfx.Win, Sfx.WinCoins -> play(Haptic.Medium)
            Sfx.Bonus, Sfx.Buy -> play(Haptic.Success)
            Sfx.BigWin -> play(Haptic.Celebrate)
            Sfx.Lose -> Unit
        }
    }

    private fun effect(h: Haptic): VibrationEffect = when (h) {
        Haptic.Tick -> if (Build.VERSION.SDK_INT >= 29) VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK) else VibrationEffect.createOneShot(10, 90)
        Haptic.Light -> if (Build.VERSION.SDK_INT >= 29) VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK) else VibrationEffect.createOneShot(18, 140)
        Haptic.Medium -> VibrationEffect.createOneShot(30, 190)
        Haptic.Heavy -> if (Build.VERSION.SDK_INT >= 29) VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK) else VibrationEffect.createOneShot(45, 255)
        Haptic.Success -> VibrationEffect.createWaveform(longArrayOf(0, 30, 70, 45), intArrayOf(0, 170, 0, 255), -1)
        Haptic.Celebrate -> VibrationEffect.createWaveform(
            longArrayOf(0, 40, 70, 40, 70, 60, 90, 140), intArrayOf(0, 190, 0, 220, 0, 255, 0, 255), -1,
        )
    }

    companion object {
        private val LEGACY_MS = mapOf(
            Haptic.Tick to 10L, Haptic.Light to 18L, Haptic.Medium to 30L, Haptic.Heavy to 45L, Haptic.Success to 120L, Haptic.Celebrate to 300L,
        )

        /** Set when the app graph is built; null in previews. */
        @Volatile var current: Haptics? = null
            private set

        /** Soft tick for a UI button press. */
        fun tap() { current?.play(Haptic.Tick) }
    }
}
