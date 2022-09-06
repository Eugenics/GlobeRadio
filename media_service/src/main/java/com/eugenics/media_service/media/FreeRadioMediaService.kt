package com.eugenics.media_service.media

import android.app.Notification
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media.MediaBrowserServiceCompat
import com.eugenics.media_service.data.repository.RepositoryFactory
import com.eugenics.media_service.domain.model.PlayerMediaItem
import com.eugenics.media_service.player.PlayerListener
import com.eugenics.media_service.player.addMediaItems
import com.eugenics.media_service.player.getMediaItems
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
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

    private val fakeMediaItem = PlayerMediaItem(
        uuid = "96202f73-0601-11e8-ae97-52543be04c81",
        name = "Radio Schizoid - Chillout / Ambient",
        tags = "Electronics",
        homepage = "",
        url = "http://94.130.113.214:8000/chill",
        urlResolved = "http://94.130.113.214:8000/chill",
        favicon = "http://static.radio.net/images/broadcasts/db/08/33694/c175.png",
        bitrate = 128,
        codec = "MP3"
    )

    private val player: Player by lazy {
        ExoPlayer.Builder(baseContext)
            .build()
            .apply {
                playWhenReady = false
                setAudioAttributes(playerAudioAttributes, true)
                setHandleAudioBecomingNoisy(true)
//                addMediaItems(mediaItems = mediaSource.mediaItems.value)
//                addMediaItem(0, fakeMediaItem)
                addListener(playerListener)
//                prepare()
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

        // Create a MediaSessionCompat
        mediaSession = MediaSessionCompat(baseContext, MEDIA_SESSION_LOG_TAG).apply {
            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            setPlaybackState(stateBuilder.build())

//            setCallback(MySessionCallback())

            // Set the session's token so that client activities can communicate with it.
            setSessionToken(sessionToken)
            isActive = true
        }

        serviceScope.launch {
            mediaSource.collectMediaSource()
            mediaSource.state.collect {
                if (mediaSource.state.value == MediaSource.STATE_INITIALIZED) {
                    player.addMediaItems(mediaItems = mediaSource.mediaItems.value)
                    player.prepare()

                    val mediaSessionConnector = MediaSessionConnector(mediaSession)
                    mediaSessionConnector.setPlayer(player)

                    notificationManager = FreeRadioNotificationManager(
                        this@FreeRadioMediaService,
                        mediaSession.sessionToken,
                        PlayerNotificationListener()
                    )
                    notificationManager.showNotificationForPlayer(player)
                }
            }
        }
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
        if (parentId == EMPTY_ROOT) {
            result.sendResult(null)
        } else {
            if (mediaSource.state.value == MediaSource.STATE_INITIALIZED) {
                val mediaItems = mediaSource.mediaItems.value
                val browserMediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()
                for (item in mediaItems) {
                    val itemDescription = MediaDescriptionCompat.Builder()
                        .setMediaId(item.uuid)
                        .setMediaUri(item.urlResolved.toUri())
                        .setDescription(item.name + ":" + item.tags)
                        .setTitle(item.name)
                        .setSubtitle(item.tags)
                        .build()
                    browserMediaItems.add(
                        MediaBrowserCompat.MediaItem(
                            itemDescription,
                            FLAG_PLAYABLE
                        )
                    )
                }
                result.sendResult(browserMediaItems)
            } else {
                result.sendResult(null)
            }

        }
    }

    private fun allowBrowsing(clientPackageName: String= "", clientUid: Int = 0): Boolean = true

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
}