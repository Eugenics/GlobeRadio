package com.eugenics.ui_core.data.enums

sealed class PlayBackState(val name: String) {
    data object Playing : PlayBackState(name = "Playing")
    data object Pause : PlayBackState(name = "Pause")
}