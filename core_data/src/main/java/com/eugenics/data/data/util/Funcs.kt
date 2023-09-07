package com.eugenics.data.data.util

import com.eugenics.core.model.CurrentPrefs
import com.eugenics.core.model.FavoriteStation
import com.eugenics.core.model.PlayerMediaItem
import com.eugenics.core.model.Station
import com.eugenics.core_database.database.enteties.FavoritesTmpDaoObject
import com.eugenics.core_database.database.enteties.PrefsDaoObject
import com.eugenics.core_database.database.enteties.StationDaoObject

fun Station.asDao(): StationDaoObject =
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
        isFavorite = this.isFavorite,
        votes = this.votes
    )

fun Station.asMediaItem(): PlayerMediaItem =
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

fun FavoriteStation.asDao(): FavoritesTmpDaoObject =
    FavoritesTmpDaoObject(
        uuid = this.uuid,
        stationuuid = this.stationuuid
    )

fun CurrentPrefs.asDao(): PrefsDaoObject =
    PrefsDaoObject(
        uuid = uuid,
        tag = tag,
        stationUUID = stationUuid,
        command = command,
        query = query
    )