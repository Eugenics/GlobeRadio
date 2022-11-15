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
import com.eugenics.media_service.data.datastore.PrefsDataSource
import com.eugenics.media_service.data.datastore.PrefsDataStoreFactory
import com.eugenics.media_service.data.repository.RepositoryFactory
import com.eugenics.media_service.domain.core.TagsCommands
import com.eugenics.media_service.domain.model.CurrentPrefs
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection.Companion.SET_FAVORITES_COMMAND
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
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
        MediaSource(repository = RepositoryFactory.create(this))
    }

    private val prefs: MutableStateFlow<CurrentPrefs> =
        MutableStateFlow(CurrentPrefs.getDefaultInstance())

    private val prefsDataSource by lazy {
        PrefsDataSource(
            dataStore = PrefsDataStoreFactory.create(
                application = application
            )
        )
    }

    override fun onCreate() {
        super.onCreate()

        collectMediaState()
        collectPrefs(prefsDataSource = prefsDataSource)

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
//        result.detach()
        if (parentId == EMPTY_ROOT) {
            result.sendResult(null)
        } else {
            Log.d("SERVICE_ON_LOAD_CHILDREN", "")
            val browserMediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()

            Log.d("MEDIA_SERVICE_SIZE", mediaSource.mediaItems.value.size.toString())

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
            stopForeground(STOP_FOREGROUND_REMOVE)
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
            when (command) {
                TagsCommands.FAVORITES_COMMAND.name -> {
                    Log.d("SERVICE_COMMAND_FAVORITES", command)
                    mediaSource.collectFavorites()
                    setPrefs(
                        tag = "*",
                        command = TagsCommands.FAVORITES_COMMAND.name
                    )
                }
                TagsCommands.STATIONS_COMMAND.name -> {
                    Log.d("SERVICE_COMMAND_STATIONS", command)
                    val tag = extras?.getString("TAG") ?: "*"
                    mediaSource.searchInMediaSource(query = tag)
                    setPrefs(
                        tag = tag,
                        command = TagsCommands.STATIONS_COMMAND.name
                    )
                }
                SET_FAVORITES_COMMAND -> {
                    Log.d("SERVICE_COMMAND_SET_FAVORITES", command)
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
                TagsCommands.RELOAD_ALL_STATIONS_COMMAND.name -> {
                    Log.d("RELOAD_ALL_STATIONS_COMMAND", command)
                    mediaSource.reloadStations()
                    setPrefs(
                        tag = "*",
                        command = TagsCommands.STATIONS_COMMAND.name
                    )
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
            Log.d(TAG, "OnPrepareFromMediaId...")
            preparePlayList(mediaItemId = mediaId, STATE_ON_MEDIA_ITEM)
            setPrefs(stationUuid = mediaId)
        }

        override fun onPrepareFromSearch(
            query: String,
            playWhenReady: Boolean,
            extras: Bundle?
        ) {
            Log.d("SERVICE_PREPARE_FROM_SEARCH", query)
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
            STATE_ON_SEARCH -> mediaSource.collectSearch(query = mediaItemId)
            STATE_ON_MEDIA_ITEM -> mediaSource.onMediaItemClick(mediaItemId = mediaItemId)
        }
    }

    private fun collectMediaState() {
        serviceScope.launch {
            mediaSource.state.collect { state ->
                Log.d("COLLECT_STATE", state.toString())
                if (state == MediaSource.STATE_INITIALIZED) {
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
                                        .also {
                                            val extras = Bundle()
                                            extras.putString("url", playerMediaItem.url)
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
            }
        }
    }

    private val mediaSessionCallbacks = object : MediaSessionCompat.Callback() {
        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            Log.d("ON_PLAY_FROM_MEDIA_ID", mediaId.toString())
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
                    .build()
                return metaData.description
            }
            return MediaDescriptionCompat.Builder().build()
        }
    }

    private fun collectPrefs(prefsDataSource: PrefsDataSource) {
        serviceScope.launch {
            prefsDataSource.getPrefs().collect {
                mediaSource.collectMediaSource(
                    tag = it.tag,
                    stationUuid = it.stationUuid,
                    command = it.command
                )
                prefs.value = it
                return@collect
            }
        }
    }

    private fun setPrefs(
        tag: String = prefs.value.tag,
        stationUuid: String = prefs.value.stationUuid,
        command: String = prefs.value.command
    ) {
        serviceScope.launch {
            prefsDataSource.setPrefs(
                prefs = CurrentPrefs(
                    tag = tag,
                    stationUuid = stationUuid,
                    command = command
                )
            )
        }
    }

    companion object {
        const val TAG = "FreeMediaService"
        private const val STATE_PREPARE = 0
        private const val STATE_ON_MEDIA_ITEM = 1
        private const val STATE_ON_SEARCH = 2
    }
}