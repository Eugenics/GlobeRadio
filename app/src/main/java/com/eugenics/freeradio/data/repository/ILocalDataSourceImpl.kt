package com.eugenics.freeradio.data.repository

import com.eugenics.freeradio.data.database.DataBase
import com.eugenics.freeradio.data.database.dao.StationDaoObject
import com.eugenics.freeradio.domain.repository.ILocalDataSource
import javax.inject.Inject

class ILocalDataSourceImpl @Inject constructor(private val database: DataBase) : ILocalDataSource {
    override suspend fun fetchAllStations(): List<StationDaoObject> =
        database.dao.fetchAllStationData()

    override suspend fun fetchStationByName(name: String): List<StationDaoObject> =
        database.dao.fetchStationByName(name = name)

    override suspend fun fetchStationByTag(tag: String): List<StationDaoObject> =
        database.dao.fetchStationByTag(tag = tag)

    override suspend fun insertStation(station: StationDaoObject) =
        database.dao.insertStation(stationDao = station)
}