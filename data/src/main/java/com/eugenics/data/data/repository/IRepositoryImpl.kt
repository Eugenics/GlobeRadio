package com.eugenics.data.data.repository

import com.eugenics.data.data.database.enteties.StationDaoObject
import com.eugenics.data.data.dto.StationRespondObject
import com.eugenics.data.data.util.Response
import com.eugenics.data.interfaces.repository.INetworkDataSource
import com.eugenics.data.interfaces.repository.ILocalDataSource
import com.eugenics.media_service.domain.interfaces.repository.IRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository(
    private val remoteDataSource: INetworkDataSource,
    private val localDataSource: ILocalDataSource,
    private val fakeINetworkDataSource: INetworkDataSource
) : IRepository {
    override suspend fun getRemoteStations(): Flow<Response<List<StationRespondObject>>> =
        flow {
            emit(Response.Loading())
            try {
                val stationList = remoteDataSource.getStations()
                emit(Response.Success(data = stationList))
            } catch (ex: Exception) {
                emit(Response.Error(error = ex.message.toString()))
            }
        }

    override suspend fun getFakeStations(): Flow<Response<List<StationRespondObject>>> =
        flow {
            emit(Response.Loading())
            try {
                val stationList = fakeINetworkDataSource.getStations()
                emit(Response.Success(data = stationList))
            } catch (ex: Exception) {
                emit(Response.Error(error = ex.message.toString()))
            }
        }

    override suspend fun getLocalStations(): List<StationDaoObject> =
        localDataSource.fetchAllStations()

    override suspend fun getLocalStationByTag(tag: String): List<StationDaoObject> =
        localDataSource.fetchStationByTag(tag = tag)

    override suspend fun getLocalStationByName(name: String): List<StationDaoObject> =
        localDataSource.fetchStationByName(name = name)

    override suspend fun insertStation(stationDao: StationDaoObject) =
        localDataSource.insertStation(station = stationDao)

    override suspend fun insertStations(stations: List<StationDaoObject>) =
        localDataSource.insertStations(stations = stations)

    override suspend fun deleteEmptyTags() = localDataSource.deleteEmptyTags()

    override suspend fun refreshStations(stations: List<StationDaoObject>) =
        localDataSource.refreshStations(stations = stations)

    override suspend fun fetchStationsByFavorites(): List<StationDaoObject> =
        localDataSource.fetchStationsByFavorites()

    override suspend fun addFavorite(stationUuid: String) {
        localDataSource.addFavorite(stationUuid = stationUuid)
    }

    override suspend fun deleteFavorite(stationUuid: String) {
        localDataSource.deleteFavorite(stationUuid = stationUuid)
    }

    override suspend fun reloadStations(stations: List<StationDaoObject>) =
        localDataSource.reloadStations(stations = stations)
}