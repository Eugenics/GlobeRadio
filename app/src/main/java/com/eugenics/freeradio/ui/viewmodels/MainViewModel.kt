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
import com.eugenics.core.model.CurrentState
import com.eugenics.core.enums.Theme
import com.eugenics.core.model.FavoriteStation
import com.eugenics.core.model.Favorites
import com.eugenics.core.model.StationsUiState
import com.eugenics.data.interfaces.IStationsRepository
import com.eugenics.freeradio.ui.util.IMediaControls
import com.eugenics.freeradio.ui.util.MediaControlsBuilder
import com.eugenics.ui_core.data.enums.DataState
import com.eugenics.freeradio.util.ImageHelper
import com.eugenics.ui_core.data.enums.UICommand
import com.eugenics.freeradio.util.IOHelper
import com.eugenics.freeradio.util.PlaylistHelper
import com.eugenics.freeradio.util.STATIONS_JSON_NAME
import com.eugenics.freeradio.util.STATIONS_ZIP_ASSET_NAME
import com.eugenics.freeradio.util.TAGS_ASSET_NAME
import com.eugenics.freeradio.util.data.StationPojo
import com.eugenics.freeradio.util.data.TagsPojo
import com.eugenics.freeradio.util.data.mapToModel
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection
import com.eugenics.ui.compose.screens.main.data.MainScreenStates
import com.eugenics.ui_core.data.enums.PlayBackState
import com.eugenics.ui_core.data.model.UIMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import java.util.UUID
import javax.inject.Inject
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Paths

