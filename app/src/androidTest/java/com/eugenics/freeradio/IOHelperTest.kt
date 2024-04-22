package com.eugenics.freeradio

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.eugenics.freeradio.util.IOHelper
import com.eugenics.freeradio.util.data.StationPojo
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.nio.file.Files
import kotlin.io.path.Path

private const val STATIONS_JSON_NAME = "full.json"
private const val TAGS_NAME = "station_tags.json"
private const val STATIONS_ZIP_FILE_NAME = "full.zip"

class IOHelperTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val zipCachePath = "${context.cacheDir.absolutePath}\\$STATIONS_ZIP_FILE_NAME"

    @Before
    fun setup() {
        IOHelper.copyAssetToCache(STATIONS_ZIP_FILE_NAME, context)
        IOHelper.copyAssetToCache(TAGS_NAME, context)
    }

    @Test
    fun testUnzipStationsFullList() {
        val unzipFilePath = Path("${context.cacheDir}/$STATIONS_JSON_NAME")
        val pathTo = "${context.cacheDir.absolutePath}/$STATIONS_JSON_NAME"

        val result = IOHelper.unzipStationsFullList(
            zipFrom = zipCachePath,
            pathTo = pathTo
        )

        Assert.assertEquals(Files.exists(unzipFilePath), true)
        Assert.assertEquals(result, true)
    }

    @Test
    fun testParseJsonToList() = runTest {
        val tag = "PARSE_JSON_TEST"
        val jsonFilePath = "${context.cacheDir.absolutePath}/$STATIONS_JSON_NAME"
        val result = mutableListOf<StationPojo>()

        IOHelper.unzipStationsFullList(
            zipFrom = zipCachePath,
            pathTo = jsonFilePath
        )
        result.addAll(IOHelper.parseJsonToList(jsonFilePath, StationPojo::class.java))

        Log.d(tag, "List size: ${result.size}")
        Assert.assertEquals(true, result.isNotEmpty())
    }
}