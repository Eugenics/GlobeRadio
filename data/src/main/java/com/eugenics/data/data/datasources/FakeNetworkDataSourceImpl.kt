package com.eugenics.data.data.datasources

import android.util.Log
import com.eugenics.data.data.dto.StationRespondObject
import com.eugenics.data.interfaces.repository.INetworkDataSource


class FakeNetworkDataSourceImpl : INetworkDataSource {
    override suspend fun getStationsByName(name: String): List<StationRespondObject> {
        val fakeSearchStationRespond = createFakeStation()
        Log.d("FakeDataSourceByName", fakeSearchStationRespond.toString())
        return listOf(fakeSearchStationRespond)
    }

    override suspend fun getStationsByTag(tag: String): List<StationRespondObject> {
        return listOf(createFakeStation())
    }

    override suspend fun getStations(): List<StationRespondObject> {
        return listOf(createFakeStation())
    }

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
}