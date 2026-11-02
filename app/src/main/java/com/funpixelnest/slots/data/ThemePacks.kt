package com.funpixelnest.slots.data

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.funpixelnest.slots.engine.ThemeIndexEntry
import com.google.android.play.core.assetpacks.AssetPackManager
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackState
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.File

sealed interface PackState {
    data object Ready : PackState
    data object NotDownloaded : PackState
    data class Downloading(val progress: Float) : PackState
    data object WaitingForWifi : PackState
    data class Failed(val code: Int) : PackState
}

/**
 * Locates theme art. Debug APKs carry every theme inside the APK; release bundles get them
 * from Google Play Asset Delivery (one "fast-follow" pack per theme, see themes/theme_<id>).
 */
class ThemePacks(private val context: Context) {
    private val manager: AssetPackManager = AssetPackManagerFactory.getInstance(context)
    private val _states = MutableStateFlow<Map<String, PackState>>(emptyMap())
    val states: StateFlow<Map<String, PackState>> = _states

    private val listener = AssetPackStateUpdateListener { s -> publish(s) }

    init {
        manager.registerListener(listener)
    }

    private fun bundled(themeId: String): Boolean =
        ApkAssetSource(context.assets, "themes/$themeId").exists("theme.json")

    private fun packDir(entry: ThemeIndexEntry): File? =
        manager.getPackLocation(entry.pack)?.assetsPath()?.let { File(it, "themes/${entry.id}") }
            ?.takeIf { File(it, "theme.json").isFile }

    fun source(entry: ThemeIndexEntry): AssetSource? = when {
        bundled(entry.id) -> ApkAssetSource(context.assets, "themes/${entry.id}")
        else -> packDir(entry)?.let { FileAssetSource(it) }
    }

    /** Fill [states] for every theme; asks Play for the ones not on the device yet. */
    fun refresh(entries: List<ThemeIndexEntry>) {
        val missing = ArrayList<String>()
        _states.update { old ->
            old + entries.associate { e ->
                e.pack to if (source(e) != null) PackState.Ready else {
                    missing += e.pack
                    old[e.pack] ?: PackState.NotDownloaded
                }
            }
        }
        if (missing.isEmpty()) return
        manager.getPackStates(missing).addOnSuccessListener { states ->
            states.packStates().values.forEach(::publish)
        }.addOnFailureListener { Log.w(TAG, "pack states unavailable", it) }
    }

    fun download(entry: ThemeIndexEntry) {
        _states.update { it + (entry.pack to PackState.Downloading(0f)) }
        manager.fetch(listOf(entry.pack))
            .addOnSuccessListener { it.packStates().values.forEach(::publish) }
            .addOnFailureListener {
                Log.w(TAG, "fetch ${entry.pack} failed", it)
                _states.update { s -> s + (entry.pack to PackState.Failed(-1)) }
            }
    }

    /** Play asks the user before a large download on mobile data. */
    fun confirmCellular(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        manager.showConfirmationDialog(launcher)
    }

    private fun publish(s: AssetPackState) {
        val state = when (s.status()) {
            AssetPackStatus.COMPLETED -> PackState.Ready
            AssetPackStatus.PENDING, AssetPackStatus.DOWNLOADING, AssetPackStatus.TRANSFERRING -> {
                val total = s.totalBytesToDownload().coerceAtLeast(1)
                PackState.Downloading((s.bytesDownloaded().toFloat() / total).coerceIn(0f, 1f))
            }
            AssetPackStatus.WAITING_FOR_WIFI, AssetPackStatus.REQUIRES_USER_CONFIRMATION -> PackState.WaitingForWifi
            AssetPackStatus.FAILED -> PackState.Failed(s.errorCode())
            else -> PackState.NotDownloaded
        }
        _states.update { it + (s.name() to state) }
    }

    fun close() = manager.unregisterListener(listener)

    companion object {
        private const val TAG = "ThemePacks"
    }
}
