package com.eugenics.freeradio.domain.model

import android.os.Parcelable
import com.eugenics.media_service.domain.core.TagsCommands
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
