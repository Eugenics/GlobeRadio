package com.eugenics.freeradio.util.data

import com.eugenics.core.model.Station
import com.fasterxml.jackson.annotation.JsonSetter

data class StationPojo(
    @JsonSetter("stationuuid")
    val stationuuid: String,
    @JsonSetter("name")
    val name: String,
    @JsonSetter("tags")
    val tags: String,
    @JsonSetter("homepage")
    val homepage: String,
    @JsonSetter("url")
    val url: String,
    @JsonSetter("url_resolved")
    val urlResolved: String,
    @JsonSetter("favicon")
    val favicon: String,
    @JsonSetter("bitrate")
    val bitrate: Int,
    @JsonSetter("codec")
    val codec: String,
    @JsonSetter("country")
    val country: String,
    @JsonSetter("countrycode")
    val countrycode: String,
    @JsonSetter("language")
    val language: String,
    @JsonSetter("languagecodes")
    val languagecodes: String,
    @JsonSetter("changeuuid")
    val changeuuid: String,
    @JsonSetter("votes")
    val votes: Int
)

fun StationPojo.mapToModel(): Station =
    Station.newInstance(
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
