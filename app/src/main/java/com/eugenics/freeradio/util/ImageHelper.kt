package com.eugenics.freeradio.util

import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object ImageHelper {

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    fun downloadAsync(imageUrl: String, callback: Callback) {
        val request = Request.Builder()
            .url(imageUrl)
            .build()
        httpClient.newCall(request).enqueue(callback)
    }
}