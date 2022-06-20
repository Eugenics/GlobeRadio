package com.eugenics.freeradio.data.repository

import android.util.Log
import com.eugenics.freeradio.data.dto.SearchStationRespond
import com.eugenics.freeradio.domain.repository.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeDataSourceImpl : DataSource {
    override suspend fun getStationsByName(name: String): List<SearchStationRespond> {
        val fakeSearchStationRespond = createFakeStation()
        Log.d("FakeDataSourceByName", fakeSearchStationRespond.toString())
        return listOf(fakeSearchStationRespond)
    }

    override suspend fun getStationsByTag(tag: String): List<SearchStationRespond> {
        return listOf(createFakeStation())
    }

    override suspend fun getStations(): List<SearchStationRespond> {
        return listOf(createFakeStation())
    }

    private fun createFakeStation(): SearchStationRespond {
        return SearchStationRespond(
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