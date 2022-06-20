package com.eugenics.freeradio.data.database.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.eugenics.freeradio.domain.model.Station

@Entity(tableName = "stations")
data class StationDaoObject(
    @PrimaryKey
    @ColumnInfo(name = "stationuuid")
    val stationuuid: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "tags")
    val tags: String,
    @ColumnInfo(name = "homepage")
    val homepage: String,
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "url_resolved")
    val urlResolved: String,
    @ColumnInfo(name = "favicon")
    val favicon: String,
    @ColumnInfo(name = "bitrate")
    val bitrate: Int,
    @ColumnInfo(name = "codec")
    val codec: String,
    @ColumnInfo(name = "country")
    val country: String,
    @ColumnInfo(name = "countrycode")
    val countrycode: String,
    @ColumnInfo(name = "language")
    val language: String,
    @ColumnInfo(name = "languagecodes")
    val languagecodes: String,
    @ColumnInfo(name = "changeuuid")
    val changeuuid: String
) {
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
