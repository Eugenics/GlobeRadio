package com.eugenics.media_service.media

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.eugenics.core.enums.Commands
import com.eugenics.core.enums.MediaSourceState
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.last

class FreeRadioMediaServiceConnection(context: Context, serviceComponent: ComponentName) {

    val isConnected = MutableStateFlow(false)
    val playbackState = MutableStateFlow(EMPTY_PLAYBACK_STATE)
    val mediaSourceState: MutableStateFlow<Int> =
        MutableStateFlow(MediaSourceState.STATE_IDL.value)


    val nowPlayingItem = MutableStateFlow(NOTHING_PLAYING)
    private var nowPlayingJob: Job? = null

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback()
    private val mediaBrowser =
        MediaBrowserCompat(
            context,
            serviceComponent,
            mediaBrowserConnectionCallback,
            null
        ).apply {
            Log.d(TAG, "CREATE MEDIA_BROWSER...")
            connect()
        }

    private val mediaController: MediaControllerCompat by lazy {
        Log.d(TAG, "CREATE MEDIA_CONTROLLER...")
        MediaControllerCompat(context, mediaBrowser.sessionToken)
            .apply {
                registerCallback(MediaControllerCallback())
            }
    }

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    fun subscribe(
        parentId: String,
        callback: MediaBrowserCompat.SubscriptionCallback =
            object : MediaBrowserCompat.SubscriptionCallback() {}
    ) {
        mediaBrowser.subscribe(parentId, callback)
        collectNowPlaying()
        sendCommand(Commands.PREPARE.name)
    }

    fun unsubscribe(
        parentId: String,
        callback: MediaBrowserCompat.SubscriptionCallback =
            object : MediaBrowserCompat.SubscriptionCallback() {}
    ) {
        mediaBrowser.unsubscribe(parentId, callback)
        nowPlayingJob?.cancel()
        sendCommand(Commands.RELEASE.name)
    }

    fun sendCommand(
        command: String,
        parameters: Bundle? = null,
        resultCallback: ((Int, Bundle?) -> Unit) = { _, _ -> }
    ) = if (mediaBrowser.isConnected) {
        mediaController.sendCommand(
            command,
            parameters,
            object : ResultReceiver(Handler(Looper.myLooper()!!)) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                    resultCallback(resultCode, resultData)
                }
            })
        true
    } else {
        false
    }

    private inner class MediaBrowserConnectionCallback : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            Log.d(TAG, "MEDIA_BROWSER CONNECTED...")
            isConnected.value = true

            Log.d(TAG, "SEND COMMAND GET_LAST_MEDIA_SOURCE_EVENT...")
            sendCommand(
                Commands.GET_LAST_MEDIA_SOURCE_EVENT.name, null
            ) { _, resultBundle ->
                val lastState = resultBundle?.getInt(Commands.GET_LAST_MEDIA_SOURCE_EVENT.name) ?: 0
                mediaSourceState.value = lastState
                Log.d(TAG, "LAST MEDIA_SOURCE_STATE:$lastState")
            }
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
                    CoroutineScope(Dispatchers.Default).launch {
                        val state = extras?.getInt("MEDIA_SOURCE_STATE") ?: 0
                        mediaSourceState.emit(state)
                        Log.d(TAG, "MediaSource state: ${mediaSourceState.last()}")
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

        fun getInstance(context: Context, serviceComponent: ComponentName) =
            instance ?: synchronized(this) {
                instance ?: FreeRadioMediaServiceConnection(context, serviceComponent)
                    .also { instance = it }
            }

        val nowPlaying = MutableStateFlow(NOTHING_PLAYING)

        class PlayerListener : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Log.e(com.eugenics.media_service.player.PlayerListener.TAG, error.toString())
                nowPlaying.value = MediaMetadataCompat.Builder()
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_TITLE,
                        "Playback error..."
                    )
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_ART_URI, ""
                    )
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, ""
                    )
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, ""
                    )
                    .build()
            }

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
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                        mediaMetadata.extras?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                            ?: ""
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
