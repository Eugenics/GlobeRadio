package com.eugenics.freeradio.ui.util

import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request

class ImageDownloadHelper(private val httpClient: OkHttpClient) {

    fun downloadAsync(imageUrl: String, callback: Callback) {
        val request = Request.Builder()
            .url(imageUrl)
            .build()
        httpClient.newCall(request).enqueue(callback)
    }
}