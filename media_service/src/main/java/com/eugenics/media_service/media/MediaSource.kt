package com.eugenics.media_service.media

import android.util.Log
import com.eugenics.media_service.data.database.enteties.StationDaoObject
import com.eugenics.media_service.data.util.Response
import com.eugenics.media_service.domain.model.PlayerMediaItem
import com.eugenics.media_service.domain.model.convertToMediaItem
import com.eugenics.media_service.domain.interfaces.repository.IRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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

    private val _state: MutableStateFlow<Int> = MutableStateFlow(STATE_IDL)
    val state: StateFlow<Int> = _state

    private var startPosition: Int = 0
    private var playOnRedy: Boolean = false

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

    fun collectMediaSource() {
        scope.launch {
            state.collect { stateValue ->
                when (stateValue) {
                    STATE_CREATED -> {
                        _state.value = STATE_INITIALIZING
                        try {
                            val stations = mutableListOf<StationDaoObject>()
                            stations.addAll(repository.getLocalStationByTag(tag = "%relax%"))
                            stations.addAll(repository.getLocalStationByTag(tag = "%chillout%"))

                            feelMediaItems(stations = stations)

                            _state.value = STATE_INITIALIZED
                        } catch (ex: Exception) {
                            _state.value = STATE_ERROR
                            Log.e(TAG, ex.message.toString())
                        }
                        playOnRedy = false
                    }
                }
                return@collect
            }
        }
    }

    fun searchInMediaSource(query: String) {
        startPosition = 0
        setPlayOnReady(value = false)

        scope.launch {
            _state.value = STATE_INITIALIZING
            try {
                val stations = mutableListOf<StationDaoObject>()
                if (query.isBlank()) {
                    stations.addAll(repository.getLocalStationByTag(tag = "%relax%"))
                    stations.addAll(repository.getLocalStationByTag(tag = "%chillout%"))
                } else {
                    stations.addAll(repository.getLocalStationByName(name = "%$query%"))
                }

                feelMediaItems(stations = stations)

                _state.value = STATE_INITIALIZED
            } catch (ex: Exception) {
                _state.value = STATE_ERROR
                Log.e(TAG, ex.message.toString())
            }
        }
    }

    fun collectFavorites() {
        scope.launch {
            _state.value = STATE_INITIALIZING
            try {
                feelMediaItems(stations = repository.fetchStationsByFavorites())
                _state.value = STATE_INITIALIZED
            } catch (ex: Exception) {
                _state.value = STATE_ERROR
                Log.e(TAG, ex.message.toString())
            }
        }
    }

    private fun feelMediaItems(stations: List<StationDaoObject>) {
        val playerMediaItems = mutableListOf<PlayerMediaItem>()
        playerMediaItems.addAll(stations
            .distinct()
            .map { station ->
                station.convertToModel().convertToMediaItem()
            }
        )
        playerMediaItems.sortBy { playerMediaItem -> playerMediaItem.name }
        _mediaItems.value = playerMediaItems
    }

    fun getStartPosition(): Int = startPosition

    private fun setPlayOnReady(value: Boolean) {
        playOnRedy = value
    }

    fun getPlayOnReady(): Boolean = playOnRedy

    fun onMediaItemClick(mediaItemId: String) {
        scope.launch {
            _state.value = STATE_INITIALIZING
            val startMediaItem = mediaItems.value.find {
                it.uuid == mediaItemId
            }
            startPosition =
                if (startMediaItem == null) {
                    0
                } else {
                    mediaItems.value.indexOf(startMediaItem)
                }

            playOnRedy = true
            delay(1000)
            _state.value = STATE_INITIALIZED
        }
    }

    companion object {
        const val STATE_IDL = 0

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