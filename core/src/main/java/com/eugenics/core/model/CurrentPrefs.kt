package com.eugenics.core.model

import com.eugenics.core.enums.Commands
import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable
data class CurrentPrefs(
    val tag: String,
    val stationUuid: String,
    val command: String,
    val query: String,
    val uuid: String
) {
    companion object {
        fun getDefaultInstance(): CurrentPrefs = CurrentPrefs(
            tag = "",
            stationUuid = "",
            command = Commands.STATIONS_COMMAND.name,
            query = "",
            uuid = UUID.randomUUID().toString()
        )
    }
}
