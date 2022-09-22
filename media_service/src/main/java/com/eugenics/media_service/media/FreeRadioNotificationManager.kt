package com.eugenics.media_service.media

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val NOW_PLAYING_CHANNEL_ID = "com.eugenics.media_service.media.NOW_PLAYING"
const val NOW_PLAYING_NOTIFICATION_ID = 0xb339 // Arbitrary number used to identify our notification

internal class FreeRadioNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener
) {

    private var notificationState: Int = NOTIFICATION_IS_HIDE

    private var player: Player? = null
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private val notificationManager: PlayerNotificationManager
    private val platformNotificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)

        val builder = PlayerNotificationManager.Builder(
            context,
            NOW_PLAYING_NOTIFICATION_ID,
            NOW_PLAYING_CHANNEL_ID
        )
        with(builder) {
            setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
            setNotificationListener(notificationListener)
            setChannelNameResourceId(R.string.notification_channel)
            setChannelDescriptionResourceId(R.string.notification_channel_description)
        }
        notificationManager = builder.build()
        notificationManager.setMediaSessionToken(sessionToken)
        notificationManager.setSmallIcon(R.drawable.ic_notification)
        notificationManager.setUseRewindAction(false)
        notificationManager.setUseNextActionInCompactView(true)
        notificationManager.setUsePreviousActionInCompactView(true)
        notificationManager.setUseChronometer(true)
        notificationManager.setUseStopAction(true)
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

    private inner class DescriptionAdapter(private val controller: MediaControllerCompat) :
        PlayerNotificationManager.MediaDescriptionAdapter {

        var currentIconUri: Uri? = null
        var currentBitmap: Bitmap? =
            ContextCompat.getDrawable(context, R.drawable.pradio_wave)?.toBitmap()

        override fun getCurrentSubText(player: Player): CharSequence? {
            return null
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? =
            controller.sessionActivity

        override fun getCurrentContentText(player: Player) =
            controller.metadata.description.subtitle

        override fun getCurrentContentTitle(player: Player) =
            player.currentMediaItem?.mediaMetadata?.title ?: NO_TITLE


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
                    serviceScope.launch {
                        currentBitmap = resolveUriAsBitmap(iconUri)
                        currentBitmap?.let { callback.onBitmap(it) }
                    }
                    null
                }
                else -> currentBitmap
            }
        }

        private suspend fun resolveUriAsBitmap(uri: Uri): Bitmap? {
            return withContext(Dispatchers.IO) {
                try {
                    Glide.with(context).applyDefaultRequestOptions(glideOptions)
                        .asBitmap()
                        .load(uri)
                        .submit(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE)
                        .get()
                } catch (ex: Exception) {
                    Log.e(TAG, ex.message.toString())
                    null
                }
            }
        }
    }

    fun getNotificationState(): Int = notificationState

    companion object {
        const val TAG = "FreeRadioNotificationManager"
        private const val NO_TITLE = "No title"

        const val NOTIFICATION_IS_SHOW = 1
        const val NOTIFICATION_IS_HIDE = 0
    }
}

const val NOTIFICATION_LARGE_ICON_SIZE = 144 // px

private val glideOptions = RequestOptions()
    .fallback(R.drawable.pradio_wave)
    .diskCacheStrategy(DiskCacheStrategy.DATA)
