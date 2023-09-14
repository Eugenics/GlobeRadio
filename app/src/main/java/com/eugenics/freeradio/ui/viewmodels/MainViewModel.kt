package com.eugenics.freeradio.ui.viewmodels

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.eugenics.core.enums.MediaSourceState
import com.eugenics.core.enums.Commands
import com.eugenics.core.model.NowPlayingStation
import com.eugenics.core.model.PlayerMediaItem
import com.eugenics.core.model.Station
import com.eugenics.core.model.CurrentState
import com.eugenics.core.enums.Theme
import com.eugenics.core.model.FavoriteStation
import com.eugenics.core.model.Favorites
import com.eugenics.core.model.StationsUiState
import com.eugenics.data.interfaces.IStationsRepository
import com.eugenics.freeradio.core.enums.DataState
import com.eugenics.freeradio.core.enums.InfoMessages
import com.eugenics.freeradio.core.enums.MessageType
import com.eugenics.freeradio.core.enums.UIState
import com.eugenics.freeradio.ui.util.ServiceViewModel
import com.eugenics.freeradio.ui.util.PlayBackState
import com.eugenics.freeradio.util.ImageHelper
import com.eugenics.freeradio.ui.util.UICommands
import com.eugenics.freeradio.util.FilesHelper
import com.eugenics.freeradio.util.PlaylistHelper
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import java.util.UUID
import javax.inject.Inject
import kotlinx.serialization.json.Json

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mediaServiceConnection: FreeRadioMediaServiceConnection,
    private val stationsRepository: IStationsRepository,
    private val dataStore: DataStore<CurrentState>
) : ServiceViewModel() {

    private val nowPlayingMetaData = mediaServiceConnection.nowPlayingItem

    private var currentMediaId: String = ""
    private val rootId = "/"

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String, children: List<MediaBrowserCompat.MediaItem>
        ) {
            val stationServiceContent = mutableListOf<Station>()

            for (child in children) {
                val extras = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    child.description.extras?.getParcelable("STATION")
                } else {
                    child.description.extras?.getParcelable(
                        "STATION", PlayerMediaItem::class.java
                    )
                }
                extras?.let {
                    stationServiceContent.add(
                        Station(
                            stationuuid = child.mediaId ?: extras.uuid,
                            name = extras.name,
                            tags = extras.tags,
                            homepage = extras.homepage,
                            url = extras.url,
                            urlResolved = extras.urlResolved,
                            favicon = extras.favicon,
                            bitrate = extras.bitrate,
                            codec = extras.codec,
                            country = "",
                            countrycode = "",
                            language = "",
                            languagecodes = "",
                            changeuuid = "",
                            isFavorite = extras.isFavorite,
                            votes = extras.votes
                        )
                    )
                }
            }
            _stations.value = stationServiceContent
        }
    }

    fun start() {
        collectStates()
        collectMediaSourceState()
        collectNowPlaying()
        collectCurrentState()
        collectServiceConnection()
        collectServicePlaybackState()
    }

    private fun unsubscribe() {
        mediaServiceConnection.unsubscribe(rootId)
    }

    override fun onCleared() {
        unsubscribe()
    }

    /**
     * MediaService commands
     */
    fun onPlayClick(mediaId: String? = currentMediaId) {
        when (mediaId ?: currentMediaId) {
            currentMediaId -> if (playBackState.value == PlayBackState.Playing) {
                mediaServiceConnection.transportControls.pause()
            } else {
                mediaServiceConnection.transportControls.play()
            }

            else -> {
                Log.d(TAG, "PLAY_FROM_MEDIA_ID_CLICKED:$mediaId")
                mediaServiceConnection.transportControls.playFromMediaId(mediaId, null)
                setSettings(stationUuid = mediaId ?: currentMediaId)
            }
        }
    }

    fun onSearch(query: String) {
        setSettings(visibleIndex = 0)
        _stations.value = listOf()
        mediaServiceConnection.transportControls.playFromSearch(query, null)
    }

    fun sendCommand(command: String, extras: Bundle? = null) {
        when (command) {
            UICommands.UI_COMMAND_BACKUP_FAVORITES.name -> backUpFavorites()
            UICommands.UI_COMMAND_RESTORE_FAVORITES.name -> _uiCommand.value =
                UICommands.UI_COMMAND_RESTORE_FAVORITES

            UICommands.UI_EXPORT_FAVORITES_PLAYLIST.name -> exportFavoritesAsPlayList()

            in enumValues<Commands>().map { it.name }.toList() -> {
                setSettings(
                    command = command, tag = extras?.getString("TAG") ?: ""
                )
                if (command != Commands.SET_FAVORITES_COMMAND.name) {
                    _stations.value = listOf()
                    setSettings(visibleIndex = 0)
                }
                mediaServiceConnection.sendCommand(
                    command = command,
                    parameters = extras,
                    resultCallback = { result, bundle ->
                        if (result == 1) {
                            Log.d(TAG, "Command: $command success")
                        } else {
                            Log.e(
                                TAG,
                                bundle?.getString(Commands.SET_FAVORITES_COMMAND.name)
                                    ?: "Command: $command error"
                            )
                        }
                    })
            }
        }
    }

    /**
     * UI State watchers
     */

    fun onVisibleIndexChanged(index: Int) {
        Log.d(TAG, "onVisibleIndexChanged:$index")
        setSettings(visibleIndex = index)
    }

    /**
     * Collectors
     */
    private fun collectServiceConnection() {
        viewModelScope.launch {
            mediaServiceConnection.isConnected.collect {
                if (it) {
                    mediaServiceConnection.subscribe(rootId, subscriptionCallback)
                    return@collect
                }
            }
        }
    }

    private fun collectNowPlaying() {
        viewModelScope.launch(ioDispatcher) {
            nowPlayingMetaData.collect { metaData ->
                _nowPlaying.value = NowPlayingStation.newInstance(
                    name = metaData.getString(MediaMetadataCompat.METADATA_KEY_TITLE) ?: "",
                    favicon = metaData.description.iconUri.toString(),
                    nowPlayingTitle = metaData.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE)
                        ?: "",
                    stationUUID = metaData.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                        ?: "",
                    description = metaData.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION)
                        ?: ""
                )
                currentMediaId = nowPlaying.value.stationUUID

                getDynamicColor(url = nowPlaying.value.favicon)
            }
        }
    }

    private fun collectCurrentState() {
        viewModelScope.launch(ioDispatcher) {
            dataStore.data.collect {
                _currentStateObject.value = it
                setStationUiState(it.stationUuid, it.stationsVisibleIndex)
            }
        }
    }

    private fun collectMediaSourceState() {
        viewModelScope.launch(Dispatchers.IO) {
            mediaServiceConnection.mediaSourceState.collect { state ->
                when (state) {
                    MediaSourceState.STATE_FIRST_INIT.value -> {
                        _uiState.value = UIState.UI_STATE_SPLASH
                    }

                    MediaSourceState.STATE_CREATED.value -> _uiState.value = UIState.UI_STATE_SPLASH
                    MediaSourceState.STATE_ERROR.value -> {
                        _uiState.value = UIState.UI_STATE_MAIN
                        _dataState.value = DataState.ERROR
                    }

                    MediaSourceState.STATE_INITIALIZING.value -> {
                        _uiState.value = UIState.UI_STATE_MAIN
                        _dataState.value = DataState.LOADING
                    }

                    MediaSourceState.STATE_INITIALIZED.value -> {
                        _uiState.value = UIState.UI_STATE_MAIN
                        _dataState.value = DataState.PREPARED
                    }
                }
            }
        }
    }

    private fun collectServicePlaybackState() {
        viewModelScope.launch(Dispatchers.IO) {
            mediaServiceConnection.playbackState.collect { stateCompat ->
                when (stateCompat.state) {
                    PlaybackStateCompat.STATE_PLAYING -> _playBackState.value =
                        PlayBackState.Playing

                    else -> _playBackState.value = PlayBackState.Pause
                }
            }
        }
    }

    fun setSettings(
        tag: String = currentStateObject.value.tag,
        stationUuid: String = currentStateObject.value.stationUuid,
        theme: Theme = currentStateObject.value.theme,
        command: String = currentStateObject.value.command,
        visibleIndex: Int = currentStateObject.value.stationsVisibleIndex
    ) {
        viewModelScope.launch(ioDispatcher) {
            val currentState = CurrentState(
                tag = tag,
                stationUuid = stationUuid,
                theme = theme,
                command = command,
                stationsVisibleIndex = visibleIndex
            )
            setSettings(settings = currentState)
            Log.d(TAG, "setSettings:$currentState")

            setStationUiState(stationUuid, visibleIndex)
        }
    }

    private fun setStationUiState(stationUuid: String, visibleIndex: Int) {
        val newStationUiState = StationsUiState(stationUuid, visibleIndex)
        if (stationsUiState.value != newStationUiState) {
            _stationsUiState.value = newStationUiState
        }
    }

    fun getTagsList(context: Context) {
        if (tagList.value.isEmpty()) {
            viewModelScope.launch(ioDispatcher) {
                _tagList.value = FilesHelper.getTags(context = context)
            }
        }
    }

    fun setUICommand(command: UICommands) {
        _uiCommand.value = command
    }

    private fun exportFavoritesAsPlayList() {
        viewModelScope.launch(ioDispatcher) {
            stationsRepository.fetchStationsByFavorites().apply {
                if (this.isNotEmpty()) {
                    _savedData.value = PlaylistHelper.convertStationsToPlaylist(this)
                    setUICommand(UICommands.UI_EXPORT_FAVORITES_PLAYLIST)
                } else {
                    sendMessage(MessageType.INFO, InfoMessages.NO_DATA_TO_LOAD.name)
                }
            }
        }
    }

    private fun backUpFavorites() {
        viewModelScope.launch(ioDispatcher) {
            stationsRepository.fetchStationsByFavorites()
                .map { station ->
                    FavoriteStation(
                        uuid = UUID.randomUUID().toString(), stationuuid = station.stationuuid
                    )
                }
                .also { favStations ->
                    if (favStations.isNotEmpty()) {
                        try {
                            _savedData.value = Json.encodeToString(
                                serializer = Favorites.serializer(),
                                value = Favorites.newInstance(favStations)
                            )
                            setUICommand(UICommands.UI_COMMAND_BACKUP_FAVORITES)
                        } catch (e: SerializationException) {
                            sendMessage(MessageType.ERROR, e.message.toString())
                            Log.e(TAG, e.toString())
                        }
                    } else {
                        sendMessage(MessageType.INFO, InfoMessages.NO_DATA_TO_LOAD.name)
                    }
                }
        }
    }

    fun restoreFavorites(favoritesJsonString: String) {
        viewModelScope.launch(ioDispatcher) {
            try {
                if (favoritesJsonString.isNotBlank()) {
                    val favorites = Json.decodeFromString(
                        Favorites.serializer(), favoritesJsonString
                    )
                    stationsRepository.restoreFavorites(
                        favorites = favorites.stationList
                    )
                    withContext(Dispatchers.Main) {
                        sendCommand(command = Commands.FAVORITES_COMMAND.name, null)
                    }
                } else {
                    sendMessage(MessageType.ERROR, InfoMessages.NO_DATA_TO_SAVE.name)
                }
            } catch (e: Exception) {
                sendMessage(MessageType.ERROR, e.message.toString())
                Log.e(TAG, e.toString())
            }
        }
    }

    private fun getDynamicColor(url: String) {
        if (url != "null" && url.isNotBlank()) {
            try {
                ImageHelper.downloadAsync(imageUrl = url) { response ->
                    response.body?.let { responseBody ->
                        val bitmap = BitmapFactory.decodeStream(responseBody.byteStream())
                        try {
                            Palette.from(bitmap).generate().dominantSwatch?.let {
                                setPrimaryDynamicColor(it.rgb)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, e.toString())
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        } else {
            setPrimaryDynamicColor(0)
        }
    }

    private suspend fun setSettings(settings: CurrentState) {
        dataStore.updateData {
            settings
        }
    }

    companion object {
        const val TAG = "MAIN_VIEW_MODEL"
    }
}