private const val CHANNEL_CAPACITY = 10
private const val ROOT_ID = "/"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mediaServiceConnection: FreeRadioMediaServiceConnection,
    private val stationsRepository: IStationsRepository,
    private val dataStore: DataStore<CurrentState>
) : ViewModel() {
    private val ioDispatcher = Dispatchers.IO
    private val defaultDispatcher = Dispatchers.Default

    private val _mainScreenStates: MutableStateFlow<MainScreenStates> = MutableStateFlow(
        MainScreenStates.emptyInstance()
    )
    val mainScreenStates: StateFlow<MainScreenStates> = _mainScreenStates

    private val _currentState: MutableStateFlow<CurrentState> =
        MutableStateFlow(CurrentState.emptyInstance())
    val currentState: StateFlow<CurrentState> = _currentState

    private val _uiCommand: MutableStateFlow<UICommand> = MutableStateFlow(UICommand.UI_COMMAND_IDL)
    val uiCommands: StateFlow<UICommand> = _uiCommand

    private val _savedData: MutableStateFlow<String> = MutableStateFlow("")
    val savedData: StateFlow<String> = _savedData

    private val _primaryDynamicColor: MutableStateFlow<Int> = MutableStateFlow(0)
    val primaryDynamicColor: StateFlow<Int> = _primaryDynamicColor

    private val settingsChannel: Channel<CurrentState> =
        Channel(capacity = CHANNEL_CAPACITY, onBufferOverflow = BufferOverflow.SUSPEND)

    private val nowPlayingMetaData = mediaServiceConnection.nowPlayingItem

    private var currentMediaId: String = ""

    private val mediaSourceState = mediaServiceConnection.mediaSourceState

    private var _viewModelState: Boolean = false
    val viewModelState: Boolean
        get() = _viewModelState

    private val mediaControls: IMediaControls = MediaControlsBuilder()
        .setMediaServiceConnection(mediaServiceConnection = mediaServiceConnection)
        .build()

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String, children: List<MediaBrowserCompat.MediaItem>
        ) {
            val stationServiceContent = mutableListOf<Station>()

            for (child in children) {
                val extras = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    @Suppress("DEPRECATION") child.description.extras?.getParcelable("STATION")
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
            _mainScreenStates.update {
                mainScreenStates.value.copyObject(stations = stationServiceContent)
            }
        }
    }

    init {
        collectSettingsForWrite()
        collectCurrentState()
    }

    fun start(context: Context) {
        Log.d(TAG, "$TAG START...")
        getTagsList(
            tagsJsonPath = "${context.cacheDir.absolutePath}/$TAGS_ASSET_NAME",
            context = context
        )
        collectServiceConnection()
        collectMediaSourceState(context)
        collectNowPlaying()
        collectServicePlaybackState()
        _viewModelState = true
    }

    private fun unsubscribe() {
        mediaServiceConnection.unsubscribe(ROOT_ID)
    }

    override fun onCleared() {
        Log.d(TAG, "unsubscribe")
        unsubscribe()
        settingsChannel.close()
        Log.d(TAG, "close settings channel")
    }

    /**
     * MediaService commands
     */
    fun onPlayClick(mediaId: String? = currentMediaId) {

        when (mediaId ?: currentMediaId) {
            currentMediaId -> if (mainScreenStates.value.playBackState == PlayBackState.Playing) {
                mediaControls.pause()
            } else {
                mediaControls.play(mediaId ?: currentMediaId)
            }

            else -> {
                mediaId?.let { mediaControls.play(it) }
            }
        }
    }

    fun onNextClick() {
        mediaControls.next()
    }

    fun onPrevClick() {
        mediaControls.prev()
    }

    fun onSearch(query: String) {
        setSettings(visibleIndex = 0, setName = "onVisibleIndexChanged")
        _mainScreenStates.update {
            mainScreenStates.value.clearStationsList()
        }
        mediaControls.search(query)
    }

    fun sendCommand(command: String, extras: Bundle? = null) {
        Log.d(TAG, "Send command: $command")
        when (command) {
            UICommand.UI_COMMAND_BACKUP_FAVORITES.name -> backUpFavorites()
            UICommand.UI_COMMAND_RESTORE_FAVORITES.name ->
                _uiCommand.value = UICommand.UI_COMMAND_RESTORE_FAVORITES


            UICommand.UI_EXPORT_FAVORITES_PLAYLIST.name -> exportFavoritesAsPlayList()

            in enumValues<Commands>().map { it.name }.toList() -> {
                setSettings(
                    command = command,
                    tag = extras?.getString("TAG") ?: "",
                    setName = "sendCommandTag"
                )
                if (command != Commands.SET_FAVORITES_COMMAND.name) {
                    _mainScreenStates.update {
                        mainScreenStates.value.clearStationsList()
                    }
                    setSettings(visibleIndex = 0, setName = "onVisibleIndexChanged")
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

    /**
     * Collectors
     */
    private fun collectServiceConnection() {
        viewModelScope.launch {
            mediaServiceConnection.isConnected.collect {
                if (it) {
                    mediaServiceConnection.subscribe(ROOT_ID, subscriptionCallback)
                    this.coroutineContext.cancel()
                }
            }
        }
    }

    private fun collectNowPlaying() {
        viewModelScope.launch(defaultDispatcher) {
            nowPlayingMetaData.collect { metaData ->
                _mainScreenStates.update {
                    mainScreenStates.value.copyObject(
                        nowPlaying = NowPlayingStation.newInstance(
                            name = metaData.getString(MediaMetadataCompat.METADATA_KEY_TITLE) ?: "",
                            favicon = metaData.description.iconUri.toString(),
                            nowPlayingTitle = metaData.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE)
                                ?: "",
                            stationUUID = metaData.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                                ?: "",
                            description = metaData.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION)
                                ?: "",
                            isFavorite = !metaData.getString("favorite").isNullOrBlank()
                        )
                    )
                }
                currentMediaId = mainScreenStates.value.nowPlaying.stationUUID
                setSettings(stationUuid = currentMediaId, setName = "collectNowPlaying")
                getDynamicColor(url = mainScreenStates.value.nowPlaying.favicon)
                Log.d(TAG, "MAIN_SCREEN_STATES: ${mainScreenStates.value}")
            }
        }
    }

    private fun collectCurrentState() {
        viewModelScope.launch(ioDispatcher) {
            dataStore.data.collect { collectedState ->
                _currentState.update {
                    collectedState
                }
                setStationUiState(collectedState.stationUuid, collectedState.stationsVisibleIndex)
                Log.d(TAG, "COLLECTED CURRENT_STATED: ${_currentState.value}")
            }
        }
    }

    private fun collectMediaSourceState(context: Context) {
        Log.d(TAG, "COLLECT MEDIA_SOURCE_STATE...")
        viewModelScope.launch {
            mediaSourceState.collect { state ->
                Log.d(TAG, "COLLECTED MEDIA_SOURCE_STATE:$state")
                when (state) {
                    MediaSourceState.STATE_FIRST_INIT.value -> {
                        Log.d(TAG, "MEDIA_SOURCE_STATE STATE_FIRST_INIT")
                        sendMessage(
                            UIMessage.TYPE_UI, messageInfoType = UIMessage.INFO_FIRST_INIT
                        )
                        uploadStationsFullList(context)
                        withContext(Dispatchers.Main) {
                            sendCommand(command = Commands.STATIONS_COMMAND.name)
                        }
                    }

                    MediaSourceState.STATE_CREATED.value ->
                        Log.d(TAG, "MEDIA_SOURCE_STATE STATE_CREATED")

                    MediaSourceState.STATE_ERROR.value ->
                        _mainScreenStates.update {
                            mainScreenStates.value.copyObject(uiDataState = DataState.Error)
                        }


                    MediaSourceState.STATE_INITIALIZING.value -> {
                        Log.d(TAG, "MEDIA_SOURCE_STATE STATE_INITIALIZING")
                        _mainScreenStates.update {
                            mainScreenStates.value.copyObject(uiDataState = DataState.Loading)
                        }
                        sendMessage(
                            UIMessage.TYPE_UI, messageInfoType = UIMessage.INFO_LOADING
                        )
                    }

                    MediaSourceState.STATE_INITIALIZED.value -> {
                        Log.d(TAG, "MEDIA_SOURCE_STATE STATE_INITIALIZED")
                        _mainScreenStates.update {
                            mainScreenStates.value.copyObject(uiDataState = DataState.Prepared)
                        }
                    }
                }
            }
        }
    }

    private fun collectServicePlaybackState() {
        viewModelScope.launch(Dispatchers.IO) {
            mediaServiceConnection.playbackState.collect { stateCompat ->
                when (stateCompat.state) {
                    PlaybackStateCompat.STATE_PLAYING ->
                        _mainScreenStates.update {
                            mainScreenStates.value.copyObject(playBackState = PlayBackState.Playing)
                        }

                    else -> _mainScreenStates.update {
                        mainScreenStates.value.copyObject(playBackState = PlayBackState.Pause)
                    }
                }
            }
        }
    }

    private fun collectSettingsForWrite() {
        viewModelScope.launch(Dispatchers.Default) {
            settingsChannel.consumeEach { currentState ->
                Log.d(TAG, "WRITE_SETTINGS: $currentState")
                dataStore.updateData { currentState }
                setStationUiState(currentState.stationUuid, currentState.stationsVisibleIndex)
            }
        }
    }

    /**
     * Functions
     */

    fun setSettings(
        tag: String = currentState.value.tag,
        stationUuid: String = currentState.value.stationUuid,
        theme: Theme = currentState.value.theme,
        command: String = currentState.value.command,
        visibleIndex: Int = currentState.value.stationsVisibleIndex,
        lastStationsListUpdate: Long = currentState.value.lastStationsListUpdate,
        setName: String = "default"
    ) {
        viewModelScope.launch(defaultDispatcher) {
            val newStateObject = CurrentState(
                tag = tag,
                stationUuid = stationUuid,
                theme = theme,
                command = command,
                stationsVisibleIndex = visibleIndex,
                lastStationsListUpdate = lastStationsListUpdate
            )
            if (newStateObject != currentState.value) {
                settingsChannel.send(newStateObject)
                Log.d(TAG, "send settings with settings name: $setName")
            }
        }
    }

    private fun setStationUiState(stationUuid: String, visibleIndex: Int) {
        val newStationUiState = StationsUiState(stationUuid, visibleIndex)
        if (mainScreenStates.value.stationsUiState != newStationUiState) {
            _mainScreenStates.update {
                mainScreenStates.value.copyObject(stationsUiState = newStationUiState)
            }
        }
    }

    private fun getTagsList(tagsJsonPath: String, context: Context) {
        if (mainScreenStates.value.tagList.isEmpty()) {
            viewModelScope.launch(ioDispatcher) {
                var cacheEnable = true
                if (!Files.exists(Paths.get(tagsJsonPath))) {
                    cacheEnable = IOHelper.copyAssetToCache(TAGS_ASSET_NAME, context)
                }
                if (cacheEnable) {
                    _mainScreenStates.update {
                        mainScreenStates.value.copyObject(
                            tagList = IOHelper.parseJsonToList(
                                filePath = tagsJsonPath,
                                TagsPojo::class.java
                            ).map { it.mapToModel() }
                        )
                    }
                }
            }
        }
    }

    fun setUICommand(command: UICommand) {
        _uiCommand.value = command
    }

    private fun exportFavoritesAsPlayList() {
        viewModelScope.launch(defaultDispatcher) {
            stationsRepository.fetchStationsByFavorites().apply {
                if (this.isNotEmpty()) {
                    _savedData.value = PlaylistHelper.convertStationsToPlaylist(this)
                    setUICommand(UICommand.UI_EXPORT_FAVORITES_PLAYLIST)
                } else {
                    sendMessage(UIMessage.TYPE_INFO, UIMessage.INFO_NO_DATA_TO_LOAD)
                }
            }
        }
    }

    private fun backUpFavorites() {
        viewModelScope.launch(ioDispatcher) {
            stationsRepository.fetchStationsByFavorites().map { station ->
                FavoriteStation(
                    uuid = UUID.randomUUID().toString(), stationuuid = station.stationuuid
                )
            }.also { favStations ->
                if (favStations.isNotEmpty()) {
                    try {
                        _savedData.value = Json.encodeToString(
                            serializer = Favorites.serializer(),
                            value = Favorites.newInstance(favStations)
                        )
                        setUICommand(UICommand.UI_COMMAND_BACKUP_FAVORITES)
                    } catch (e: SerializationException) {
                        sendMessage(
                            UIMessage.TYPE_ERROR, UIMessage.INFO_ERROR, e.message.toString()
                        )
                        Log.e(TAG, e.toString())
                    }
                } else {
                    sendMessage(UIMessage.TYPE_INFO, UIMessage.INFO_NO_DATA_TO_LOAD)
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
                    sendMessage(UIMessage.TYPE_ERROR, UIMessage.INFO_NO_DATA_TO_SAVE)
                }
            } catch (e: Exception) {
                sendMessage(UIMessage.TYPE_ERROR, UIMessage.INFO_ERROR, e.message.toString())
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

    private suspend fun uploadStationsFullList(context: Context) {
        Log.d(TAG, "Start stations upload...")
        var cacheEnable = true
        if (!Files.exists(Paths.get("${context.cacheDir.absolutePath}/$STATIONS_JSON_NAME"))) {
            Log.d(TAG, "Cache copy...")
            cacheEnable = IOHelper.copyAssetToCache(STATIONS_ZIP_ASSET_NAME, context)
            if (cacheEnable) {
                Log.d(TAG, "Start stations unzip...")
                cacheEnable = IOHelper.unzipStationsFullList(
                    zipFrom = "${context.cacheDir.absolutePath}/$STATIONS_ZIP_ASSET_NAME",
                    pathTo = "${context.cacheDir.absolutePath}/$STATIONS_JSON_NAME"
                )
            }
        }

        if (cacheEnable) {
            Log.d(TAG, "Start stations upload to db...")
            val jsonFilePath = "${context.cacheDir.absolutePath}/$STATIONS_JSON_NAME"
            val stations = IOHelper.parseJsonToList(
                filePath = jsonFilePath,
                StationPojo::class.java
            )
                .map { it.mapToModel() }
            stationsRepository.reloadStations(stations = stations)
            Log.d(TAG, "Send command for reload stations...")
        }
    }

    fun sendMessage(messageType: Int, messageInfoType: Int, messageText: String = "") {
        _mainScreenStates.update {
            mainScreenStates.value.copyObject(
                message = UIMessage.newInstance(
                    id = UUID.randomUUID().toString(),
                    messageType = messageType,
                    messageInfoType = messageInfoType,
                    messageText = messageText
                )
            )
        }
        Log.d(TAG, messageInfoType.toString())
    }

    fun setPrimaryDynamicColor(rgb: Int) {
        _primaryDynamicColor.update { rgb }
        Log.d(TAG, "RGB COLOR UPDATED: $rgb")
    }

    companion object {
        const val TAG = "MAIN_VIEW_MODEL"
    }
}