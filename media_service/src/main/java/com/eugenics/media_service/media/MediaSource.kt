package com.eugenics.media_service.media

import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import com.eugenics.core.enums.TagsCommands
import com.eugenics.core.model.CurrentPrefs
import com.eugenics.core.model.PlayerMediaItem
import com.eugenics.data.data.database.enteties.StationDaoObject
import com.eugenics.data.data.util.Response
import com.eugenics.data.data.util.convertToMediaItem
import com.eugenics.data.interfaces.repository.IRepository
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection.Companion.SET_FAVORITES_COMMAND
import com.eugenics.media_service.util.PrefsHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "MEDIA_SOURCE"

class MediaSource(private val repository: IRepository) {

    private val prefsHelper = PrefsHelper(repository = repository)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        Log.e(TAG, throwable.message.toString(), throwable)
    }
    private val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)
    private val prefsScope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)

    private val _mediaItems: MutableStateFlow<MutableList<PlayerMediaItem>> =
        MutableStateFlow(mutableListOf())
    val mediaItems: StateFlow<List<PlayerMediaItem>> = _mediaItems

    private val _state: MutableStateFlow<Int> = MutableStateFlow(STATE_IDL)
    val state: StateFlow<Int> = _state

    private var startPosition: Int = 0
    private var playOnReady: Boolean = false

    private val prefs: MutableStateFlow<CurrentPrefs> =
        MutableStateFlow(CurrentPrefs.getDefaultInstance())

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
            prefsHelper.collectPrefs(
                prefs = prefs
            ) { newPrefs ->
                collectMediaSource(
                    tag = newPrefs.tag,
                    stationUuid = newPrefs.stationUuid,
                    command = newPrefs.command
                )
            }
        }
    }

    private fun collectMediaSource(
        tag: String,
        stationUuid: String,
        command: String
    ) {
        scope.launch {
            state.collect { stateValue ->
                when (stateValue) {
                    STATE_CREATED -> {
                        _state.value = STATE_INITIALIZING
                        try {
                            val stations = mutableListOf<StationDaoObject>()
                            when (command) {
                                TagsCommands.STATIONS_COMMAND.name ->
                                    stations.addAll(repository.getLocalStationByTag(tag = "%$tag%"))

                                TagsCommands.FAVORITES_COMMAND.name ->
                                    stations.addAll(repository.fetchStationsByFavorites())

                                else -> stations.addAll(repository.getLocalStations())
                            }

                            feelMediaItems(stations = stations)

                            onMediaItemClick(
                                mediaItemId = stationUuid,
                                false
                            )

                            _state.value = STATE_INITIALIZED
                        } catch (ex: Exception) {
                            _state.value = STATE_ERROR
                            Log.e(TAG, ex.message.toString())
                        }
                        playOnReady = false
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
                if (query.isBlank() || query == "*") {
                    stations.addAll(repository.getLocalStations())
                } else {
                    stations.addAll(
//                        repository.getLocalStationByName(name = "%$query%")
                        repository.getLocalStationByTag(tag = "%$query%")
                    )
                }

                feelMediaItems(stations = stations)
                delay(DELAY_TIME)
                _state.value = STATE_INITIALIZED
            } catch (ex: Exception) {
                _state.value = STATE_ERROR
                Log.e(TAG, ex.message.toString())
            }
        }
    }

    fun collectFavorites() {
        startPosition = 0
        setPlayOnReady(value = false)

        scope.launch {
            _state.value = STATE_INITIALIZING
            try {
                feelMediaItems(stations = repository.fetchStationsByFavorites())
                delay(DELAY_TIME)
                _state.value = STATE_INITIALIZED
            } catch (ex: Exception) {
                _state.value = STATE_ERROR
                Log.e(TAG, ex.message.toString())
            }
        }
    }

    fun collectSearch(query: String) {
        startPosition = 0
        setPlayOnReady(value = false)

        scope.launch {
            _state.value = STATE_INITIALIZING
            try {
                feelMediaItems(stations = repository.getLocalStationByName(name = "%$query%"))
                delay(DELAY_TIME)
                _state.value = STATE_INITIALIZED
            } catch (ex: Exception) {
                _state.value = STATE_ERROR
                Log.e(TAG, ex.message.toString())
            }
        }
    }

    fun setFavorites(
        stationUuid: String,
        isFavorite: Int,
        cb: ResultReceiver?
    ) {
        scope.launch {
            val resultBundle = Bundle()
            try {
                if (isFavorite == 1) {
                    repository.addFavorite(stationUuid = stationUuid)
                } else {
                    repository.deleteFavorite(stationUuid = stationUuid)
                }
                resultBundle.putString(SET_FAVORITES_COMMAND, "Success")
                cb?.send(1, resultBundle)
            } catch (ex: Exception) {
                _state.value = STATE_ERROR
                Log.e(TAG, ex.message.toString())
                resultBundle.putString(SET_FAVORITES_COMMAND, ex.message.toString())
                cb?.send(0, resultBundle)
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
        playOnReady = value
    }

    fun getPlayOnReady(): Boolean = playOnReady

    fun onMediaItemClick(
        mediaItemId: String,
        playWhenReady: Boolean = true
    ) {
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

            playOnReady = playWhenReady
            delay(DELAY_TIME)
            _state.value = STATE_INITIALIZED
        }
    }

    fun reloadStations() {
        try {
            _state.value = STATE_INITIALIZING
            scope.launch {
                repository.getRemoteStations().collect { response ->
                    when (response) {
                        is Response.Loading -> {
                            _state.value = STATE_INITIALIZING
                        }

                        is Response.Success -> {
                            val stations = response.data
                            stations?.let {
                                repository.reloadStations(
                                    stations.map { stationRespondObject ->
                                        stationRespondObject.convertToDaoObject()
                                    }
                                )
                            }
                            _state.value = STATE_INITIALIZED
                        }

                        is Response.Error -> {
                            Log.e(TAG, response.message)
                            _state.value = STATE_ERROR
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            _state.value = STATE_ERROR
            Log.e(TAG, ex.message.toString())
//            resultBundle.putString(SET_FAVORITES_COMMAND, ex.message.toString())
//            cb?.send(0, resultBundle)
        }
    }

    fun setPrefs(
        tag: String = prefs.value.tag,
        stationUuid: String = prefs.value.stationUuid,
        command: String = prefs.value.command
    ) {
        prefsScope.launch {
            prefsHelper.setPrefs(
                tag = tag, stationUuid = stationUuid, command = command
            )
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

        private const val DELAY_TIME = 1000L
    }
}