package com.eugenics.media_service.media

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import kotlinx.coroutines.flow.MutableStateFlow

class FreeRadioMediaServiceConnection(context: Context, serviceComponent: ComponentName) {
    val isConnected = MutableStateFlow(false)
    val networkFailure = MutableStateFlow(false)
    val playbackState = MutableStateFlow(EMPTY_PLAYBACK_STATE)
    val nowPlaying = MutableStateFlow(NOTHING_PLAYING)

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(
        context,
        serviceComponent,
        mediaBrowserConnectionCallback, null
    ).apply {
        connect()
    }

    private lateinit var mediaController: MediaControllerCompat

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }

    fun sendCommand(
        command: String,
        parameters: Bundle?,
        resultCallback: ((Int, Bundle?) -> Unit)
    ) = if (mediaBrowser.isConnected) {
        mediaController.sendCommand(
            command,
            parameters,
            object : ResultReceiver(Handler()) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                    resultCallback(resultCode, resultData)
                }
            })
        true
    } else {
        false
    }

    private inner class MediaBrowserConnectionCallback(private val context: Context) :
        MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            isConnected.value = true
        }

        override fun onConnectionSuspended() {
            isConnected.value = false
        }

        override fun onConnectionFailed() {
            isConnected.value = false
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            playbackState.value = state ?: EMPTY_PLAYBACK_STATE
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            nowPlaying.value = metadata ?: NOTHING_PLAYING
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }

    companion object {
        @Volatile
        private var instance: FreeRadioMediaServiceConnection? = null

        const val SET_FAVORITES_COMMAND = "setFavorites"
        const val SET_FAVORITES_STATION_KEY = "setFavoritesStationKey"
        const val SET_FAVORITES_VALUE_KEY = "setFavoritesValueKey"

        fun getInstance(context: Context, serviceComponent: ComponentName) =
            instance ?: synchronized(this) {
                instance ?: FreeRadioMediaServiceConnection(context, serviceComponent)
                    .also { instance = it }
            }
    }
}

@Suppress("PropertyName")
val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()

@Suppress("PropertyName")
val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()
