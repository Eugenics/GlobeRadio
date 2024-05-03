package com.eugenics.freeradio.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.eugenics.core.model.CurrentState
import com.eugenics.core.model.NowPlayingStation
import com.eugenics.core.model.Station
import com.eugenics.core.model.StationsUiState
import com.eugenics.core.model.Tag
import com.eugenics.ui_core.data.enums.PlayBackState
import com.eugenics.ui_core.data.enums.UICommands
import com.eugenics.ui_core.data.enums.DataState
import com.eugenics.ui_core.data.enums.UIDataState
import com.eugenics.ui_core.data.model.UIMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

open class ServiceViewModel : ViewModel() {
    protected val ioDispatcher = Dispatchers.IO

    protected val _nowPlaying = MutableStateFlow(NowPlayingStation.emptyInstance())
    val nowPlaying: StateFlow<NowPlayingStation> = _nowPlaying

    protected val _Ui_dataState: MutableStateFlow<UIDataState> = MutableStateFlow(DataState.Loading)
    val uiDataState: StateFlow<UIDataState> = _Ui_dataState

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

    protected val _message: MutableStateFlow<UIMessage> =
        MutableStateFlow(UIMessage.emptyInstance())
    val message: StateFlow<UIMessage> = _message

    protected val _uiCommand: MutableStateFlow<UICommands> =
        MutableStateFlow(UICommands.UI_COMMAND_IDL)
    val uiCommands: StateFlow<UICommands> = _uiCommand

    protected val _primaryDynamicColor: MutableStateFlow<Int> = MutableStateFlow(0)
    val primaryDynamicColor: StateFlow<Int> = _primaryDynamicColor

    protected val _stationsUiState: MutableStateFlow<StationsUiState> =
        MutableStateFlow(StationsUiState.emptyInstance())
    val stationsUiState: StateFlow<StationsUiState> = _stationsUiState

    fun sendMessage(messageType: Int, messageInfoType: Int, messageText: String = "") {
        _message.value = UIMessage.newInstance(
            id = UUID.randomUUID().toString(),
            messageType = messageType,
            messageInfoType = messageInfoType,
            messageText = messageText
        )
        Log.d(MainViewModel.TAG, messageInfoType.toString())
    }

    fun setPrimaryDynamicColor(rgb: Int) {
        _primaryDynamicColor.value = rgb
    }
}