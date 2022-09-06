package com.eugenics.media_service.data.repository

import android.content.Context
import com.eugenics.media_service.data.database.enteties.StationDaoObject
import com.eugenics.media_service.data.datasources.DataSourceFactory
import com.eugenics.media_service.data.dto.StationRespondObject
import com.eugenics.media_service.data.util.Response
import com.eugenics.media_service.domain.interfaces.repository.IDataSource
import com.eugenics.media_service.domain.interfaces.repository.ILocalDataSource
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

    override suspend fun insertStation(stationDao: StationDaoObject) =
        localDataSource.insertStation(station = stationDao)

    override suspend fun insertStations(stations: List<StationDaoObject>) =
        localDataSource.insertStations(stations = stations)

    companion object {
        fun newInstance(context: Context): IRepository =
            IRepositoryImpl(context = context)
    }
}