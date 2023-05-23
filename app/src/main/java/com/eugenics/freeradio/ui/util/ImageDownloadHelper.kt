package com.eugenics.freeradio.ui.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object ImageDownloadHelper {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15L, TimeUnit.SECONDS)
        .readTimeout(15L, TimeUnit.SECONDS)
        .build()

    fun downloadImageAsBitmap(imageUrl: String): Bitmap? {
        val request = Request.Builder()
            .url(imageUrl)
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                response.body?.let { body ->
                    return BitmapFactory.decodeByteArray(body.bytes(), 0, body.bytes().size)
                }
            } else {
                return null
            }
        }
        return null
    }
}