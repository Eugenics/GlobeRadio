package com.eugenics.freeradio.data.repository


import com.eugenics.freeradio.data.database.dao.StationDaoObject
import com.eugenics.freeradio.data.dto.SearchStationRespond
import com.eugenics.freeradio.domain.repository.DataSource
import com.eugenics.freeradio.domain.repository.ILocalDataSource
import com.eugenics.freeradio.domain.repository.Repository

class RepositoryImpl(
    private val dataSource: DataSource,
    private val localDataSource: ILocalDataSource
) : Repository {

    override suspend fun getStationsByName(name: String) = dataSource.getStationsByName(name = name)

    override suspend fun getStationsByTag(tag: String) = dataSource.getStationsByTag(tag = tag)

    override suspend fun getAllStations(): List<SearchStationRespond> = dataSource.getStations()

    override suspend fun getLocalStations(): List<StationDaoObject> =
        localDataSource.fetchAllStations()

    override suspend fun getLocalStationByName(name: String): List<StationDaoObject> =
        localDataSource.fetchStationByName(name = name)

    override suspend fun getLocalStationByTag(tag: String): List<StationDaoObject> =
        localDataSource.fetchStationByTag(tag = tag)

    override suspend fun insertStation(stationDao: StationDaoObject) =
        localDataSource.insertStation(station = stationDao)
}