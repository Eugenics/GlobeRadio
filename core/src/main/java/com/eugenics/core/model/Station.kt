package com.eugenics.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Station(
    val stationuuid: String,
    val name: String,
    val tags: String,
    val homepage: String,
    val url: String,
    val urlResolved: String,
    val favicon: String,
    val bitrate: Int,
    val codec: String,
    val country: String,
    val countrycode: String,
    val language: String,
    val languagecodes: String,
    val changeuuid: String,
    val isFavorite: Int
) : Parcelable {
    companion object {
        fun emptyInstance(): Station =
            Station(
                stationuuid = "",
                name = "",
                tags = "",
                homepage = "",
                url = "",
                urlResolved = "",
                favicon = "",
                bitrate = 0,
                codec = "",
                country = "",
                countrycode = "",
                language = "",
                languagecodes = "",
                changeuuid = "",
                isFavorite = 0
            )
    }
}