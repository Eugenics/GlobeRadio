package com.eugenics.media_service.media

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.eugenics.media_service.R
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

const val NOW_PLAYING_CHANNEL_ID = "com.eugenics.media_service.media.free_radio"
const val NOW_PLAYING_NOTIFICATION_ID = 0xb500

private const val NOTIFICATION_LARGE_ICON_SIZE = 250 // px
private const val NO_TITLE = "No title"

internal class FreeRadioNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener
) {

    var notificationState: Int = NOTIFICATION_IS_HIDE
        private set

    private var player: Player? = null
    private val notificationManager: PlayerNotificationManager =
        PlayerNotificationManager.Builder(
            context,
            NOW_PLAYING_NOTIFICATION_ID,
            NOW_PLAYING_CHANNEL_ID
        )
            .setMediaDescriptionAdapter(DescriptionAdapter())
            .setNotificationListener(notificationListener)
            .setChannelNameResourceId(R.string.notification_channel_name)
            .build()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, throwable.message.toString())
    }

    init {
        notificationManager.setMediaSessionToken(sessionToken)
        notificationManager.setSmallIcon(R.drawable.ic_notification)
        notificationManager.setUseRewindAction(false)
        notificationManager.setUseNextActionInCompactView(true)
        notificationManager.setUsePreviousActionInCompactView(true)
        notificationManager.setUseChronometer(true)
    }

    fun hideNotification() {
        notificationManager.setPlayer(null)
        this.player = null
        notificationState = NOTIFICATION_IS_HIDE
    }

    fun showNotificationForPlayer(player: Player) {
        this.player = player
        notificationManager.setPlayer(player)
        notificationState = NOTIFICATION_IS_SHOW
    }

    private inner class DescriptionAdapter : PlayerNotificationManager.MediaDescriptionAdapter {

        var currentIconUri: Uri? = null
        var currentBitmap: Bitmap? =
            ContextCompat.getDrawable(context, R.drawable.pradio_wave)?.toBitmap()

        override fun getCurrentSubText(player: Player): CharSequence = NO_TITLE

        override fun createCurrentContentIntent(player: Player): PendingIntent? = null

        override fun getCurrentContentText(player: Player) =
            player.mediaMetadata.title ?: ""

        override fun getCurrentContentTitle(player: Player) =
            player.mediaMetadata.displayTitle ?: ""

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            val iconUri = player.currentMediaItem?.mediaMetadata?.artworkUri
            return when {
                iconUri.toString().isBlank() ->
                    ContextCompat.getDrawable(context, R.drawable.pradio_wave)?.toBitmap()

                iconUri != currentIconUri && iconUri != null -> {
                    currentIconUri = iconUri
                    runBlocking(coroutineExceptionHandler) {
                        currentBitmap = resolveUriAsBitmap(iconUri)
                        currentBitmap?.let { callback.onBitmap(it) }
                    }
                    currentBitmap
                }

                else -> currentBitmap
            }
        }

        private suspend fun resolveUriAsBitmap(uri: Uri): Bitmap? {
            return withContext(Dispatchers.IO + coroutineExceptionHandler) {
                var bitmap = ContextCompat.getDrawable(context, R.drawable.pradio_wave)?.toBitmap()
                try {
                    bitmap = Glide.with(context)
                        .applyDefaultRequestOptions(glideOptions)
                        .asBitmap()
                        .load(uri)
                        .submit(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE)
                        .get()
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
                bitmap
            }
        }
    }

    companion object {
        private const val TAG = "FreeRadioNotificationManager"

        const val NOTIFICATION_IS_SHOW = 1
        const val NOTIFICATION_IS_HIDE = 0
    }
}

private val glideOptions = RequestOptions()
    .fallback(R.drawable.pradio_wave)
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .timeout(15000)
