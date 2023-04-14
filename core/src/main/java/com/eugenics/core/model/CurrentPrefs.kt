package com.eugenics.core.model

import com.eugenics.core.enums.TagsCommands
import kotlinx.serialization.Serializable


@Serializable
data class CurrentPrefs(
    val tag: String,
    val stationUuid: String,
    val command: String
) {
    companion object {
        fun getDefaultInstance(): CurrentPrefs = CurrentPrefs(
            tag = "/",
            stationUuid = "",
            command = TagsCommands.STATIONS_COMMAND.name
        )
    }
}
