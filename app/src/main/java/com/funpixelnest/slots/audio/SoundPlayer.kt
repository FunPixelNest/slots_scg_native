package com.funpixelnest.slots.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.funpixelnest.slots.data.PrefsPlayerStore

/** Sound effects and music converted from 0_Common/Audio (assets/common/audio/<name>.ogg). */
enum class Sfx(val file: String) {
    Button("button"), Spin("spin_sound"), ReelStop("pointer_hit"), Win("win_sound"),
    WinCoins("win_coins"), BigWin("big_win"), Lose("loose"), Bonus("bonus"), Buy("buy"), Choose("choose"),
}

class SoundPlayer(private val context: Context, private val prefs: PrefsPlayerStore, private val haptics: Haptics? = null) {
    private val pool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        ).build()
    private val ids = HashMap<Sfx, Int>()
    private var music: MediaPlayer? = null
    private var musicWanted = false

    init {
        Sfx.entries.forEach { s ->
            runCatching {
                context.assets.openFd("common/audio/${s.file}.ogg").use { ids[s] = pool.load(it, 1) }
            }
        }
    }

    fun play(s: Sfx, volume: Float = 1f) {
        haptics?.forSfx(s) // vibration follows the game events even with the sound off
        if (!prefs.soundOn) return
        ids[s]?.let { pool.play(it, volume, volume, 1, 0, 1f) }
    }

    fun startMusic() {
        musicWanted = true
        if (!prefs.musicOn || music != null) return
        music = runCatching {
            context.assets.openFd("common/audio/background.ogg").use { fd ->
                MediaPlayer().apply {
                    setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
                    isLooping = true
                    setVolume(0.35f, 0.35f)
                    prepare()
                    start()
                }
            }
        }.getOrNull()
    }

    fun stopMusic(keepWanted: Boolean = false) {
        musicWanted = keepWanted
        music?.run { runCatching { stop() }; release() }
        music = null
    }

    fun setSound(on: Boolean) { prefs.soundOn = on }

    fun setMusic(on: Boolean) {
        prefs.musicOn = on
        if (on && musicWanted) startMusic() else stopMusic(keepWanted = musicWanted)
    }

    fun onPause() = stopMusic(keepWanted = musicWanted)
    fun onResume() { if (musicWanted) startMusic() }
}
