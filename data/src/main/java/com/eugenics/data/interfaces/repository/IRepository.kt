package com.eugenics.media_service.domain.interfaces.repository

import com.eugenics.data.data.database.enteties.StationDaoObject
import com.eugenics.data.data.dto.StationRespondObject
import com.eugenics.data.data.util.Response
import kotlinx.coroutines.flow.Flow


interface IRepository {
    suspend fun getRemoteStations(): Flow<Response<List<StationRespondObject>>>
    suspend fun getFakeStations(): Flow<Response<List<StationRespondObject>>>
    suspend fun getLocalStations(): List<StationDaoObject>
    suspend fun getLocalStationByTag(tag: String): List<StationDaoObject>
    suspend fun getLocalStationByName(name: String): List<StationDaoObject>

    suspend fun insertStation(stationDao: StationDaoObject)
    suspend fun insertStations(stations: List<StationDaoObject>)

    suspend fun deleteEmptyTags()

    suspend fun refreshStations(stations: List<StationDaoObject>)

    suspend fun fetchStationsByFavorites(): List<StationDaoObject>
    suspend fun addFavorite(stationUuid: String)
    suspend fun deleteFavorite(stationUuid: String)

    suspend fun reloadStations(stations: List<StationDaoObject>)
}