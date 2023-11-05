package com.eugenics.core.model

import com.eugenics.core.enums.Commands
import kotlinx.serialization.Serializable


@Serializable
data class CurrentPrefs(
    val tag: String,
    val stationUuid: String,
    val command: String,
    val query: String,
    val uuid: String
) {
    companion object {
        const val GUID = "0f984b7d-6fe3-4034-b3d4-61c31ff82b2a"
        fun getDefaultInstance(): CurrentPrefs = CurrentPrefs(
            tag = "",
            stationUuid = "",
            command = Commands.STATIONS_COMMAND.name,
            query = "",
            uuid = GUID
        )
    }
}
