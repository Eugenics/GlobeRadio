package com.eugenics.media_service.application

import android.app.Application
import android.content.Context

class MediaServiceApplication : Application() {
    private lateinit var appContext: Context

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    fun getContext() = appContext
}