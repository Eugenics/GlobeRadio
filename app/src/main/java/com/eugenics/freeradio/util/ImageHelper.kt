package com.eugenics.freeradio.util

import android.util.Log
import com.eugenics.freeradio.ui.viewmodels.MainViewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

object ImageHelper {

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    fun downloadAsync(imageUrl: String, callback: (response: Response) -> Unit) {
        val request = Request.Builder()
            .url(imageUrl)
            .build()
        httpClient.newCall(request).enqueue(onImageDownloadCallback(callback))
    }

    private fun onImageDownloadCallback(onSuccess: (response: Response) -> Unit): Callback =
        object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(MainViewModel.TAG, e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    onSuccess(response)
                }
            }
        }
}