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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.eugenics.core.enums.MediaSourceState
import com.eugenics.core.enums.Commands
import com.eugenics.core.model.NowPlayingStation
import com.eugenics.core.model.PlayerMediaItem
import com.eugenics.core.model.Station
import com.eugenics.core.model.Tag
import com.eugenics.core.model.CurrentState
import com.eugenics.core.enums.Theme
import com.eugenics.core.model.FavoriteStation
import com.eugenics.core.model.Favorites
import com.eugenics.data.interfaces.IStationsRepository
import com.eugenics.freeradio.core.data.SystemMessage
import com.eugenics.freeradio.core.enums.DataState
import com.eugenics.freeradio.core.enums.MessageType
import com.eugenics.freeradio.core.enums.UIState
import com.eugenics.freeradio.ui.util.PlayBackState
import com.eugenics.freeradio.util.ImageHelper
import com.eugenics.freeradio.ui.util.UICommands
import com.eugenics.freeradio.util.FilesHelper
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mediaServiceConnection: FreeRadioMediaServiceConnection,
    private val stationsRepository: IStationsRepository,
    private val dataStore: DataStore<CurrentState>
) : ViewModel() {

    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.UI_STATE_SPLASH)
    val uiState: StateFlow<UIState> = _uiState

    private val _dataState: MutableStateFlow<DataState> = MutableStateFlow(DataState.LOADING)
    val dataState: StateFlow<DataState> = _dataState

    private val _stations: MutableStateFlow<List<Station>> = MutableStateFlow(mutableListOf())
    val stations: StateFlow<List<Station>> = _stations

    private val _playBackState: MutableStateFlow<PlayBackState> =
        MutableStateFlow(PlayBackState.Pause)
    val playBackState: StateFlow<PlayBackState> = _playBackState

    private val _currentStateObject: MutableStateFlow<CurrentState> =
        MutableStateFlow(CurrentState.getDefaultValueInstance())
    val currentStateObject: StateFlow<CurrentState> = _currentStateObject

    private val nowPlayingMetaData = mediaServiceConnection.nowPlayingItem
    val nowPlaying = MutableStateFlow(NowPlayingStation.emptyInstance())

    private val ioDispatcher = Dispatchers.IO

    private var currentMediaId: String = ""
    private val rootId = "/"

    private var _tagList: MutableStateFlow<List<Tag>> = MutableStateFlow(listOf())
    val tagList: StateFlow<List<Tag>> = _tagList

    private val _backUpData: MutableStateFlow<String> = MutableStateFlow("")
    val backUpData: StateFlow<String> = _backUpData

    private val _message: MutableStateFlow<SystemMessage> =
        MutableStateFlow(SystemMessage.emptyInstance())
    val message: StateFlow<SystemMessage> = _message

    private val _uiCommand: MutableStateFlow<UICommands> =
        MutableStateFlow(UICommands.UI_COMMAND_IDL)
    val uiCommands: StateFlow<UICommands> = _uiCommand

    private val _primaryDynamicColor: MutableStateFlow<Int> = MutableStateFlow(0)
    val primaryDynamicColor: StateFlow<Int> = _primaryDynamicColor

    private val _visibleIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val visibleIndex: StateFlow<Int> = _visibleIndex

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
                            isFavorite = extras.isFavorite
                        )
                    )
                }
            }
            _stations.value = stationServiceContent
        }
    }

    private val favIcoDownloadCallback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e(TAG, e.toString())
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                response.body?.let { responseBody ->
                    val bitmap = BitmapFactory.decodeStream(responseBody.byteStream())
                    try {
                        Palette.from(bitmap).generate().dominantSwatch?.let {
                            _primaryDynamicColor.value = it.rgb
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    }
                }
            }
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

    fun onItemClick(mediaId: String) {
        if (mediaId == currentMediaId) {
            when (playBackState.value) {
                PlayBackState.Playing -> pause()
                else -> play()
            }
        } else {
            Log.d(TAG, "PLAY_FROM_MEDIA_ID_CLICKED:$mediaId")
            mediaServiceConnection.transportControls.playFromMediaId(mediaId, null)
            setSettings(stationUuid = mediaId)
        }
    }

    fun onVisibleIndexChanged(index: Int) {
        Log.d(TAG, "onVisibleIndexChanged:$index")
        setSettings(visibleIndex = index)
    }

    fun play() {
        mediaServiceConnection.transportControls.play()
    }

    fun pause() {
        mediaServiceConnection.transportControls.pause()
    }

    fun search(query: String) {
        setSettings(visibleIndex = 0)
        _stations.value = listOf()
        mediaServiceConnection.transportControls.playFromSearch(query, null)
    }

    fun sendCommand(command: String, extras: Bundle? = null) {
        when (command) {
            UICommands.UI_COMMAND_BACKUP_FAVORITES.name -> backUpFavorites()
            UICommands.UI_COMMAND_RESTORE_FAVORITES.name -> _uiCommand.value =
                UICommands.UI_COMMAND_RESTORE_FAVORITES

            in enumValues<Commands>().map { it.name }.toList() -> {
                setSettings(
                    command = command, tag = extras?.getString("TAG") ?: ""
                )
                if (command != Commands.SET_FAVORITES_COMMAND.name) {
                    _stations.value = listOf()
                    setSettings(visibleIndex = 0)
                }
                mediaServiceConnection.sendCommand(command = command,
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

    private fun collectNowPlaying() {
        viewModelScope.launch(ioDispatcher) {
            nowPlayingMetaData.collect { metaData ->
                nowPlaying.value = NowPlayingStation.newInstance(
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

                downloadFavIco(url = nowPlaying.value.favicon)
            }
        }
    }

    private fun collectCurrentState() {
        viewModelScope.launch(ioDispatcher) {
            dataStore.data.collect {
                _currentStateObject.value = it
                _visibleIndex.value = it.visibleIndex
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
        visibleIndex: Int = currentStateObject.value.visibleIndex
    ) {
        viewModelScope.launch(ioDispatcher) {
            val currentState = CurrentState(
                tag = tag,
                stationUuid = stationUuid,
                theme = theme,
                command = command,
                visibleIndex = visibleIndex
            )
            setSettings(settings = currentState)
            _visibleIndex.value = visibleIndex
            Log.d(TAG, "setSettings:$currentState")
        }
    }

    fun getTagsList(context: Context) {
        if (tagList.value.isEmpty()) {
            viewModelScope.launch(ioDispatcher) {
                _tagList.value = FilesHelper.getTags(context = context)
            }
        }
    }

    fun getSettings(): CurrentState = currentStateObject.value

    fun setUICommand(command: UICommands) {
        _uiCommand.value = command
    }

    private fun backUpFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            val favorites =
                Favorites(stationList = stationsRepository.fetchStationsByFavorites().map {
                    FavoriteStation(
                        uuid = UUID.randomUUID().toString(), stationuuid = it.stationuuid
                    )
                })
            if (favorites.stationList.isNotEmpty()) {
                try {
                    val jsonString = Json.encodeToString(
                        serializer = Favorites.serializer(), value = favorites
                    )
                    _backUpData.value = jsonString
                    setUICommand(UICommands.UI_COMMAND_BACKUP_FAVORITES)
                } catch (e: Exception) {
                    sendMessage(MessageType.ERROR, e.message.toString())
                    Log.e(TAG, e.toString())
                }
            } else {
                sendMessage(MessageType.ERROR, NO_DATA_TO_BACKUP)
            }
        }
    }

    fun restoreFavorites(favoritesJsonString: String) {
        viewModelScope.launch(Dispatchers.IO) {
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
                    sendMessage(MessageType.ERROR, NO_DATA_TO_RESTORE)
                }
            } catch (e: Exception) {
                sendMessage(MessageType.ERROR, e.message.toString())
                Log.e(TAG, e.toString())
            }
        }
    }

    private fun downloadFavIco(url: String) {
        if (url != "null" && url.isNotBlank()) {
            try {
                ImageHelper.downloadAsync(imageUrl = url, callback = favIcoDownloadCallback)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        } else {
            _primaryDynamicColor.value = 0
        }
    }

    fun setPrimaryDynamicColor(rgb: Int) {
        _primaryDynamicColor.value = rgb
    }

    private suspend fun setSettings(settings: CurrentState) {
        dataStore.updateData {
            settings
        }
    }

    override fun onCleared() {
        unsubscribe()
    }

    fun sendMessage(type: MessageType, message: String) {
        _message.value = SystemMessage.newInstance(
            id = UUID.randomUUID().toString(),
            type = type,
            message = message
        )
        Log.d(TAG, message)
    }

    private fun collectStates() {
        viewModelScope.launch {
            uiState.collect {
                Log.d(TAG, it.name)
            }
        }
        viewModelScope.launch {
            dataState.collect {
                Log.d(TAG, it.name)
            }
        }
    }

    companion object {
        const val TAG = "MAIN_VIEW_MODEL"

        private const val NO_DATA_TO_RESTORE = "No data to restore..."
        private const val NO_DATA_TO_BACKUP = "No data to backup..."

    }
}