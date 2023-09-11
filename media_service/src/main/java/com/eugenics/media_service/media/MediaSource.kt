package com.eugenics.media_service.media

import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import com.eugenics.core.enums.MediaSourceState
import com.eugenics.core.enums.Commands
import com.eugenics.core.model.CurrentPrefs
import com.eugenics.core.model.PlayerMediaItem
import com.eugenics.data.data.util.Response
import com.eugenics.data.data.util.asMediaItem
import com.eugenics.core.model.Station
import com.eugenics.data.interfaces.IPrefsRepository
import com.eugenics.data.interfaces.IStationsRepository
import com.eugenics.media_service.util.PrefsHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MediaSource(
    private val stationsRepository: IStationsRepository,
    private val prefsRepository: IPrefsRepository
) {

    private val prefsHelper = PrefsHelper(prefsRepository = prefsRepository)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        Log.e(TAG, throwable.message.toString(), throwable)
    }
    private val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)

    private val _mediaItems: MutableStateFlow<MutableList<PlayerMediaItem>> =
        MutableStateFlow(mutableListOf())
    val mediaItems: StateFlow<List<PlayerMediaItem>> = _mediaItems

    private val _state: MutableStateFlow<Int> = MutableStateFlow(MediaSourceState.STATE_IDL.value)
    val state: StateFlow<Int> = _state

    private var startPosition: Int = 0
    private var playOnReady: Boolean = false

    private val prefs: MutableStateFlow<CurrentPrefs> =
        MutableStateFlow(CurrentPrefs.getDefaultInstance())

    //Preload media source
    init {
        Log.d(TAG, "Init media source...")
        scope.launch {
            prefs.value = prefsHelper.getPrefs()

            if (stationsRepository.checkLocalStations().isEmpty()) {
                Log.d(TAG, "Load remote repository...")
                loadStationsFromRemote(firstLoad = true)
            } else {
                _state.value = MediaSourceState.STATE_CREATED.value
                collectMediaSource(
                    tag = prefs.value.tag,
                    stationUuid = prefs.value.stationUuid,
                    command = Commands.valueOf(prefs.value.command),
                    query = prefs.value.query
                )
            }
        }
    }

    fun collectMediaSource(
        tag: String,
        stationUuid: String,
        command: Commands,
        query: String = ""
    ) {
        scope.launch {
            Log.d(TAG, "COLLECT MEDIA_SOURCE:$tag,$command")
            _state.value = MediaSourceState.STATE_INITIALIZING.value
            try {
                when (command) {
                    Commands.STATIONS_COMMAND ->
                        feelMediaItems(stationsRepository.getLocalStationByTag(tag = "%$tag%"))

                    Commands.FAVORITES_COMMAND ->
                        feelMediaItems(stationsRepository.fetchStationsByFavorites())

                    Commands.RELOAD_ALL_STATIONS_COMMAND ->
                        loadStationsFromRemote()

                    Commands.SEARCH_COMMAND ->
                        feelMediaItems(stationsRepository.getLocalStationByName(name = "%$query%"))

                    else -> feelMediaItems(stationsRepository.getLocalStations())
                }

                if (command != Commands.RELOAD_ALL_STATIONS_COMMAND) {
                    setPrefs(
                        tag = tag,
                        command = command.name,
                        query = query,
                        stationUuid = stationUuid
                    )
                    onMediaItemClick(
                        mediaItemId = stationUuid,
                        playWhenReady = false
                    )
                }

                delay(DELAY_TIME)
                _state.value = MediaSourceState.STATE_INITIALIZED.value
            } catch (ex: Exception) {
                _state.value = MediaSourceState.STATE_ERROR.value
                Log.e(TAG, ex.message.toString())
            }
            setPlayOnReady(value = false)
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
                    stationsRepository.addFavorite(stationUuid = stationUuid)
                } else {
                    stationsRepository.deleteFavorite(stationUuid = stationUuid)
                }
                resultBundle.putString(Commands.SET_FAVORITES_COMMAND.name, "Success")
                cb?.send(1, resultBundle)
            } catch (ex: Exception) {
                _state.value = MediaSourceState.STATE_ERROR.value
                Log.e(TAG, ex.message.toString())
                resultBundle.putString(Commands.SET_FAVORITES_COMMAND.name, ex.message.toString())
                cb?.send(0, resultBundle)
            }
        }
    }

    private fun feelMediaItems(stations: List<Station>) {
        val playerMediaItems = mutableListOf<PlayerMediaItem>()
        playerMediaItems.addAll(stations
            .distinct()
            .map { station ->
                station.asMediaItem()
            }
        )
        playerMediaItems.sortByDescending { playerMediaItem -> playerMediaItem.votes }
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
        val startMediaItem = mediaItems.value.find {
            it.uuid == mediaItemId
        }
        startPosition =
            if (startMediaItem == null) {
                0
            } else {
                setPrefs(stationUuid = mediaItemId)
                mediaItems.value.indexOf(startMediaItem)
            }

        setPlayOnReady(value = playWhenReady)
    }

    private fun setPrefs(
        tag: String = prefs.value.tag,
        stationUuid: String = prefs.value.stationUuid,
        command: String = prefs.value.command,
        query: String = prefs.value.query
    ) {
        prefs.value = CurrentPrefs(
            tag = tag,
            stationUuid = stationUuid,
            command = command,
            query = query,
            uuid = prefs.value.uuid
        )
        scope.launch {
            prefsHelper.setPrefs(
                tag = tag, stationUuid = stationUuid, command = command, query = query
            )
        }
    }

    private suspend fun loadStationsFromRemote(firstLoad: Boolean = false) {
        stationsRepository.getRemoteStations().collect { response ->
            when (response) {
                is Response.Loading -> {
                    Log.d(TAG, "Loading...")
                    if (firstLoad) {
                        _state.value = MediaSourceState.STATE_FIRST_INIT.value
                    } else {
                        _state.value = MediaSourceState.STATE_INITIALIZING.value
                    }
                }

                is Response.Error -> {
                    Log.e(TAG, response.message)
                    _state.value = MediaSourceState.STATE_ERROR.value
                    return@collect
                }

                is Response.Success -> {
                    Log.d(TAG, "Success...")
                    response.data?.let { stations ->
                        Log.d(TAG, "Save to data base...")
                        stationsRepository.reloadStations(stations = stations)
                        Log.d(TAG, "Saved to data base...")
                    }

                    feelMediaItems(stationsRepository.getLocalStations())
                    setPrefs(command = Commands.STATIONS_COMMAND.name)

                    _state.value = MediaSourceState.STATE_INITIALIZED.value
                    return@collect
                }
            }
        }
    }

    companion object {
        const val TAG = "MEDIA_SOURCE"
        private const val DELAY_TIME = 1000L
    }
}