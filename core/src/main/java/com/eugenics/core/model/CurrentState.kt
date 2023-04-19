package com.eugenics.core.model

import android.os.Parcelable
import com.eugenics.core.enums.TagsCommands
import com.eugenics.core.enums.Theme
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class CurrentState(
    val tag: String,
    val stationUuid: String,
    val theme: Theme,
    val command: String
) : Parcelable {
    companion object {
        fun getDefaultValueInstance(): CurrentState =
            CurrentState(
                tag = "",
                stationUuid = "",
                theme = Theme.SYSTEM,
                command = TagsCommands.STATIONS_COMMAND.name
            )
    }
}
