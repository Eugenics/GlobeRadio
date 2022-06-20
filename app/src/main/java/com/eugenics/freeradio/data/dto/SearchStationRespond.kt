package com.eugenics.freeradio.data.dto


import com.eugenics.freeradio.data.database.dao.StationDaoObject
import com.eugenics.freeradio.domain.model.Station
import com.google.gson.annotations.SerializedName


data class SearchStationRespond(
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
            changeuuid = changeuuid
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
            changeuuid = changeuuid
        )

}