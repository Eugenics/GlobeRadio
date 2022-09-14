package com.eugenics.freeradio.ui.viewmodels

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eugenics.freeradio.domain.model.Station
import com.eugenics.media_service.domain.model.PlayerMediaItem
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private var currentMediaId: String = ""
    private val rootId = "/"

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: List<MediaBrowserCompat.MediaItem>
        ) {
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
                            changeuuid = ""
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

    init {
        viewModelScope.launch {
            mediaServiceConnection.isConnected.collect {
                if (it) {
                    mediaServiceConnection.subscribe(rootId, subscriptionCallback)
                    _uiState.value = UI_STATE_LOADING
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
        mediaServiceConnection.transportControls.playFromSearch(query, null)
    }

    fun sendCommand(command: String, extras: String? = null) {
        val commandParameters = Bundle()
        commandParameters.putString(FreeRadioMediaServiceConnection.COMMAND_EXTRAS_KEY, extras)
        mediaServiceConnection.sendCommand(
            command = command,
            parameters = commandParameters,
            resultCallback = { _, _ -> }
        )
    }

    companion object {
        const val TAG = "SEARCH_VIEW_MODEL"

        const val UI_STATE_LOADING = 0
        const val UI_STATE_EMPTY = 1
        const val UI_STATE_READY = 2
        const val UI_STATE_IDL = 3

        val STATE_PLAYING = stateBuilder(state = PlaybackStateCompat.STATE_PLAYING)
        val STATE_PAUSE = stateBuilder(state = PlaybackStateCompat.STATE_PAUSED)

        private fun stateBuilder(state: Int): PlaybackStateCompat =
            PlaybackStateCompat.Builder()
                .setState(state, 0, 0f)
                .build()
    }
}