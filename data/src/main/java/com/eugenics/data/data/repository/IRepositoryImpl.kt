package com.eugenics.data.data.repository

import android.content.Context
import com.eugenics.data.data.database.enteties.StationDaoObject
import com.eugenics.data.data.datasources.DataSourceFactory
import com.eugenics.data.data.dto.StationRespondObject
import com.eugenics.data.data.util.Response
import com.eugenics.data.interfaces.repository.IDataSource
import com.eugenics.data.interfaces.repository.ILocalDataSource
import com.eugenics.media_service.domain.interfaces.repository.IRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class IRepositoryImpl(
    private val context: Context
) : IRepository {
    private val remoteDataSource: IDataSource = DataSourceFactory.createRemoteDataSource()
    private val localDataSource: ILocalDataSource =
        DataSourceFactory.createLocalDataSource(context = context)
    private val fakeIDataSource: IDataSource = DataSourceFactory.createFakeDataSource()

    override suspend fun getRemoteStations(): Flow<Response<List<StationRespondObject>>> = flow {
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
                val stationList = fakeIDataSource.getStations()
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

    companion object {
        fun newInstance(context: Context): IRepository =
            IRepositoryImpl(context = context)
    }
}