package com.eugenics.media_service.media

import android.app.Notification
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media.MediaBrowserServiceCompat
import com.eugenics.media_service.data.repository.RepositoryFactory
import com.eugenics.media_service.player.PlayerListener
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private const val MEDIA_SESSION_LOG_TAG = "free_radio_media_session"
private const val STATIONS_ROOT = "/"
private const val EMPTY_ROOT = "@empty@"

class FreeRadioMediaService : MediaBrowserServiceCompat() {
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private val playerAudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val playerListener = PlayerListener()

    private val player: Player by lazy {
        ExoPlayer.Builder(baseContext)
            .build()
            .apply {
                playWhenReady = false
                setAudioAttributes(playerAudioAttributes, true)
                setHandleAudioBecomingNoisy(true)
                addListener(playerListener)
            }
    }

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder

    private lateinit var notificationManager: FreeRadioNotificationManager

    private var isForegroundService = false

    private val mediaSource: MediaSource by lazy {
        MediaSource(repository = RepositoryFactory.create(this))
    }

    override fun onCreate() {
        super.onCreate()

        collectMediaSource()
        preparePlayList()

        // Create a MediaSessionCompat
        mediaSession = MediaSessionCompat(baseContext, MEDIA_SESSION_LOG_TAG).apply {
            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            setPlaybackState(stateBuilder.build())

            setCallback(mediaSessionCallbacks)

            // Set the session's token so that client activities can communicate with it.
            setSessionToken(sessionToken)
            isActive = true
        }
        val mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(playbackPrepare)
        mediaSessionConnector.setPlayer(player)

        notificationManager = FreeRadioNotificationManager(
            this@FreeRadioMediaService,
            mediaSession.sessionToken,
            PlayerNotificationListener()
        )
        notificationManager.showNotificationForPlayer(player)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        player.stop()
        player.clearMediaItems()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.run {
            isActive = false
            release()
        }
        player.release()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
        return if (allowBrowsing(clientPackageName, clientUid)) {
            // Returns a root ID that clients can use with onLoadChildren() to retrieve
            // the content hierarchy.
            BrowserRoot(STATIONS_ROOT, null)
        } else {
            // Clients can connect, but this BrowserRoot is an empty hierarchy
            // so onLoadChildren returns nothing. This disables the ability to browse for content.
            BrowserRoot(EMPTY_ROOT, null)
        }
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.detach()
        if (parentId == EMPTY_ROOT) {
            result.sendResult(null)
        } else {
            serviceScope.launch {
                mediaSource.state.collect {
                    Log.d(
                        "SERVICE_ON_LOAD_CHILDREN",
                        it.toString()
                    )
                    when (it) {
                        MediaSource.STATE_INITIALIZED -> {
                            val browserMediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()

                            Log.d(
                                "MEDIA_SERVICE_SIZE",
                                mediaSource.mediaItems.value.size.toString()
                            )

                            val mediaItems = mediaSource.mediaItems.value
                            for (item in mediaItems) {
                                val extras = Bundle()
                                extras.putParcelable("STATION", item)
                                val itemDescription = MediaDescriptionCompat.Builder()
                                    .setMediaId(item.uuid)
                                    .setMediaUri(item.urlResolved.toUri())
                                    .setDescription(item.name + ":" + item.tags)
                                    .setTitle(item.name)
                                    .setSubtitle(item.tags)
                                    .setIconUri(item.favicon.toUri())
                                    .setExtras(extras)
                                    .build()
                                browserMediaItems.add(
                                    MediaBrowserCompat.MediaItem(
                                        itemDescription,
                                        FLAG_PLAYABLE
                                    )
                                )
                            }
                            try {
                                result.sendResult(browserMediaItems)
                            } catch (ex: Exception) {
                                Log.e(TAG, ex.message.toString())
                                notifyChildrenChanged(STATIONS_ROOT)
                            }

                        }
                        else -> {}
                    }
                }
            }
        }
    }


    private fun allowBrowsing(clientPackageName: String = "", clientUid: Int = 0): Boolean = true

    /**
     * Listen for notification events.
     */
    private inner class PlayerNotificationListener :
        PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(applicationContext, this.javaClass)
                )

                startForeground(notificationId, notification)
                isForegroundService = true
            }
        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }
    }

    private val playbackPrepare = object : MediaSessionConnector.PlaybackPreparer {
        override fun onCommand(
            player: Player,
            command: String,
            extras: Bundle?,
            cb: ResultReceiver?
        ): Boolean {
            return true
        }

        override fun getSupportedPrepareActions(): Long =
            PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
                    PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
                    PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH

        override fun onPrepare(playWhenReady: Boolean) {
            preparePlayList()
        }

        override fun onPrepareFromMediaId(
            mediaId: String,
            playWhenReady: Boolean,
            extras: Bundle?
        ) {
            Log.d(TAG, "OnPrepare...")
            preparePlayList(mediaItemId = mediaId, STATE_ON_MEDIA_ITEM)
        }

        override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) {
            Log.d("SERVICE_PREPARE_FROM_SEARCH", query)
            preparePlayList(mediaItemId = query, STATE_ON_SEARCH)
        }

        override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {
        }

    }

    private fun preparePlayList(mediaItemId: String = "", state: Int = STATE_PREPARE) {
        Log.d(TAG, "preparePlayList... $state")
        when (state) {
            STATE_PREPARE -> mediaSource.collectMediaSource()
            STATE_ON_SEARCH -> mediaSource.searchInMediaSource(query = mediaItemId)
            STATE_ON_MEDIA_ITEM -> {
                mediaSource.onMediaItemClick(mediaItemId = mediaItemId)
                collectMediaSource()
            }
        }
    }

    private fun collectMediaSource() {
        serviceScope.launch {
            mediaSource.state.collect { state ->
                Log.d("COLLECT_STATE", state.toString())
                if (state == MediaSource.STATE_INITIALIZED) {
                    player.stop()
                    player.setMediaItems(
                        mediaSource.mediaItems.value.map { playerMediaItem ->
                            MediaItem.Builder()
                                .setMediaId(playerMediaItem.uuid)
                                .setTag(playerMediaItem.tags)
                                .setUri(playerMediaItem.urlResolved)
                                .setMediaMetadata(
                                    MediaMetadata.Builder()
                                        .setTitle(playerMediaItem.name)
                                        .setSubtitle(playerMediaItem.tags)
                                        .setDisplayTitle(playerMediaItem.name)
                                        .setArtworkUri(playerMediaItem.favicon.toUri())
                                        .build()
                                )
                                .build()
                        }, mediaSource.getStartPosition(), 0L
                    )
                    player.playWhenReady = mediaSource.getPlayOnReady()
                    player.prepare()
                    return@collect
                }
            }
        }
    }

    private val mediaSessionCallbacks = object : MediaSessionCompat.Callback() {
        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            Log.d("ON_PLAY_FROM_MEDIA_ID", mediaId.toString())
        }
    }

    companion object {
        const val TAG = "FreeMediaService"
        private const val STATE_PREPARE = 0
        private const val STATE_ON_MEDIA_ITEM = 1
        private const val STATE_ON_SEARCH = 2
    }
}