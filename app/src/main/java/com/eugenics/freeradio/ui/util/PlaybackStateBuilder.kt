package com.eugenics.freeradio.ui.util

import android.support.v4.media.session.PlaybackStateCompat

internal object PlaybackStateBuilder {
    val STATE_PLAYING = stateBuilder(state = PlaybackStateCompat.STATE_PLAYING)
    val STATE_PAUSE = stateBuilder(state = PlaybackStateCompat.STATE_PAUSED)

    private fun stateBuilder(state: Int): PlaybackStateCompat =
        PlaybackStateCompat.Builder()
            .setState(state, 0, 0f)
            .build()
}

sealed class PlayBackState(val state:PlaybackStateCompat){
    object Playing:PlayBackState(state = PlaybackStateBuilder.STATE_PLAYING)
    object Pause:PlayBackState(state = PlaybackStateBuilder.STATE_PAUSE)
}