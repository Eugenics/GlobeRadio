package com.eugenics.freeradio.util

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.zip.ZipInputStream
import kotlin.io.path.Path

private const val TAG = "IOHelper"
private const val PARSE_JSON_TAG = "PARSE_JSON"
private const val COPY_ASSET_TO_CACHE_TAG = "COPY ASSET TO CACHE"

object IOHelper {
    fun <T> parseJsonToList(filePath: String, tClass: Class<T>): List<T> =
        try {
            val jsonFile = File(filePath)
            val typeFactory = TypeFactory.defaultInstance()
            val objectMapper = ObjectMapper().apply {
                configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
            if (jsonFile.exists()) {
                objectMapper.readValue(
                    jsonFile,
                    typeFactory.constructCollectionType(ArrayList::class.java, tClass)
                )
            } else listOf()
        } catch (e: Exception) {
            Log.e(PARSE_JSON_TAG, e.message.toString())
            listOf()
        }

    fun unzipStationsFullList(zipFrom: String, pathTo: String): Boolean =
        try {
            val zipStream = ZipInputStream(FileInputStream(zipFrom))

            if (zipStream.nextEntry != null) {
                val zipBytes = zipStream.readBytes()
                if (zipBytes.isNotEmpty()) {
                    Files.deleteIfExists(Path(pathTo))
                    val outputFile = FileOutputStream(pathTo)
                    outputFile.write(zipBytes)
                    outputFile.close()
                }
            }

            zipStream.closeEntry()
            zipStream.close()
            true
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            false
        }

    fun copyAssetToCache(assetName: String, context: Context): Boolean =
        try {
            val cacheOutputStream =
                FileOutputStream("${context.cacheDir.absolutePath}/$assetName")
            val assetInputStream = context.assets.open(assetName)

            assetInputStream.copyTo(cacheOutputStream)

            assetInputStream.close()
            cacheOutputStream.close()
            true
        } catch (e: Exception) {
            Log.e(COPY_ASSET_TO_CACHE_TAG, e.toString())
            false
        }
}