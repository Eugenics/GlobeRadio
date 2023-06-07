package com.eugenics.data.data.datasources

import com.eugenics.core_database.database.dao.StationDao
import com.eugenics.core_database.database.enteties.FavoritesTmpDaoObject
import com.eugenics.core_database.database.enteties.StationDaoObject
import com.eugenics.data.interfaces.IStationsLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StationsLocalDataSource(private val stationDao: StationDao) : IStationsLocalDataSource {

    private val ioDispatcher = Dispatchers.IO

    override suspend fun fetchAllStations(): List<StationDaoObject> =
        withContext(ioDispatcher) {
            stationDao.fetchAllStationData()
        }


    override suspend fun fetchStationByName(name: String): List<StationDaoObject> =
        withContext(ioDispatcher) {
            stationDao.fetchStationByName(name = name)
        }


    override suspend fun fetchStationByTag(tag: String): List<StationDaoObject> =
        withContext(ioDispatcher) {
            stationDao.fetchStationByTag(tag = tag)
        }


    override suspend fun insertStation(station: StationDaoObject) =
        withContext(ioDispatcher) {
            stationDao.insertStation(stationDao = station)
        }

    override suspend fun insertStations(stations: List<StationDaoObject>) =
        withContext(ioDispatcher) {
            stationDao.insertStations(stationsDao = stations)
        }

    override suspend fun deleteEmptyTags() =
        withContext(ioDispatcher) {
            stationDao.deleteStationsWithoutTags()
        }

    override suspend fun fetchStationsByFavorites(): List<StationDaoObject> =
        withContext(ioDispatcher) {
            stationDao.fetchStationsByFavorites()
        }

    override suspend fun addFavorite(stationUuid: String) {
        withContext(ioDispatcher) {
            stationDao.addFavorite(stationUuid = stationUuid)
        }
    }

    override suspend fun deleteFavorite(stationUuid: String) {
        withContext(ioDispatcher) {
            stationDao.deleteFavorite(stationUuid = stationUuid)
        }
    }

    override suspend fun reloadStations(stations: List<StationDaoObject>) =
        withContext(ioDispatcher) {
            stationDao.reloadAllStations(stationsDao = stations)
        }

    override suspend fun checkStations(): List<StationDaoObject> =
        withContext(ioDispatcher) {
            stationDao.checkStations()
        }

    override suspend fun restoreFavorites(favorites: List<FavoritesTmpDaoObject>) {
        withContext(ioDispatcher) {
            stationDao.restoreFavorites(favorites = favorites)
        }
    }
}