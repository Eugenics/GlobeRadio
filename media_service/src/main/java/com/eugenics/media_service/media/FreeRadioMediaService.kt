package com.eugenics.media_service.media

import android.app.Notification
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media.MediaBrowserServiceCompat
import com.eugenics.core.enums.MediaSourceState
import com.eugenics.core.enums.Commands
import com.eugenics.data.interfaces.IPrefsRepository
import com.eugenics.data.interfaces.IStationsRepository
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection.Companion.SET_FAVORITES_STATION_KEY
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection.Companion.SET_FAVORITES_VALUE_KEY
import com.eugenics.media_service.player.getMediaItems
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MEDIA_SESSION_LOG_TAG = "free_radio_media_session"
private const val STATIONS_ROOT = "/"
private const val EMPTY_ROOT = "@empty@"

@AndroidEntryPoint
class FreeRadioMediaService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var stationsRepository: IStationsRepository

    @Inject
    lateinit var prefsRepository: IPrefsRepository

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private val playerAudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val playerListener = FreeRadioMediaServiceConnection.Companion.PlayerListener()

    private val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
        .setDefaultRequestProperties(mapOf("1" to "Icy-MetaData"))

    private val player: ExoPlayer by lazy {
        ExoPlayer.Builder(baseContext)
            .setMediaSourceFactory(
                ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory)
            )
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
        MediaSource(
            stationsRepository = stationsRepository,
            prefsRepository = prefsRepository
        )
    }

    override fun onCreate() {
        super.onCreate()

        collectMediaState()

        mediaSession = MediaSessionCompat(baseContext, MEDIA_SESSION_LOG_TAG).apply {
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            setPlaybackState(stateBuilder.build())

            setCallback(mediaSessionCallbacks)

            setSessionToken(sessionToken)
            isActive = true
        }
        val mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(playbackPrepare)
        mediaSessionConnector.setQueueNavigator(QueueNavigator(mediaSession))
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
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.run {
            isActive = false
            release()
        }
        player.release()
        notificationManager.hideNotification()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return if (allowBrowsing(clientPackageName, clientUid)) {
            BrowserRoot(STATIONS_ROOT, null)
        } else {
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
            Log.d(TAG, "SERVICE_ON_LOAD_CHILDREN")
            val browserMediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()

            Log.d(TAG, "MEDIA_SERVICE_SIZE:${mediaSource.mediaItems.value.size}")

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
            }
        }
    }

    private fun allowBrowsing(clientPackageName: String = "", clientUid: Int = 0): Boolean = true

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
        }
    }

    private val playbackPrepare = object : MediaSessionConnector.PlaybackPreparer {
        override fun onCommand(
            player: Player,
            command: String,
            extras: Bundle?,
            cb: ResultReceiver?
        ): Boolean {
            when (command) {
                Commands.FAVORITES_COMMAND.name -> {
                    Log.d(TAG, "SERVICE_COMMAND_FAVORITES")
                    mediaSource.collectMediaSource(
                        tag = "",
                        command = Commands.FAVORITES_COMMAND,
                        stationUuid = ""
                    )
                }

                Commands.STATIONS_COMMAND.name -> {
                    Log.d(TAG, "SERVICE_COMMAND_STATIONS")
                    val tag = extras?.getString("TAG") ?: ""
                    mediaSource.collectMediaSource(
                        tag = tag,
                        command = Commands.STATIONS_COMMAND,
                        stationUuid = ""
                    )
                }

                Commands.RELOAD_ALL_STATIONS_COMMAND.name -> {
                    Log.d(TAG, "RELOAD_ALL_STATIONS_COMMAND")
                    mediaSource.collectMediaSource(
                        tag = "",
                        command = Commands.RELOAD_ALL_STATIONS_COMMAND,
                        stationUuid = ""
                    )
                }

                Commands.SET_FAVORITES_COMMAND.name -> {
                    Log.d(TAG, "SERVICE_COMMAND_SET_FAVORITES")
                    extras?.let { bundle ->
                        val stationUuid = bundle.getString(SET_FAVORITES_STATION_KEY)
                        val isFavorite = bundle.getInt(SET_FAVORITES_VALUE_KEY)
                        stationUuid?.let { uuid ->
                            mediaSource.setFavorites(
                                stationUuid = uuid,
                                isFavorite = isFavorite,
                                cb = cb
                            )
                        }
                    }
                }

                Commands.RELEASE.name -> {
                    player.stop()
                    notificationManager.hideNotification()
                    this@FreeRadioMediaService.stopForeground(STOP_FOREGROUND_REMOVE)
                    this@FreeRadioMediaService.stopSelf()
                    Log.d(TAG, "RELEASE command...")
                }

                Commands.PREPARE.name -> {
                    if (notificationManager.notificationState ==
                        FreeRadioNotificationManager.NOTIFICATION_IS_HIDE
                    ) {
                        notificationManager.showNotificationForPlayer(player)
                    }
                }
            }
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
            Log.d(TAG, "SERVICE_PREPARE_FROM_MEDIA_ID:$mediaId")
            preparePlayList(mediaItemId = mediaId, STATE_ON_MEDIA_ITEM)

        }

        override fun onPrepareFromSearch(
            query: String,
            playWhenReady: Boolean,
            extras: Bundle?
        ) {
            Log.d(TAG, "SERVICE_PREPARE_FROM_SEARCH:$query")
            preparePlayList(mediaItemId = query, STATE_ON_SEARCH)
        }

        override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {
        }

    }

    private fun preparePlayList(
        mediaItemId: String = "",
        state: Int = STATE_PREPARE
    ) {
        Log.d(TAG, "preparePlayList... $state")
        when (state) {
            STATE_ON_SEARCH -> {
                mediaSource.collectMediaSource(
                    tag = "",
                    stationUuid = "",
                    command = Commands.SEARCH_COMMAND,
                    query = mediaItemId
                )
            }

            STATE_ON_MEDIA_ITEM -> {
                mediaSource.onMediaItemClick(mediaItemId = mediaItemId, playWhenReady = false)
                player.stop()
                player.seekTo(mediaSource.getStartPosition(), 0L)
                player.playWhenReady = true
                player.prepare()
            }

            else -> {
                val currentMediaId = player.currentMediaItem?.mediaId ?: ""
                if (currentMediaId.isNotBlank()) {
                    mediaSource.onMediaItemClick(
                        mediaItemId = currentMediaId,
                        playWhenReady = false
                    )
                    player.stop()
                    player.seekTo(mediaSource.getStartPosition(), 0L)
                    player.playWhenReady = true
                    player.prepare()
                }
            }
        }
    }

    private fun collectMediaState() {
        serviceScope.launch {
            mediaSource.state.collect { state ->
                Log.d(TAG, "COLLECT_MEDIA_SOURCE_STATE:$state")

                if (state == MediaSourceState.STATE_INITIALIZED.value) {
                    player.stop()
                    player.setMediaItems(
                        mediaSource.mediaItems.value.map { playerMediaItem ->
                            MediaItem.Builder()
                                .setMediaId(playerMediaItem.uuid)
                                .setUri(playerMediaItem.urlResolved.toUri())
                                .setMediaMetadata(
                                    MediaMetadata.Builder()
                                        .setSubtitle(playerMediaItem.tags)
                                        .setDisplayTitle(playerMediaItem.name)
                                        .setArtworkUri(playerMediaItem.favicon.toUri())
                                        .setDescription("${playerMediaItem.codec}:${playerMediaItem.bitrate}")
                                        .also {
                                            val extras = Bundle()
                                            extras.putString("url", playerMediaItem.url)
                                            extras.putString(
                                                MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                                                playerMediaItem.uuid
                                            )
                                            it.setExtras(extras)
                                        }
                                        .build()
                                )
                                .build()
                        }, mediaSource.getStartPosition(), 0L
                    )
                    player.playWhenReady = mediaSource.getPlayOnReady()
                    player.prepare()

                    notifyChildrenChanged(STATIONS_ROOT)
                }
                sendMediaSourceState(state = state)
            }
        }
    }

    private fun sendMediaSourceState(state: Int) {
        Log.d(TAG, "SEND MEDIA_SOURCE_STATE:${MediaSourceState.getNameByValue(value = state)}")

        mediaSession.sendSessionEvent("MEDIA_SOURCE_STATE",
            Bundle().also {
                it.putInt("MEDIA_SOURCE_STATE", state)
            }
        )
    }

    private val mediaSessionCallbacks = object : MediaSessionCompat.Callback() {

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            Log.d(TAG, "ON_PLAY_FROM_MEDIA_ID:$mediaId")
        }
    }

    private inner class QueueNavigator(
        mediaSession: MediaSessionCompat
    ) : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            if (windowIndex < player.getMediaItems().size) {
                val metaData = MediaMetadataCompat.Builder()
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_TITLE,
                        player.mediaMetadata.displayTitle?.toString() ?: ""
                    )
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_ART_URI,
                        player.mediaMetadata.artworkUri?.toString() ?: ""
                    )
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                        player.currentMediaItem?.mediaMetadata?.extras?.getString("url")
                    )
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,
                        player.currentMediaItem?.mediaMetadata?.description.toString()
                    )
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                        player.currentMediaItem?.mediaId
                    )
                    .build()
                return metaData.description
            }
            return MediaDescriptionCompat.Builder().build()
        }
    }


    companion object {
        const val TAG = "FREE_RADIO_MEDIA_SERVICE"
        private const val STATE_PREPARE = 0
        private const val STATE_ON_MEDIA_ITEM = 1
        private const val STATE_ON_SEARCH = 2
    }
}