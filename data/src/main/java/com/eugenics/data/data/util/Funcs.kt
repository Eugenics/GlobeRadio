package com.eugenics.data.data.util

import com.eugenics.core.model.FavoriteStation
import com.eugenics.core.model.PlayerMediaItem
import com.eugenics.core.model.Station
import com.eugenics.data.data.database.enteties.FavoritesTmpDaoObject
import com.eugenics.data.data.database.enteties.StationDaoObject

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
        uuid = this.stationuuid,
        name = this.name,
        tags = this.tags,
        homepage = this.homepage,
        url = this.url,
        urlResolved = this.urlResolved,
        favicon = this.favicon,
        bitrate = this.bitrate,
        codec = this.codec,
        isFavorite = this.isFavorite
    )

fun FavoriteStation.convertToFavoritesTmpDaoObject(): FavoritesTmpDaoObject =
    FavoritesTmpDaoObject(
        uuid = this.uuid,
        stationuuid = this.stationuuid
    )