package com.eugenics.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NowPlayingStation(
    val name: String,
    val favicon: String,
    val nowPlayingTitle: String
) : Parcelable {
    companion object {
        fun emptyInstance() =
            NowPlayingStation(
                name = "",
                favicon = "",
                nowPlayingTitle = ""
            )

        fun newInstance(name: String = "", favicon: String = "", nowPlayingTitle: String = "") =
            NowPlayingStation(
                name = name,
                favicon = favicon,
                nowPlayingTitle = nowPlayingTitle
            )
    }
}
