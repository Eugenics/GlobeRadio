/*
 * Copyright 2020 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eugenics.media_service.media

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.content.res.ResourcesCompat
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

/**
 * A wrapper class for ExoPlayer's PlayerNotificationManager. It sets up the notification shown to
 * the user during audio playback and provides track metadata, such as track title and icon image.
 */
internal class FreeRadioNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener
) {

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
    }

    fun showNotificationForPlayer(player: Player) {
        notificationManager.setPlayer(player)
    }

    private inner class DescriptionAdapter(private val controller: MediaControllerCompat) :
        PlayerNotificationManager.MediaDescriptionAdapter {

        var currentIconUri: Uri? = null
        var currentBitmap: Bitmap? = null

        override fun createCurrentContentIntent(player: Player): PendingIntent? =
            controller.sessionActivity

        override fun getCurrentContentText(player: Player) =
            controller.metadata.description.subtitle

        override fun getCurrentContentTitle(player: Player) =
            player.currentMediaItem?.mediaMetadata?.title ?: "No title"


        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            Log.d(TAG, player.currentMediaItem?.mediaMetadata?.artworkUri.toString().ifEmpty { "No string..." })
            val iconUri = player.currentMediaItem?.mediaMetadata?.artworkUri
            return when {
                iconUri == null -> {
                    ResourcesCompat.getDrawable(
                        Resources.getSystem(),
                        R.drawable.ic_music_note,
                        null
                    )?.toBitmap() ?: currentBitmap
                }
                (iconUri != currentIconUri) || currentBitmap == null -> {
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
                // Block on downloading artwork.
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

    companion object {
        const val TAG = "FreeRadioNotificationManager"
    }
}

const val NOTIFICATION_LARGE_ICON_SIZE = 144 // px

private val glideOptions = RequestOptions()
    .fallback(R.drawable.ic_music_note)
    .diskCacheStrategy(DiskCacheStrategy.DATA)
