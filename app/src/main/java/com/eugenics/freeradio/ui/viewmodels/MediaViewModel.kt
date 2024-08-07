package com.eugenics.freeradio.ui.viewmodels

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.ThumbRating
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionCommand
import androidx.palette.graphics.Palette
import com.eugenics.core.constants.SET_FAVORITES_COMMAND
import com.eugenics.core.enums.Commands
import com.eugenics.core.enums.Theme
import com.eugenics.core.model.CurrentState
import com.eugenics.core.model.NowPlayingStation
import com.eugenics.core.model.Station
import com.eugenics.core.model.StationsUiState
import com.eugenics.core.model.Tag
import com.eugenics.freeradio.util.ImageHelper
import com.eugenics.media_service.media3.data.ContentDataSource
import com.eugenics.ui.compose.screens.main.data.MainScreenStates
import com.eugenics.ui_core.data.enums.DataState
import com.eugenics.ui_core.data.enums.PlayBackState
import com.eugenics.ui_core.data.enums.UICommand
import com.eugenics.ui_core.data.model.UIMessage
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

private const val CHANNEL_CAPACITY = 10

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val mediaBrowserFuture: ListenableFuture<MediaBrowser>,
    private val dataStore: DataStore<CurrentState>
) : ViewModel() {

    private val ioDispatcher = Dispatchers.IO

    private var mediaBrowser: MediaBrowser? = null
    private var mediaBrowserInit: Boolean = false

    private val _tags: MutableStateFlow<List<Tag>> = MutableStateFlow(listOf())
    private val tags: StateFlow<List<Tag>> = _tags

    private val _currentUiState: MutableStateFlow<CurrentState> =
        MutableStateFlow(CurrentState.emptyInstance())
    val currentUiState: StateFlow<CurrentState> = _currentUiState

    private val _mainScreenStates: MutableStateFlow<MainScreenStates> = MutableStateFlow(
        MainScreenStates.emptyInstance()
    )
    val mainScreenStates: StateFlow<MainScreenStates> = _mainScreenStates

    private val settingsChannel: Channel<CurrentState> =
        Channel(capacity = CHANNEL_CAPACITY, onBufferOverflow = BufferOverflow.SUSPEND)

    private val _primaryDynamicColor: MutableStateFlow<Int> = MutableStateFlow(0)
    val primaryDynamicColor: StateFlow<Int> = _primaryDynamicColor

    private val nowPlayingStation: MutableStateFlow<NowPlayingStation> =
        MutableStateFlow(NowPlayingStation.emptyInstance())

    private val _uiCommand: MutableStateFlow<UICommand> =
        MutableStateFlow(UICommand.UI_COMMAND_IDL)
    val uiCommand: StateFlow<UICommand> = _uiCommand

    private val _savedData: MutableStateFlow<String> = MutableStateFlow("")
    val savedData: StateFlow<String> = _savedData

    init {
        Log.d(TAG, "MEDIA_VIEWMODEL_INIT")
        collectSettingsForWrite()
        collectCurrentState()
        collectNowPlayingStationUid()
    }

    override fun onCleared() {
        super.onCleared()
        settingsChannel.close()
    }

    private fun initialize() {
        Log.d(TAG, "MEDIABROWSER_INIT")
        mediaBrowserInit = true
        _mainScreenStates.update {
            mainScreenStates.value.copyObject(uiDataState = DataState.Loading)
        }
        sendMessage(
            UIMessage.TYPE_UI, messageInfoType = UIMessage.INFO_LOADING
        )

        mediaBrowserFuture.addListener({
            mediaBrowser = mediaBrowserFuture.get()
            mediaBrowser?.addListener(PlayerListener())
            getRootItem()
        }, MoreExecutors.directExecutor())
    }

    private fun getBrowserChildren(parentId: String, stationMediaItemId: String = "") {
        mediaBrowser?.let { browser ->
            val childrenFuture = browser.getChildren(parentId, 0, Int.MAX_VALUE, null)
            childrenFuture.addListener(
                {
                    childrenFuture.get().value?.let { items ->
                        Log.d(TAG, "GET_CHILDREN...")
                        _mainScreenStates.update { state ->
                            state.copyObject(stations = listOf())
                            state.copyObject(stations = items.map { item -> item.toStation() })
                        }
                        setUiDataState(DataState.Prepared)
                        browser.pause()
                        _mainScreenStates.update { state ->
                            state.copyObject(playBackState = PlayBackState.Pause)
                        }

                        browser.clearMediaItems()
                        browser.addMediaItems(items)
                        onItemClick(stationMediaItemId.ifBlank { items.first().mediaId }, false)
                    }
                }, MoreExecutors.directExecutor()
            )
        }
    }

    private fun getRootItem() {
        mediaBrowser?.let { mediaBrowser ->
            val rootItemFuture = mediaBrowser.getLibraryRoot(null)
            rootItemFuture.addListener({
                val rootItem = rootItemFuture.get()
                Log.d(TAG, rootItem.value?.mediaId.toString())
                getTags()
            }, MoreExecutors.directExecutor())
        }
    }

    fun getTagMediaItem(parentId: String, stationMediaItemId: String = "") {
        mediaBrowser?.let { browser ->
            Log.d(TAG, "GET_PARENT_ITEM:${parentId.ifBlank { tags.value.first().value }}")
            setSettings(tag = parentId)
            setUiDataState(DataState.Loading)
            val result = browser.getItem(parentId.ifBlank { tags.value.first().value })
            result.addListener({
                getBrowserChildren(
                    parentId.ifBlank { tags.value.first().value },
                    stationMediaItemId
                )
            }, MoreExecutors.directExecutor())
        }
    }

    private fun getTags() {
        mediaBrowser?.let { browser ->
            val tagsFuture = browser.getChildren(
                ContentDataSource.ROOT_ID, 0, Int.MAX_VALUE, null
            )
            tagsFuture.addListener(
                {
                    _tags.value = tagsFuture.get().value?.let { list ->
                        list.filter { item -> item.mediaMetadata.isPlayable == false }
                            .filter { item -> item.mediaId != ContentDataSource.FAVORITES_ID }
                            .filter { item -> item.mediaId != "All" }
                    }?.map { mediaItem ->
                        Tag(
                            name = mediaItem.mediaMetadata.title.toString(),
                            value = mediaItem.mediaId
                        )
                    } ?: listOf()
                    _mainScreenStates.update {
                        mainScreenStates.value.copyObject(tagList = tags.value)
                    }
                    getTagMediaItem(currentUiState.value.tag, currentUiState.value.stationUuid)
                }, MoreExecutors.directExecutor()
            )
        }
    }

    fun onItemClick(itemId: String?, playThenReady: Boolean) {
        mediaBrowser?.let { browser ->
            Log.d(TAG, "ON ITEM CLICK:$itemId")
            mainScreenStates.value.stations.find { it.stationuuid == itemId }?.let { station ->
                if (station.stationuuid == nowPlayingStation.value.stationUUID) {
                    if (browser.isPlaying) browser.pause() else browser.play()
                } else {
                    val mediaItemIndex = mainScreenStates.value.stations.indexOf(station)
                    if (mediaItemIndex >= 0) {
                        nowPlayingStation.update {
                            NowPlayingStation.newInstance(
                                name = station.name,
                                favicon = station.favicon,
                                nowPlayingTitle = "",
                                stationUUID = station.stationuuid,
                                description = "",
                                isFavorite = station.isFavorite == 1
                            )
                        }
                        browser.seekTo(mediaItemIndex, 0L)
                        browser.prepare()
                        browser.playWhenReady = playThenReady
                        setSettings(stationUuid = station.stationuuid)
                    }
                }
            }
        }
    }

    fun play() {
        mediaBrowser?.let { browser -> if (browser.isPlaying) browser.pause() else browser.play() }
    }

    fun seek(next: Boolean) {
        mediaBrowser?.let { browser ->
            if (next && browser.currentMediaItemIndex < browser.mediaItemCount)
                browser.seekTo(browser.currentMediaItemIndex + 1, 0L)
            else if (browser.currentMediaItemIndex > 0)
                browser.seekTo(browser.currentMediaItemIndex - 1, 0L)
        }
    }

    private inner class PlayerListener : Player.Listener {

        override fun onEvents(player: Player, events: Player.Events) {
            when {
                events.contains(Player.EVENT_MEDIA_METADATA_CHANGED) -> {
                    Log.d(TAG, "ON_EVENT_METADATA_CHANGED:${player.mediaMetadata.title}")
                    player.currentMediaItem?.let { item ->
                        nowPlayingStation.update {
                            NowPlayingStation.newInstance(
                                name = (player.mediaMetadata.displayTitle ?: "").toString(),
                                favicon = player.mediaMetadata.artworkUri.toString(),
                                nowPlayingTitle = (player.mediaMetadata.title ?: "").toString(),
                                stationUUID = item.mediaId,
                                description = player.mediaMetadata.description.toString(),
                                isFavorite = (player.mediaMetadata.userRating as ThumbRating).isThumbsUp
                            )
                        }
                    }
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _mainScreenStates.update { state ->
                state.copyObject(
                    playBackState =
                    if (isPlaying) PlayBackState.Playing else PlayBackState.Pause
                )
            }
        }

        override fun onPlayerErrorChanged(error: PlaybackException?) {
            Log.d(TAG, "PLAYER ERROR: $error")
            error?.message?.let { message ->
                nowPlayingStation.update { state ->
                    state.copy(nowPlayingTitle = message)
                }
            }
        }
    }

    fun setSettings(
        tag: String = currentUiState.value.tag,
        stationUuid: String = currentUiState.value.stationUuid,
        theme: Theme = currentUiState.value.theme,
        command: String = currentUiState.value.command,
        visibleIndex: Int = currentUiState.value.stationsVisibleIndex,
        lastStationsListUpdate: Long = currentUiState.value.lastStationsListUpdate,
        setName: String = "default"
    ) {
        viewModelScope.launch(ioDispatcher) {
            val newStateObject = CurrentState(
                tag = tag,
                stationUuid = stationUuid,
                theme = theme,
                command = command,
                stationsVisibleIndex = visibleIndex,
                lastStationsListUpdate = lastStationsListUpdate
            )
            if (newStateObject != currentUiState.value) {
                settingsChannel.send(newStateObject)
                Log.d(MainViewModel.TAG, "send settings with settings name: $setName")
            }
        }
    }

    private fun collectSettingsForWrite() {
        viewModelScope.launch(Dispatchers.Default) {
            settingsChannel.consumeEach { currentState ->
                Log.d(MainViewModel.TAG, "WRITE_SETTINGS: $currentState")
                dataStore.updateData { currentState }
                setStationUiState(currentState.stationUuid, currentState.stationsVisibleIndex)
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

    private fun setPrimaryDynamicColor(rgb: Int) {
        _primaryDynamicColor.update { rgb }
        Log.d(TAG, "RGB COLOR UPDATED: $rgb")
    }

    fun sendMessage(messageType: Int, messageInfoType: Int, messageText: String = "") {
        _mainScreenStates.update { state ->
            state.copyObject(
                message = UIMessage.newInstance(
                    id = UUID.randomUUID().toString(),
                    messageType = messageType,
                    messageInfoType = messageInfoType,
                    messageText = messageText
                )
            )
        }
        Log.d(MainViewModel.TAG, messageInfoType.toString())
    }

    private fun collectNowPlayingStationUid() {
        viewModelScope.launch {
            nowPlayingStation.collect { station ->
                _mainScreenStates.update {
                    mainScreenStates.value.copyObject(nowPlaying = station)
                }
                setSettings(stationUuid = station.stationUUID, setName = "collectNowPlaying")
                getDynamicColor(url = station.favicon)
            }
        }
    }

    private fun collectCurrentState() {
        viewModelScope.launch(ioDispatcher) {
            dataStore.data.collect { collectedState ->
                _currentUiState.update {
                    collectedState
                }
                setStationUiState(collectedState.stationUuid, collectedState.stationsVisibleIndex)
                if (!mediaBrowserInit) {
                    withContext(Dispatchers.Main) { initialize() }
                }
                Log.d(TAG, "COLLECTED CURRENT_STATED: ${currentUiState.value}")
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

    private fun setUiDataState(dataState: DataState) {
        _mainScreenStates.update { state ->
            state.copyObject(uiDataState = dataState)
        }
    }

    fun setUICommand(command: UICommand) {
        _uiCommand.value = command
    }

    fun sendCommand(command: String, bundle: Bundle?) {
        when (command) {
            Commands.STATIONS_COMMAND.name -> {}
            SET_FAVORITES_COMMAND -> {
                mediaBrowser?.let { browser ->
                    bundle?.let {
                        val resultFuture = browser.sendCustomCommand(
                            SessionCommand(SET_FAVORITES_COMMAND, it), it
                        )
                        resultFuture.addListener({
                            val result = resultFuture.get()
                        }, MoreExecutors.directExecutor())
                    }

                }
            }

            else -> {}
        }
    }

    companion object {
        private const val TAG = "MEDIA_VIEW_MODEL"
    }
}

fun MediaItem.toStation(): Station = (if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
    this.mediaMetadata.extras?.getParcelable(ContentDataSource.MEDIA_ITEM_STATION_EXTRAS_KEY)
} else {
    this.mediaMetadata.extras?.getParcelable(
        ContentDataSource.MEDIA_ITEM_STATION_EXTRAS_KEY, Station::class.java
    )
}) ?: Station.newInstance()