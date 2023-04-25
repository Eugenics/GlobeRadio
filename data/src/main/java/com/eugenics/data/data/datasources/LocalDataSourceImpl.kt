package com.eugenics.data.data.datasources

import com.eugenics.data.data.database.dao.PrefsDao
import com.eugenics.data.data.database.dao.StationDao
import com.eugenics.data.data.database.enteties.PrefsDaoObject
import com.eugenics.data.data.database.enteties.StationDaoObject
import com.eugenics.data.interfaces.repository.ILocalDataSource

class LocalDataSourceImpl(
    private val dao: StationDao,
    private val prefsDao: PrefsDao
) : ILocalDataSource {

    override suspend fun fetchAllStations(): List<StationDaoObject> =
        dao.fetchAllStationData()

    override suspend fun fetchStationByName(name: String): List<StationDaoObject> =
        dao.fetchStationByName(name = name)

    override suspend fun fetchStationByTag(tag: String): List<StationDaoObject> =
        dao.fetchStationByTag(tag = tag)

    override suspend fun insertStation(station: StationDaoObject) =
        dao.insertStation(stationDao = station)

    override suspend fun insertStations(stations: List<StationDaoObject>) =
        dao.insertStations(stationsDao = stations)

    override suspend fun deleteEmptyTags() = dao.deleteStationsWithoutTags()

    override suspend fun fetchStationsByFavorites(): List<StationDaoObject> =
        dao.fetchStationsByFavorites()

    override suspend fun addFavorite(stationUuid: String) {
        dao.addFavorite(stationUuid = stationUuid)
    }

    override suspend fun deleteFavorite(stationUuid: String) {
        dao.deleteFavorite(stationUuid = stationUuid)
    }

    override suspend fun reloadStations(stations: List<StationDaoObject>) =
        dao.reloadAllStations(stationsDao = stations)

    override suspend fun checkStations(): List<StationDaoObject> =
        dao.checkStations()

    override suspend fun fetchPrefs(): List<PrefsDaoObject> =
        prefsDao.fetchPrefs()

    override suspend fun updatePrefs(prefs: PrefsDaoObject) {
        prefsDao.updatePrefs(prefsObject = prefs)
    }

    override suspend fun insertPrefs(prefs: PrefsDaoObject) {
        prefsDao.insertPrefs(prefsObject = prefs)
    }

    override suspend fun deletePrefs() {
        prefsDao.deletePrefs()
    }

    companion object {
        fun newInstance(dao: StationDao, prefsDao: PrefsDao) =
            LocalDataSourceImpl(dao = dao, prefsDao = prefsDao)
    }
}