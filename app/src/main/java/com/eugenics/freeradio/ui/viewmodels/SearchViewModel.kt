package com.eugenics.freeradio.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eugenics.freeradio.domain.model.Station
import com.eugenics.freeradio.domain.model.convertToMediaItem
import com.eugenics.freeradio.domain.usecases.UseCase
import com.eugenics.media_service.player.Player
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
    private val player: Player
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
            item = item.convertToMediaItem()
        )
    }

    private fun addMediaItems() {
        player.addMediaItems(mediaItems = stations.map { station -> station.convertToMediaItem() })
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