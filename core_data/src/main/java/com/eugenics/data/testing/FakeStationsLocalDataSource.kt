package com.eugenics.data.testing

import android.util.Log
import com.eugenics.core_database.database.enteties.FavoritesTmpDaoObject
import com.eugenics.core_database.database.enteties.StationDaoObject
import com.eugenics.data.interfaces.IStationsLocalDataSource
import java.util.UUID

class FakeStationsLocalDataSource : IStationsLocalDataSource {
    override suspend fun fetchAllStations(): List<StationDaoObject> = fakeStationsDaoObject

    override suspend fun fetchStationByName(name: String): List<StationDaoObject> =
        fakeStationsDaoObject

    override suspend fun fetchStationByTag(tag: String): List<StationDaoObject> =
        fakeStationsDaoObject

    override suspend fun insertStation(station: StationDaoObject) {
        Log.d(TAG, "Station inserted")
    }

    override suspend fun insertStations(stations: List<StationDaoObject>) {
        Log.d(TAG, "List of stations inserted")
    }

    override suspend fun deleteEmptyTags() {
        Log.d(TAG, "Empty tags deleted")
    }

    override suspend fun fetchStationsByFavorites(): List<StationDaoObject> = fakeStationsDaoObject

    override suspend fun addFavorite(stationUuid: String) {
        Log.d(TAG, "Favorites added")
    }

    override suspend fun deleteFavorite(stationUuid: String) {
        Log.d(TAG, "Favorite station deleted")
    }

    override suspend fun reloadStations(stations: List<StationDaoObject>) {
        Log.d(TAG, "Stations reloaded")
    }

    override suspend fun checkStations(): List<StationDaoObject> = fakeStationsDaoObject

    override suspend fun restoreFavorites(favorites: List<FavoritesTmpDaoObject>) {
        Log.d(TAG, "Favorites restored")
    }

    companion object {
        private const val TAG = "FakeDatasource"
        const val STATIONS_CNT = 100

        val fakeStationsDaoObject: List<StationDaoObject> =
            mutableListOf<StationDaoObject>().apply {
                repeat(STATIONS_CNT) {
                    this.add(
                        StationDaoObject(
                            stationuuid = UUID.randomUUID().toString(),
                            name = "Fake station #1",
                            tags = "fake",
                            homepage = "fake.com",
                            url = "http://94.130.113.214:8000/chill",
                            urlResolved = "http://94.130.113.214:8000/chill",
                            favicon = "http://static.radio.net/images/broadcasts/db/08/33694/c175.png",
                            bitrate = 128,
                            codec = "MP3",
                            country = "India",
                            countrycode = "IN",
                            language = "hindi",
                            languagecodes = "hi",
                            changeuuid = UUID.randomUUID().toString(),
                            isFavorite = 1
                        )
                    )
                }
            }
    }
}