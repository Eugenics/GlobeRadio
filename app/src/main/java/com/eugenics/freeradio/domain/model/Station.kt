package com.eugenics.freeradio.domain.model

import android.os.Parcelable
import com.eugenics.media_service.data.database.enteties.StationDaoObject
import com.eugenics.media_service.domain.model.PlayerMediaItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class Station(
    val stationuuid: String = "",
    val name: String = "",
    val tags: String = "",
    val homepage: String = "",
    val url: String = "",
    val urlResolved: String = "",
    val favicon: String = "",
    val bitrate: Int = 0,
    val codec: String = "",
    val country: String = "",
    val countrycode: String = "",
    val language: String = "",
    val languagecodes: String = "",
    val changeuuid: String = "",
    val isFavorite: Int = 0,
    val nowPlayingArtist: String = "",
    val nowPlayingTitle: String = ""
) : Parcelable

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
        changeuuid = this.changeuuid,
        isFavorite = this.isFavorite
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