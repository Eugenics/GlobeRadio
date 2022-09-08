package com.eugenics.media_service.media

import android.util.Log
import com.eugenics.media_service.data.database.enteties.StationDaoObject
import com.eugenics.media_service.data.util.Response
import com.eugenics.media_service.domain.model.PlayerMediaItem
import com.eugenics.media_service.domain.model.convertToMediaItem
import com.eugenics.media_service.domain.interfaces.repository.IRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "MEDIA_SOURCE"

class MediaSource(private val repository: IRepository) {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        Log.e(TAG, throwable.message.toString(), throwable)
    }
    private val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)

    private val _mediaItems: MutableStateFlow<MutableList<PlayerMediaItem>> =
        MutableStateFlow(mutableListOf())
    val mediaItems: StateFlow<List<PlayerMediaItem>> = _mediaItems

    private val _state: MutableStateFlow<Int> = MutableStateFlow(STATE_CREATED)
    val state: StateFlow<Int> = _state

    //Preload media source
    init {
        _state.value = STATE_INITIALIZING
        scope.launch {
            if (repository.getLocalStations().isEmpty()) {
                repository.getRemoteStations().collect { response ->
                    when (response) {
                        is Response.Loading -> {
                            _state.value = STATE_INITIALIZING
                        }
                        is Response.Error -> {
                            Log.e(TAG, response.message)
                            _state.value = STATE_ERROR
                        }
                        is Response.Success -> {
                            response.data?.let { stations ->
                                repository.refreshStations(
                                    stations = stations
                                        .map { station -> station.convertToDaoObject() }
                                )
                            }
                            _state.value = STATE_CREATED
                        }
                    }
                }
            } else {
                _state.value = STATE_CREATED
            }
        }
    }

    suspend fun collectMediaSource() {
        scope.launch {
            state.collect { stateValue ->
                when (stateValue) {
                    STATE_CREATED -> {
                        _state.value = STATE_INITIALIZING
                        try {
                            val stations = mutableListOf<StationDaoObject>()
                            stations.addAll(repository.getLocalStationByTag(tag = "%relax%"))
                            stations.addAll(repository.getLocalStationByTag(tag = "%chillout%"))

                            val playerMediaItems = mutableListOf<PlayerMediaItem>()
                            playerMediaItems.addAll(stations
                                .distinct()
                                .map { station ->
                                    station.convertToModel().convertToMediaItem()
                                }
                            )
                            playerMediaItems.sortBy { playerMediaItem -> playerMediaItem.name }
                            _mediaItems.value = playerMediaItems
                            _state.value = STATE_INITIALIZED
                        } catch (ex: Exception) {
                            _state.value = STATE_ERROR
                            Log.e(TAG, ex.message.toString())
                        }
                    }
                }
            }
        }
    }

    companion object {
        /**
         * State indicating the source was created, but no initialization has performed.
         */
        const val STATE_CREATED = 1

        /**
         * State indicating initialization of the source is in progress.
         */
        const val STATE_INITIALIZING = 2

        /**
         * State indicating the source has been initialized and is ready to be used.
         */
        const val STATE_INITIALIZED = 3

        /**
         * State indicating an error has occurred.
         */
        const val STATE_ERROR = 4
    }
}