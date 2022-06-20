package com.eugenics.freeradio.domain.model

import androidx.room.ColumnInfo
import com.eugenics.freeradio.data.database.dao.StationDaoObject

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
    val changeuuid: String
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
}