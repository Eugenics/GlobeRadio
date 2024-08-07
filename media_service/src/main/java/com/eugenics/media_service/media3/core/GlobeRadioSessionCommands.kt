package com.eugenics.media_service.media3.core

import android.os.Bundle
import androidx.media3.session.SessionCommand
import com.eugenics.core.constants.RELOAD_ALL_STATIONS_COMMAND
import com.eugenics.core.constants.SET_FAVORITES_COMMAND
import com.eugenics.core.constants.TAG_COMMAND

object GlobeRadioSessionCommands {
    val customSessionCommandsList = listOf(
        SessionCommand(SET_FAVORITES_COMMAND, Bundle()),
        SessionCommand(RELOAD_ALL_STATIONS_COMMAND, Bundle()),
        SessionCommand(TAG_COMMAND, Bundle()),
    )
}