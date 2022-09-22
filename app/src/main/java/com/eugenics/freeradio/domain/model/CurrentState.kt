package com.eugenics.freeradio.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class CurrentState(
    val tag: String,
    val stationUuid: String,
    val theme: Theme
) : Parcelable {
    companion object {
        fun getDefaultValueInstance(): CurrentState =
            CurrentState(
                tag = "",
                stationUuid = "",
                theme = Theme.SYSTEM
            )
    }
}
