package com.eugenics.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class PlayerMediaItem(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String = "",
    val tags: String = "",
    val homepage: String = "",
    val url: String = "",
    val urlResolved: String = "",
    val favicon: String = "",
    val bitrate: Int = 0,
    val codec: String = "",
    val isFavorite: Int = 0,
    val votes: Int = 0
) : Parcelable {
    companion object {
        fun newInstance(): PlayerMediaItem = PlayerMediaItem()
    }
}
