package com.eugenics.media_service.data.dto

import com.eugenics.media_service.data.database.enteties.StationDaoObject
import com.eugenics.media_service.domain.model.Station
import com.google.gson.annotations.SerializedName
import okhttp3.internal.isSensitiveHeader


data class StationRespondObject(
    @SerializedName("stationuuid")
    val stationuuid: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("tags")
    val tags: String,
    @SerializedName("homepage")
    val homepage: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("url_resolved")
    val urlResolved: String,
    @SerializedName("favicon")
    val favicon: String,
    @SerializedName("bitrate")
    val bitrate: Int,
    @SerializedName("codec")
    val codec: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("countrycode")
    val countrycode: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("languagecodes")
    val languagecodes: String,
    @SerializedName("changeuuid")
    val changeuuid: String,
) {
    fun convertToDaoObject(): StationDaoObject =
        StationDaoObject(
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
            isFavorite = 0
        )

    fun convertToModel(): Station =
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
            isFavorite = 0
        )

}