package com.eugenics.data.data.repository

import com.eugenics.data.data.util.Response
import com.eugenics.core.model.FavoriteStation
import com.eugenics.core.model.Station
import com.eugenics.core_database.database.enteties.asModel
import com.eugenics.data.data.util.asDao
import com.eugenics.data.interfaces.IStationsLocalDataSource
import com.eugenics.data.interfaces.IStationsRemoteDataSource
import com.eugenics.data.interfaces.IStationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StationsRepository(
    private val stationsRemoteDataSource: IStationsRemoteDataSource,
    private val stationsLocalDataSource: IStationsLocalDataSource
) : IStationsRepository {

    override fun getRemoteStations(): Flow<Response<List<Station>>> =
        flow {
            emit(Response.Loading())
            try {
                val stationList = stationsRemoteDataSource.fetchStations()

                emit(Response.Success(data = stationList))
            } catch (ex: Exception) {
                emit(Response.Error(error = ex.toString()))
            }
        }

    override suspend fun getLocalStations(): List<Station> =
        stationsLocalDataSource.fetchAllStations().map { stationDaoObject ->
            stationDaoObject.asModel()
        }

    override suspend fun getLocalStationByTag(tag: String): List<Station> =
        stationsLocalDataSource.fetchStationByTag(tag = tag).map { stationDaoObject ->
            stationDaoObject.asModel()
        }

    override suspend fun getLocalStationByName(name: String): List<Station> =
        stationsLocalDataSource.fetchStationByName(name = name).map { stationDaoObject ->
            stationDaoObject.asModel()
        }

    override suspend fun fetchStationsByFavorites(): List<Station> =
        stationsLocalDataSource.fetchStationsByFavorites().map { stationDaoObject ->
            stationDaoObject.asModel()
        }

    override suspend fun insertStation(station: Station) =
        stationsLocalDataSource.insertStation(station = station.asDao())

    override suspend fun insertStations(stations: List<Station>) =
        stationsLocalDataSource.insertStations(
            stations = stations.map { station -> station.asDao() }
        )

    override suspend fun addFavorite(stationUuid: String) {
        stationsLocalDataSource.addFavorite(stationUuid = stationUuid)
    }

    override suspend fun deleteFavorite(stationUuid: String) {
        stationsLocalDataSource.deleteFavorite(stationUuid = stationUuid)
    }

    override suspend fun reloadStations(stations: List<Station>) =
        stationsLocalDataSource.reloadStations(stations = stations.map { station ->
            station.asDao()
        })

    override suspend fun checkLocalStations(): List<Station> =
        stationsLocalDataSource.checkStations().map { stationDaoObject ->
            stationDaoObject.asModel()
        }


    override suspend fun restoreFavorites(favorites: List<FavoriteStation>) {
        stationsLocalDataSource.restoreFavorites(favorites = favorites.map { favorite ->
            favorite.asDao()
        })
    }

    override suspend fun deleteEmptyTags() = stationsLocalDataSource.deleteEmptyTags()
}