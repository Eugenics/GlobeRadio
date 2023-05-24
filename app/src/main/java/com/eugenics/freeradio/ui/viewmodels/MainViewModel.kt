package com.eugenics.freeradio.ui.viewmodels

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.eugenics.core.enums.MediaSourceState
import com.eugenics.core.enums.Commands
import com.eugenics.core.model.NowPlayingStation
import com.eugenics.core.model.PlayerMediaItem
import com.eugenics.core.model.Station
import com.eugenics.core.model.Tag
import com.eugenics.data.interfaces.repository.IRepository
import com.eugenics.core.model.CurrentState
import com.eugenics.core.enums.Theme
import com.eugenics.core.model.FavoriteStation
import com.eugenics.core.model.Favorites
import com.eugenics.data.data.util.convertToFavoritesTmpDaoObject
import com.eugenics.freeradio.ui.util.ImageDownloadHelper
import com.eugenics.freeradio.ui.util.UICommands
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
    private val repository: IRepository,
    private val imageDownloadHelper: ImageDownloadHelper
) : ViewModel() {

    private val _uiState: MutableStateFlow<Int> = MutableStateFlow(UI_STATE_SPLASH)
    val uiState: StateFlow<Int> = _uiState

    private val _stations: MutableStateFlow<List<Station>> = MutableStateFlow(mutableListOf())
    val stations: StateFlow<List<Station>> = _stations

    private val _playBackState = mediaServiceConnection.playbackState
    val playBackState: StateFlow<PlaybackStateCompat> = _playBackState

    private val _settings: MutableStateFlow<CurrentState> =
        MutableStateFlow(CurrentState.getDefaultValueInstance())
    val settings: StateFlow<CurrentState> = _settings

    private val nowPlayingMetaData = mediaServiceConnection.nowPlayingItem
    val nowPlaying = MutableStateFlow(NowPlayingStation.emptyInstance())

    private val ioDispatcher = Dispatchers.IO

    private var currentMediaId: String = ""
    private val rootId = "/"

    private var _tagList: MutableStateFlow<List<Tag>> = MutableStateFlow(listOf())
    val tagList: StateFlow<List<Tag>> = _tagList

    private val _backUpData: MutableStateFlow<String> = MutableStateFlow("")
    val backUpData: StateFlow<String> = _backUpData

    private val _message: MutableStateFlow<String> = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private val _uiCommand: MutableStateFlow<UICommands> =
        MutableStateFlow(UICommands.UI_COMMAND_IDL)
    val uiCommands: StateFlow<UICommands> = _uiCommand

    private val _primaryDynamicColor: MutableStateFlow<Int> = MutableStateFlow(0)
    val primaryDynamicColor: StateFlow<Int> = _primaryDynamicColor

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: List<MediaBrowserCompat.MediaItem>
        ) {
            val stationServiceContent = mutableListOf<Station>()

            for (child in children) {
                val extras =
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        child.description.extras?.getParcelable("STATION")
                    } else {
                        child.description.extras?.getParcelable(
                            "STATION",
                            PlayerMediaItem::class.java
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
            _message.value = e.message.toString()
            Log.e(TAG, e.toString())
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                response.body?.let { responseBody ->
                    val bitmap = BitmapFactory.decodeStream(responseBody.byteStream())
                    Palette.from(bitmap)
                        .generate()
                        .dominantSwatch?.let {
                            _primaryDynamicColor.value = it.rgb
                        }
                }
            }
        }
    }

    init {
        collectMediaSourceState()
        collectNowPlaying()
        collectSettings()
        collectServiceConnection()
        getTagsList()
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
            when (playBackState.value.state) {
                PlaybackStateCompat.STATE_PLAYING -> pause()
                else -> play()
            }
        } else {
            Log.d(TAG, "PLAY_FROM_MEDIA_ID_CLICKED:$mediaId")
            mediaServiceConnection.transportControls.playFromMediaId(mediaId, null)
            _playBackState.value = STATE_PLAYING
            setSettings(stationUuid = mediaId)
        }
    }

    fun onVisibleIndexChanged(index: Int) {
        Log.d(TAG, "onVisibleIndexChanged:$index")
        setSettings(visibleIndex = index)
    }

    fun play() {
        mediaServiceConnection.transportControls.play()
        _playBackState.value = STATE_PLAYING
    }


    fun pause() {
        mediaServiceConnection.transportControls.pause()
        _playBackState.value = STATE_PAUSE
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
                    command = command,
                    tag = extras?.getString("TAG") ?: ""
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
                    }
                )
            }
        }
    }

    private fun collectNowPlaying() {
        viewModelScope.launch(ioDispatcher) {
            nowPlayingMetaData.collect { metaData ->
                nowPlaying.value = NowPlayingStation.newInstance(
                    name = metaData.getString(MediaMetadataCompat.METADATA_KEY_TITLE) ?: "",
                    favicon = metaData.description.iconUri.toString(),
                    nowPlayingTitle =
                    metaData.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE) ?: "",
                    stationUUID =
                    metaData.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) ?: "",
                    description =
                    metaData.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION) ?: ""
                )
                currentMediaId = nowPlaying.value.stationUUID

                downloadFavIco(url = nowPlaying.value.favicon)
            }
        }
    }

    private fun collectSettings() {
        viewModelScope.launch(ioDispatcher) {
            repository.getSettings().collect {
                _settings.value = it
            }
        }
    }

    private fun collectMediaSourceState() {
        viewModelScope.launch(Dispatchers.IO) {
            mediaServiceConnection.mediaSourceState.collect { state ->
                when (state) {
                    MediaSourceState.STATE_FIRST_INIT.value -> _uiState.value = UI_STATE_SPLASH
                    MediaSourceState.STATE_CREATED.value -> _uiState.value = UI_STATE_SPLASH
                    MediaSourceState.STATE_ERROR.value -> _uiState.value = UI_STATE_ERROR
                    MediaSourceState.STATE_INITIALIZING.value -> _uiState.value =
                        UI_STATE_UPDATE_DATA

                    MediaSourceState.STATE_INITIALIZED.value -> _uiState.value = UI_STATE_MAIN
                }
            }
        }
    }

    fun setSettings(
        tag: String = settings.value.tag,
        stationUuid: String = settings.value.stationUuid,
        theme: Theme = settings.value.theme,
        command: String = settings.value.command,
        visibleIndex: Int = settings.value.visibleIndex
    ) {
        viewModelScope.launch(ioDispatcher) {
            val currentState = CurrentState(
                tag = tag,
                stationUuid = stationUuid,
                theme = theme,
                command = command,
                visibleIndex = visibleIndex
            )
            repository.setSettings(settings = currentState)
            Log.d(TAG, "setSettings:$currentState")
        }
    }

    private fun getTagsList() {
        viewModelScope.launch(ioDispatcher) {
            _tagList.value = repository.getTags()
        }
    }

    fun clearMessage() {
        _message.value = ""
    }

    fun getSettings(): CurrentState = settings.value

    fun setUICommand(command: UICommands) {
        _uiCommand.value = command
    }

    private fun backUpFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            val favorites = Favorites(
                stationList = repository.fetchStationsByFavorites()
                    .map { it.convertToModel() }
                    .map {
                        FavoriteStation(
                            uuid = UUID.randomUUID().toString(),
                            stationuuid = it.stationuuid
                        )
                    }
            )
            if (favorites.stationList.isNotEmpty()) {
                try {
                    val jsonString = Json.encodeToString(
                        serializer = Favorites.serializer(),
                        value = favorites
                    )
                    _backUpData.value = jsonString
                    setUICommand(UICommands.UI_COMMAND_BACKUP_FAVORITES)
                } catch (e: Exception) {
                    _message.value = e.message.toString()
                    Log.e(TAG, e.toString())
                }
            } else {
                _message.emit("No data to backup...")
            }
        }
    }

    fun restoreFavorites(favoritesJsonString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (favoritesJsonString.isNotBlank()) {
                    val favorites = Json.decodeFromString(
                        Favorites.serializer(),
                        favoritesJsonString
                    )
                    repository.restoreFavorites(
                        favorites = favorites.stationList.map {
                            it.convertToFavoritesTmpDaoObject()
                        }
                    )
                    withContext(Dispatchers.Main) {
                        sendCommand(command = Commands.FAVORITES_COMMAND.name, null)
                    }
                } else {
                    _message.value = "No data to restore..."
                }
            } catch (e: Exception) {
                _message.value = e.message.toString()
                Log.e(TAG, e.toString())
            }
        }
    }

    private fun downloadFavIco(url: String) {
        if (url != "null" && url.isNotBlank()) {
            try {
                imageDownloadHelper
                    .downloadAsync(imageUrl = url, callback = favIcoDownloadCallback)
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

    companion object {
        const val TAG = "SEARCH_VIEW_MODEL"

        const val UI_STATE_SPLASH = 10
        const val UI_STATE_UPDATE_DATA = 11
        const val UI_STATE_ERROR = 12
        const val UI_STATE_MAIN = 13

        val STATE_PLAYING = stateBuilder(state = PlaybackStateCompat.STATE_PLAYING)
        val STATE_PAUSE = stateBuilder(state = PlaybackStateCompat.STATE_PAUSED)

        private fun stateBuilder(state: Int): PlaybackStateCompat =
            PlaybackStateCompat.Builder()
                .setState(state, 0, 0f)
                .build()
    }
}