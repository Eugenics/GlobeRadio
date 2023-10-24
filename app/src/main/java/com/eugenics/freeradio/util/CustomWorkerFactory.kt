package com.eugenics.freeradio.util

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.eugenics.data.interfaces.IStationsRepository

class CustomWorkerFactory(private val repository: IStationsRepository) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker =
        StationsWorker(
            appContext,
            workerParameters,
            repository
        )
}