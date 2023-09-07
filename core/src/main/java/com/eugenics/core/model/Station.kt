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
    val isFavorite: Int,
    val votes: Int
) : Parcelable {
    companion object {
        fun newInstance(
            stationuuid: String = "",
            name: String = "",
            tags: String = "",
            homepage: String = "",
            url: String = "",
            urlResolved: String = "",
            favicon: String = "",
            bitrate: Int = 0,
            codec: String = "",
            country: String = "",
            countrycode: String = "",
            language: String = "",
            languagecodes: String = "",
            changeuuid: String = "",
            isFavorite: Int = 0
        ): Station =
            Station(
                stationuuid = stationuuid,
                name = name,
                tags = tags,
                homepage = homepage,
                url = url,
                urlResolved = urlResolved,
                favicon = favicon,
                bitrate = bitrate,
                codec = codec,
                country = country,
                countrycode = countrycode,
                language = language,
                languagecodes = languagecodes,
                changeuuid = changeuuid,
                isFavorite = isFavorite,
                votes = 0
            )
    }
}