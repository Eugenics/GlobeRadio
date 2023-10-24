package com.eugenics.freeradio.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.eugenics.data.data.util.Response
import com.eugenics.data.interfaces.IStationsRepository
import com.eugenics.freeradio.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TAG = "StationsWorker"
private const val NOTIFICATION_GROUP_KEY = "com.eugenics.freeradio.notification"
private const val NOTIFICATION_ID = 15785
private const val DELAY_TIME = 10_000L
private const val LOADING_MESSAGE = "Loading..."
private const val SUCCESS_MESSAGE = "Success..."
private const val NOTIFICATION_CHANNEL_NAME = "WORK_MANAGER_NOTIFICATION"

class StationsWorker(
    context: Context,
    parameters: WorkerParameters,
    private val stationsRepository: IStationsRepository
) : CoroutineWorker(context, parameters) {

    private val notificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    override suspend fun doWork(): Result {
        Log.d(TAG, "Work start...")
        delay(DELAY_TIME)
        Log.d(TAG, "Delay end...")

        setForeground(
            createForegroundInfo(
                applicationContext.getString(R.string.start_update),
                true
            )
        )


        updateStations()
        Log.d(TAG, "Work end...")

        setForeground(
            createForegroundInfo(
                applicationContext.getString(R.string.updated),
                false
            )
        )
        delay(DELAY_TIME)
        return Result.success()
    }

    private suspend fun updateStations() {
        withContext(Dispatchers.IO) {
            stationsRepository.getRemoteStations().collect { response ->
                when (response) {
                    is Response.Loading -> Log.d(TAG, LOADING_MESSAGE)

                    is Response.Error -> {
                        Log.e(TAG, response.message)
                        this.cancel(response.message)
                    }

                    is Response.Success -> {
                        Log.d(TAG, SUCCESS_MESSAGE)
                        response.data?.let { stations ->
                            Log.d(TAG, "Save to data base...")
                            stationsRepository.reloadStations(stations = stations)
                            Log.d(TAG, "Saved to data base...")
                        }
                        this.cancel(SUCCESS_MESSAGE)
                    }
                }
            }
        }
    }

    private fun createForegroundInfo(
        message: String,
        isCanceled: Boolean = true
    ): ForegroundInfo {
        val channelId = applicationContext.getString(R.string.notification_channel_id)
        val notificationTitle = applicationContext.getString(R.string.workmanager_title)
        val cancelTitle = applicationContext.getString(R.string.cancel_string)
        val cancellationIntent =
            WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
        val lageIcon =
            BitmapFactory.decodeResource(applicationContext.resources, R.drawable.globe_logo)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(notificationTitle)
            .setContentText(message)
            .setLargeIcon(lageIcon)
            .setSmallIcon(R.drawable.ic_info)
            .setGroup(NOTIFICATION_GROUP_KEY)
            .also {
                if (isCanceled)
                    it.addAction(
                        R.drawable.cancel_24,
                        cancelTitle, cancellationIntent
                    )
            }
            .build()

        createNotificationChannel()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channelId = applicationContext.getString(R.string.notification_channel_id)
        val channelName = NOTIFICATION_CHANNEL_NAME
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }
}