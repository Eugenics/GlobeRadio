package com.eugenics.media_service.domain.model

import com.eugenics.media_service.domain.core.TagsCommands
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
