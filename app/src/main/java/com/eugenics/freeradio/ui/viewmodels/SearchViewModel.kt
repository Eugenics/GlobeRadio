package com.eugenics.freeradio.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.eugenics.freeradio.domain.core.Player
import com.eugenics.freeradio.domain.model.Station
import com.eugenics.freeradio.domain.usecases.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val useCase: UseCase,
    val player: Player
) : ViewModel() {

    private val _stations = mutableStateListOf<Station>()
    val stations: List<Station> = _stations

    private val _state = MutableStateFlow(false)
    val state: StateFlow<Boolean> = _state

    var itemIndex: Int = 0

    fun getStationsByName(name: String) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            Log.e("HTTP error...", throwable.message.toString())
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (useCase.getStationsLocalUseCase().isEmpty()) {
                val stations = useCase.getStationsUseCase()
                if (stations.isNotEmpty()) {
                    stations.forEach { station ->
                        useCase.insertStationIntoDbUseCase(station = station)
                    }
                    Log.d("DB Update", "List of stations update [${stations.size}]")
                } else {
                    Log.d("DB Update", "Something went wrong!")
                }
            }
            useCase.getStationsByTagLocalUseCase(tag = "%chillout%").apply {
                _stations.clear()
                _stations.addAll(this)
            }
            useCase.getStationsByTagLocalUseCase(tag = "%psychill%").apply {
                _stations.addAll(this)
            }

//            useCase.getStationsByNameLocalUseCase(name = "%$name%").apply {
//                _stations.clear()
//                _stations.addAll(this)
//            }
        }
    }

    fun addMediaItem(index: Int, item: Station) {
        player.addMediaItem(
            index = index,
            item = MediaItem.Builder()
                .setUri(item.urlResolved)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setMediaUri(Uri.Builder().path(item.urlResolved).build())
                        .setDisplayTitle(item.name)
                        .build()
                )
                .build()
        )
    }

    private fun addMediaItems() {
        val mediaItems = mutableListOf<MediaItem>()
        for ((index, station) in stations.withIndex()) {
            mediaItems.add(index, MediaItem.fromUri(station.urlResolved))
        }
        player.addMediaItems(mediaItems)
    }

    fun play(itemPosition: Int) {
        itemIndex = itemPosition
        player.seekPosition(itemPosition)
        player.play()
        _state.value = true
    }

    fun pause() {
        player.pause()
        _state.value = false
    }
}