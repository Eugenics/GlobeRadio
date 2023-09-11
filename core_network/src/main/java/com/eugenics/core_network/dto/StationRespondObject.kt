package com.eugenics.core_network.dto

import com.eugenics.core.model.Station
import com.google.gson.annotations.SerializedName

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
    @SerializedName("votes")
    val votes: Int
)

fun StationRespondObject.asModel(): Station =
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
        isFavorite = 0,
        votes = votes
    )