package com.eugenics.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NowPlayingStation(
    val name: String,
    val favicon: String,
    val nowPlayingTitle: String,
    val stationUUID: String
) : Parcelable {
    companion object {
        fun emptyInstance() =
            NowPlayingStation(
                name = "",
                favicon = "",
                nowPlayingTitle = "",
                stationUUID = ""
            )

        fun newInstance(
            name: String = "",
            favicon: String = "",
            nowPlayingTitle: String = "",
            stationUUID: String = ""
        ) =
            NowPlayingStation(
                name = name,
                favicon = favicon,
                nowPlayingTitle = nowPlayingTitle,
                stationUUID = stationUUID
            )
    }
}
