package com.eugenics.media_service.data.datasources

import android.content.Context
import com.eugenics.media_service.data.database.database.DataBase
import com.eugenics.media_service.data.database.enteties.StationDaoObject
import com.eugenics.media_service.data.database.database.DataBaseFactory
import com.eugenics.media_service.domain.interfaces.repository.ILocalDataSource
import java.util.*

class LocalDataSourceImpl(
    private val context: Context
) : ILocalDataSource {
    private val database: DataBase = DataBaseFactory.create(context = context)

    override suspend fun fetchAllStations(): List<StationDaoObject> =
        database.dao.fetchAllStationData()

    override suspend fun fetchStationByName(name: String): List<StationDaoObject> =
        database.dao.fetchStationByName(name = name)

    override suspend fun fetchStationByTag(tag: String): List<StationDaoObject> =
        database.dao.fetchStationByTag(tag = tag)

    override suspend fun insertStation(station: StationDaoObject) =
        database.dao.insertStation(stationDao = station)

    override suspend fun insertStations(stations: List<StationDaoObject>) =
        database.dao.insertStations(stationsDao = stations)

    override suspend fun deleteEmptyTags() = database.dao.deleteStationsWithoutTags()

    override suspend fun refreshStations(stations: List<StationDaoObject>) =
        database.dao.refreshStations(stationsDao = stations)

    override suspend fun fetchStationsByFavorites(): List<StationDaoObject> =
        database.dao.fetchStationsByFavorites()

    override suspend fun addFavorite(stationUuid: String) {
        database.dao.addFavorite(uuid = UUID.randomUUID().toString(), stationUuid = stationUuid)
    }

    override suspend fun deleteFavorite(stationUuid: String) {
        database.dao.deleteFavorite(stationUuid = stationUuid)
    }

    companion object {
        fun newInstance(context: Context): ILocalDataSource =
            LocalDataSourceImpl(context = context)
    }
}