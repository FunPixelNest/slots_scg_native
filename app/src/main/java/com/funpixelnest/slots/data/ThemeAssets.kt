package com.funpixelnest.slots.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.funpixelnest.slots.engine.ThemeConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

/** Sprite metadata from the Unity .meta files (sprites.json). */
@Serializable
data class SpriteMeta(
    val w: Int = 0,
    val h: Int = 0,
    /** 9-slice border: left, top, right, bottom (px). */
    val border: List<Float>? = null,
    /** Sprite-sheet frames: x, y, w, h (px, y from the top). */
    val frames: List<List<Int>>? = null,
)

/** One animation frame: an image, optionally a sub-rectangle of a sprite sheet. */
data class Frame(val image: String, val offset: IntOffset = IntOffset.Zero, val size: IntSize? = null)

/** Shared bitmap cache, sized to a share of the app heap. */
object ImageCache {
    private val cache = object : LruCache<String, ImageBitmap>(
        (Runtime.getRuntime().maxMemory() / 4).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    ) {
        override fun sizeOf(key: String, value: ImageBitmap) = value.width * value.height * 4
    }

    fun get(key: String): ImageBitmap? = cache.get(key)
    fun put(key: String, value: ImageBitmap) { cache.put(key, value) }
    fun clear() = cache.evictAll()
}

/**
 * Everything one theme needs at runtime: its slot config plus access to its converted art.
 * Image keys are paths relative to the theme folder without the ".webp" extension.
 */
class ThemeAssets(val config: ThemeConfig, private val source: AssetSource, private val sprites: Map<String, SpriteMeta>) {

    private val prefix = "theme:${config.id}:"

    fun meta(key: String): SpriteMeta? = sprites[key]

    fun hasImage(key: String?): Boolean = key != null && source.exists("$key.webp")

    fun cached(key: String): ImageBitmap? = ImageCache.get(prefix + key)

    /** Decode (or fetch from cache). Safe to call from any thread. */
    fun loadBlocking(key: String): ImageBitmap? {
        ImageCache.get(prefix + key)?.let { return it }
        return try {
            val opts = BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 }
            val bmp = source.open("$key.webp").use { BitmapFactory.decodeStream(it, null, opts) } ?: return null
            bmp.asImageBitmap().also { ImageCache.put(prefix + key, it) }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun load(key: String): ImageBitmap? = cached(key) ?: withContext(Dispatchers.IO) { loadBlocking(key) }

    /** Frames of a symbol's win animation (sprite sheet or frame sequence), or empty. */
    fun symbolFrames(symbolId: Int): List<Frame> {
        val anim = config.symbols.getOrNull(symbolId)?.anim ?: return emptyList()
        anim.sheet?.let { sheet ->
            val rects = sprites[sheet]?.frames.orEmpty()
            return rects.map { r -> Frame(sheet, IntOffset(r[0], r[1]), IntSize(r[2], r[3])) }
        }
        return anim.frames.map { Frame(it) }
    }

    /** Warm the cache with everything the game screen draws first. */
    suspend fun preload(onProgress: (Float) -> Unit = {}) = withContext(Dispatchers.IO) {
        val keys = buildList {
            config.background?.let(::add)
            config.frame?.let(::add)
            config.reelBackground?.let(::add)
            config.symbols.forEach { s -> s.image?.let(::add); s.blur?.let(::add) }
            addAll(config.ui.values)
        }.distinct()
        keys.forEachIndexed { i, k ->
            loadBlocking(k)
            onProgress((i + 1f) / keys.size)
        }
    }

    companion object {
        private val spritesSerializer = MapSerializer(String.serializer(), SpriteMeta.serializer())

        fun open(source: AssetSource): ThemeAssets {
            val config = ThemeConfig.parse(source.readText("theme.json"))
            val sprites = if (source.exists("sprites.json"))
                ThemeConfig.json.decodeFromString(spritesSerializer, source.readText("sprites.json")) else emptyMap()
            return ThemeAssets(config, source, sprites)
        }
    }
}
