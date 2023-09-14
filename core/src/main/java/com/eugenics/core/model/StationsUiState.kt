package com.eugenics.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class StationsUiState(
    val clickedStationUid: String,
    val visibleIndex: Int
) : Parcelable {
    companion object {
        fun emptyInstance(): StationsUiState =
            StationsUiState(clickedStationUid = "", visibleIndex = 0)
    }
}
