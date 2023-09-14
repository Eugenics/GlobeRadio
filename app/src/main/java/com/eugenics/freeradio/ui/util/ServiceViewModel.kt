package com.eugenics.freeradio.ui.util

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eugenics.core.model.CurrentState
import com.eugenics.core.model.NowPlayingStation
import com.eugenics.core.model.Station
import com.eugenics.core.model.StationsUiState
import com.eugenics.core.model.Tag
import com.eugenics.freeradio.core.data.SystemMessage
import com.eugenics.freeradio.core.enums.DataState
import com.eugenics.freeradio.core.enums.MessageType
import com.eugenics.freeradio.core.enums.UIState
import com.eugenics.freeradio.ui.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

open class ServiceViewModel : ViewModel() {
    protected val ioDispatcher = Dispatchers.IO

    protected val _nowPlaying = MutableStateFlow(NowPlayingStation.emptyInstance())
    val nowPlaying: StateFlow<NowPlayingStation> = _nowPlaying

    protected val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.UI_STATE_SPLASH)
    val uiState: StateFlow<UIState> = _uiState

    protected val _dataState: MutableStateFlow<DataState> = MutableStateFlow(DataState.LOADING)
    val dataState: StateFlow<DataState> = _dataState

    protected val _stations: MutableStateFlow<List<Station>> = MutableStateFlow(mutableListOf())
    val stations: StateFlow<List<Station>> = _stations

    protected val _playBackState: MutableStateFlow<PlayBackState> =
        MutableStateFlow(PlayBackState.Pause)
    val playBackState: StateFlow<PlayBackState> = _playBackState

    protected val _currentStateObject: MutableStateFlow<CurrentState> =
        MutableStateFlow(CurrentState.getDefaultValueInstance())
    val currentStateObject: StateFlow<CurrentState> = _currentStateObject

    protected var _tagList: MutableStateFlow<List<Tag>> = MutableStateFlow(listOf())
    val tagList: StateFlow<List<Tag>> = _tagList

    protected val _savedData: MutableStateFlow<String> = MutableStateFlow("")
    val savedData: StateFlow<String> = _savedData

    protected val _message: MutableStateFlow<SystemMessage> =
        MutableStateFlow(SystemMessage.emptyInstance())
    val message: StateFlow<SystemMessage> = _message

    protected val _uiCommand: MutableStateFlow<UICommands> =
        MutableStateFlow(UICommands.UI_COMMAND_IDL)
    val uiCommands: StateFlow<UICommands> = _uiCommand

    protected val _primaryDynamicColor: MutableStateFlow<Int> = MutableStateFlow(0)
    val primaryDynamicColor: StateFlow<Int> = _primaryDynamicColor

    protected val _stationsUiState: MutableStateFlow<StationsUiState> =
        MutableStateFlow(StationsUiState.emptyInstance())
    val stationsUiState: StateFlow<StationsUiState> = _stationsUiState

    fun sendMessage(type: MessageType, message: String) {
        _message.value = SystemMessage.newInstance(
            id = UUID.randomUUID().toString(), type = type, message = message
        )
        Log.d(MainViewModel.TAG, message)
    }

    fun setPrimaryDynamicColor(rgb: Int) {
        _primaryDynamicColor.value = rgb
    }

    protected fun collectStates() {
        viewModelScope.launch {
            uiState.collect {
                Log.d(MainViewModel.TAG, it.name)
            }
        }
        viewModelScope.launch {
            dataState.collect {
                Log.d(MainViewModel.TAG, it.name)
            }
        }
    }
}