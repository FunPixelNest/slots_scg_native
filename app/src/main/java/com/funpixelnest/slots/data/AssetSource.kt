package com.funpixelnest.slots.data

import android.content.res.AssetManager
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

/** Read-only file tree: the APK assets or an unpacked Play asset pack. */
interface AssetSource {
    fun open(path: String): InputStream
    fun exists(path: String): Boolean
    fun readText(path: String): String = open(path).use { it.readBytes().decodeToString() }
}

class ApkAssetSource(private val assets: AssetManager, private val root: String) : AssetSource {
    override fun open(path: String): InputStream = assets.open("$root/$path")
    override fun exists(path: String): Boolean = try {
        assets.open("$root/$path").close(); true
    } catch (_: IOException) {
        false
    }
}

class FileAssetSource(private val root: File) : AssetSource {
    override fun open(path: String): InputStream = FileInputStream(File(root, path))
    override fun exists(path: String): Boolean = File(root, path).isFile
}
