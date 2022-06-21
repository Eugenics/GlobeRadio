package com.eugenics.freeradio.domain.model

import com.eugenics.freeradio.data.database.dao.StationDaoObject
import com.eugenics.media_service.domain.model.PlayerMediaItem

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
)

fun Station.convertToDao(): StationDaoObject =
    StationDaoObject(
        stationuuid = this.stationuuid,
        name = this.name,
        tags = this.tags,
        homepage = this.homepage,
        url = this.url,
        urlResolved = this.urlResolved,
        favicon = this.favicon,
        bitrate = this.bitrate,
        codec = this.codec,
        country = this.country,
        countrycode = this.countrycode,
        language = this.language,
        languagecodes = this.languagecodes,
        changeuuid = this.changeuuid
    )

fun Station.convertToMediaItem(): PlayerMediaItem =
    PlayerMediaItem(
        name = this.name,
        tags = this.tags,
        homepage = this.homepage,
        url = this.url,
        urlResolved = this.urlResolved,
        favicon = this.favicon,
        bitrate = this.bitrate,
        codec = this.codec
    )