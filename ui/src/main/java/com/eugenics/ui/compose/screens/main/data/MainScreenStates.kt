package com.eugenics.ui.compose.screens.main.data

import com.eugenics.core.model.NowPlayingStation
import com.eugenics.core.model.Station
import com.eugenics.core.model.StationsUiState
import com.eugenics.core.model.Tag
import com.eugenics.ui_core.data.enums.DataState
import com.eugenics.ui_core.data.enums.PlayBackState
import com.eugenics.ui_core.data.model.UIMessage

data class MainScreenStates(
    val nowPlaying: NowPlayingStation,
    val dataState: DataState,
    val stations: List<Station>,
    val playBackState: PlayBackState,
    val tagList: List<Tag>,
    val message: UIMessage,
    val stationsUiState: StationsUiState
) {
    @Synchronized
    fun copyObject(
        nowPlaying: NowPlayingStation = this.nowPlaying,
        uiDataState: DataState = this.dataState,
        stations: List<Station> = this.stations,
        playBackState: PlayBackState = this.playBackState,
        tagList: List<Tag> = this.tagList,
        message: UIMessage = this.message,
        stationsUiState: StationsUiState = this.stationsUiState
    ): MainScreenStates =
        MainScreenStates(
            nowPlaying = nowPlaying,
            dataState = uiDataState,
            stations = stations,
            playBackState = playBackState,
            tagList = tagList,
            message = message,
            stationsUiState = stationsUiState
        )

    fun clearStationsList() = this.copyObject(stations = listOf())

    companion object {
        fun emptyInstance(): MainScreenStates = MainScreenStates(
            nowPlaying = NowPlayingStation.emptyInstance(),
            dataState = DataState.Loading,
            stations = listOf(),
            playBackState = PlayBackState.Pause,
            tagList = listOf(),
            message = UIMessage.emptyInstance(),
            stationsUiState = StationsUiState.emptyInstance()
        )
    }
}
