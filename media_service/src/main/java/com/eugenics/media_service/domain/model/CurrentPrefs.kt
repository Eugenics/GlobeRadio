package com.eugenics.media_service.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CurrentPrefs(
    val tag: String,
    val stationUuid: String
) {
    companion object {
        fun getDefaultInstance(): CurrentPrefs = CurrentPrefs(
            tag = "/",
            stationUuid = ""
        )
    }
}
