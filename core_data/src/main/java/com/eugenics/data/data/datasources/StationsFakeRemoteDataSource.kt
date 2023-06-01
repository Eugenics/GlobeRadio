package com.eugenics.data.data.datasources

import com.eugenics.core.model.Station
import com.eugenics.core_network.dto.StationRespondObject
import com.eugenics.core_network.dto.asModel
import com.eugenics.data.interfaces.IStationsRemoteDataSource


class StationsFakeRemoteDataSource : IStationsRemoteDataSource {

    private fun createFakeStation(): StationRespondObject {
        return StationRespondObject(
            stationuuid = "96202f73-0601-11e8-ae97-52543be04c81",
            name = "Radio Schizoid - Chillout / Ambient",
            tags = "Electronics",
            homepage = "",
            url = "http://94.130.113.214:8000/chill",
            urlResolved = "http://94.130.113.214:8000/chill",
            favicon = "http://static.radio.net/images/broadcasts/db/08/33694/c175.png",
            bitrate = 128,
            codec = "MP3",
            country = "India",
            countrycode = "IN",
            language = "hindi",
            languagecodes = "hi",
            changeuuid = "92c2fbdc-14ec-4861-af65-49dd7de7826f",
        )
    }

    override suspend fun fetchStationsByName(name: String): List<Station> =
        listOf(createFakeStation()).map { stationRespondObject ->
            stationRespondObject.asModel()
        }

    override suspend fun fetchStationsByTag(tag: String): List<Station> =
        listOf(createFakeStation()).map { stationRespondObject ->
            stationRespondObject.asModel()
        }

    override suspend fun fetchStations(): List<Station> =
        listOf(createFakeStation()).map { stationRespondObject ->
            stationRespondObject.asModel()
        }
}