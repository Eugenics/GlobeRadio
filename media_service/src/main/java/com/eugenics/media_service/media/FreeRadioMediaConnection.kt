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
import android.util.Log
import com.eugenics.core.enums.MediaSourceState
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

class FreeRadioMediaServiceConnection(context: Context, serviceComponent: ComponentName) {
    val isConnected = MutableStateFlow(false)
    val playbackState = MutableStateFlow(EMPTY_PLAYBACK_STATE)
    val mediaSourceState: MutableStateFlow<Int> = MutableStateFlow(MediaSourceState.STATE_IDL.value)


    val nowPlayingItem = MutableStateFlow(NOTHING_PLAYING)
    private var nowPlayingJob: Job? = null

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(
        context,
        serviceComponent,
        mediaBrowserConnectionCallback,
        null
    ).apply {
        connect()
    }

    private lateinit var mediaController: MediaControllerCompat

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
        collectNowPlaying()
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
        nowPlayingJob?.cancel()
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
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            when (event) {
                "MEDIA_SOURCE_STATE" -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        mediaSourceState.emit(
                            extras?.getInt("MEDIA_SOURCE_STATE") ?: 0
                        )
                        Log.d(TAG, "MediaSource state: ${mediaSourceState.value}")
                    }
                }

                else -> super.onSessionEvent(event, extras)
            }
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }

    private fun collectNowPlaying() {
        val scope = CoroutineScope(Dispatchers.IO)
        nowPlayingJob = scope.launch {
            nowPlaying.collect {
                nowPlayingItem.value = it
            }
        }
    }

    companion object {
        const val TAG = "FreeRadioMediaServiceConnection"

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

        val nowPlaying = MutableStateFlow(NOTHING_PLAYING)

        class PlayerListener : Player.Listener {
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                nowPlaying.value = MediaMetadataCompat.Builder()
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_TITLE,
                        mediaMetadata.displayTitle?.toString() ?: ""
                    )
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_ART_URI,
                        mediaMetadata.artworkUri?.toString() ?: ""
                    )
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,
                        mediaMetadata.title?.toString() ?: ""
                    )
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,
                        mediaMetadata.description?.toString() ?: ""
                    )
                    .build()
            }
        }
    }
}

val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()

val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()
