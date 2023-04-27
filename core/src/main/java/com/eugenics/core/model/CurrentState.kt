package com.eugenics.core.model

import android.os.Parcelable
import com.eugenics.core.enums.Commands
import com.eugenics.core.enums.Theme
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class CurrentState(
    val tag: String,
    val stationUuid: String,
    val theme: Theme,
    val command: String,
    val visibleIndex: Int
) : Parcelable {
    companion object {
        fun getDefaultValueInstance(): CurrentState =
            CurrentState(
                tag = "",
                stationUuid = "",
                theme = Theme.SYSTEM,
                command = Commands.STATIONS_COMMAND.name,
                visibleIndex = 0
            )
    }
}
