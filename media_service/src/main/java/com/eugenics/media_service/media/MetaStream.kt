package com.eugenics.media_service.media

import android.util.Log
import com.eugenics.media_service.domain.model.StreamTitle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.StringBuilder
import kotlin.Exception

class MetaStream : Runnable {
    private val _streamTitle: MutableStateFlow<StreamTitle> = MutableStateFlow(StreamTitle())
    val streamTitle: StateFlow<StreamTitle> = _streamTitle

    private val client = OkHttpClient()
    private var streamUrl: String = ""

    private fun run(url: String) {
        val request = Request.Builder()
            .url(url)
            .addHeader(name = "Icy-MetaData", value = "1")
            .addHeader(name = "User-Agent", value = "FreeRadioMeta_v.0")
            .build()

        val response = client.newCall(request = request).execute()
        val icyMetaInt = response.headers["icy-metaint"]
        val metaInt = icyMetaInt?.toInt() ?: 0
        val inputStream = response.body?.byteStream()
        val string = StringBuilder()

        try {
            if (icyMetaInt != null) {
                inputStream?.let {
                    var byte = it.read()
                    var readCnt = 0
                    var metaBufSize = 0

                    while (byte != -1) {
                        readCnt++
                        if (readCnt == metaInt + 1) {
                            metaBufSize = byte.shl(4)
                            byte = it.read()
                        }
                        if (metaBufSize > 0 && readCnt < (metaInt + metaBufSize)) {
                            string.append(byte.toChar())
                        } else {
                            if (readCnt > metaInt + metaBufSize) break
                        }

                        byte = it.read()
                    }
                    val parseData = parseMetaData(
                        metaData = String(string.toString().toByteArray(Charsets.ISO_8859_1))
                    )
                    if (streamTitle.value != parseData) {
                        _streamTitle.value = parseData
                    }
                }
                inputStream?.close()
            } else {
                Log.d(TAG, "No ICY metaint...")
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            inputStream?.close()
        }
    }

    private fun parseMetaData(metaData: String): StreamTitle =
        try {
            val streamTitle = metaData.split(";")[0]
            if (streamTitle.split("=").size > 1) {
                val clearTitle = streamTitle.split("=")[1]
                if (clearTitle.isNotBlank()) {
                    val artist = clearTitle.split("-")[0].replace(
                        oldChar = '\'',
                        newChar = ' '
                    ).trim()
                    val title = clearTitle.split("-")[1].replace(
                        oldChar = '\'',
                        newChar = ' '
                    ).trim()
                    StreamTitle(
                        artist = artist,
                        title = title
                    )
                } else {
                    StreamTitle(title = streamTitle)
                }
            } else {
                StreamTitle(title = streamTitle)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            StreamTitle()
        }

    override fun run() {
        if (streamUrl.isNotBlank()) {
            run(url = streamUrl)
        }
    }

    fun setStreamUrl(streamUrl: String) {
        this.streamUrl = streamUrl
    }

    companion object {
        const val TAG = "MetaStream"
    }
}