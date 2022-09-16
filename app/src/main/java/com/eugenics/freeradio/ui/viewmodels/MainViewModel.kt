package com.eugenics.freeradio.ui.viewmodels

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eugenics.freeradio.domain.model.Station
import com.eugenics.media_service.domain.model.PlayerMediaItem
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection.Companion.FAVORITES_COMMAND
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection.Companion.SET_FAVORITES_COMMAND
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection.Companion.STATIONS_COMMAND
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mediaServiceConnection: FreeRadioMediaServiceConnection
) : ViewModel() {

    private val _uiState: MutableStateFlow<Int> = MutableStateFlow(UI_STATE_IDL)
    val uiState: StateFlow<Int> = _uiState

    private val _stations: MutableStateFlow<List<Station>> = MutableStateFlow(mutableListOf())
    val stations: StateFlow<List<Station>> = _stations

    private val _state = mediaServiceConnection.playbackState
    val state: StateFlow<PlaybackStateCompat> = _state

    private val nowPlayingMetaData = mediaServiceConnection.nowPlaying

    val nowPlaying = MutableStateFlow(Station())

    private val ioDispatcher = Dispatchers.IO

    private var currentMediaId: String = ""
    private val rootId = "/"

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: List<MediaBrowserCompat.MediaItem>
        ) {
            if (children.isEmpty() && _uiState.value == UI_STATE_LOADING) {
                _uiState.value = UI_STATE_LOADING
            } else {
                val stationServiceContent = mutableListOf<Station>()
                for (child in children) {
                    val extras = child.description.extras?.getParcelable<PlayerMediaItem>("STATION")
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

                if (_stations.value.isEmpty()) {
                    _uiState.value = UI_STATE_EMPTY
                } else {
                    _uiState.value = UI_STATE_READY
                }
            }
        }
    }

    init {
        collectNowPlaying()
        viewModelScope.launch {
            mediaServiceConnection.isConnected.collect {
                if (it) {
                    mediaServiceConnection.subscribe(rootId, subscriptionCallback)
                    _uiState.value = UI_STATE_LOADING
                    return@collect
                }
            }
        }
    }

    fun onItemClick(mediaId: String) {
        if (mediaId == currentMediaId) {
            when (state.value.state) {
                PlaybackStateCompat.STATE_PLAYING -> pause()
                else -> play()
            }
        } else {
            Log.d("PLAY_FROM_MEDIA_ID_CLICKED", mediaId)
            mediaServiceConnection.transportControls.playFromMediaId(mediaId, null)
            _state.value = STATE_PLAYING
            currentMediaId = mediaId
        }
    }


    fun play() {
        mediaServiceConnection.transportControls.play()
        _state.value = STATE_PLAYING
    }

    fun pause() {
        mediaServiceConnection.transportControls.pause()
        _state.value = STATE_PAUSE
    }

    fun search(query: String) {
        _uiState.value = UI_STATE_REFRESH
        mediaServiceConnection.transportControls.playFromSearch(query, null)
    }

    fun sendCommand(command: String, extras: Bundle? = null) {
        if (command in listOf(STATIONS_COMMAND, FAVORITES_COMMAND)) {
            _uiState.value = UI_STATE_REFRESH
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
                        bundle?.getString(SET_FAVORITES_COMMAND) ?: "Command: $command error"
                    )
                }
            }
        )
    }

    private fun collectNowPlaying() {
        viewModelScope.launch(ioDispatcher) {
            nowPlayingMetaData.collect { metaData ->
                val station = Station(
                    name = metaData.description.title.toString(),
                    favicon = metaData.description.iconUri.toString()
                )
                nowPlaying.value = station
            }
        }
    }

    companion object {
        const val TAG = "SEARCH_VIEW_MODEL"

        const val UI_STATE_LOADING = 0
        const val UI_STATE_EMPTY = 1
        const val UI_STATE_READY = 2
        const val UI_STATE_IDL = 3
        const val UI_STATE_REFRESH = 4

        val STATE_PLAYING = stateBuilder(state = PlaybackStateCompat.STATE_PLAYING)
        val STATE_PAUSE = stateBuilder(state = PlaybackStateCompat.STATE_PAUSED)

        private fun stateBuilder(state: Int): PlaybackStateCompat =
            PlaybackStateCompat.Builder()
                .setState(state, 0, 0f)
                .build()
    }
}