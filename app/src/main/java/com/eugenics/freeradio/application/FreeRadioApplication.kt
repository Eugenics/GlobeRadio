package com.eugenics.freeradio.application

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.eugenics.freeradio.util.CustomWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FreeRadioApplication : Application() {

    @Inject
    lateinit var customWorkerFactory: CustomWorkerFactory

    override fun onCreate() {
        super.onCreate()
        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(customWorkerFactory)
                .build()
        )
    }
}