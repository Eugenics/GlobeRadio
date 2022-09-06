package com.eugenics.freeradio.ui.viewmodels

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import com.eugenics.freeradio.domain.model.Station
import com.eugenics.media_service.domain.model.PlayerMediaItem
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val mediaServiceConnection: FreeRadioMediaServiceConnection
) : ViewModel() {

    private val _stations = mutableStateListOf<Station>()
    val stations: List<Station> = _stations

    private val _state = mediaServiceConnection.playbackState
    val state: StateFlow<PlaybackStateCompat> = _state

    var itemIndex: Int = 0
    val rootId = "/"

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {

        override fun onChildrenLoaded(
            parentId: String,
            children: List<MediaBrowserCompat.MediaItem>
        ) {
            for (child in children) {
                val extras = child.description.extras?.getParcelable<PlayerMediaItem>("STATION")
                extras?.let {
                    _stations.add(
                        Station(
                            stationuuid = extras.uuid,
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
        }
    }

    init {
        mediaServiceConnection.subscribe(rootId, subscriptionCallback)
    }


    fun play() {
        mediaServiceConnection.transportControls.play()
        _state.value = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0f)
            .build()
    }

    fun pause() {
        mediaServiceConnection.transportControls.pause()
        _state.value = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PAUSED, 0, 0f)
            .build()
    }
}