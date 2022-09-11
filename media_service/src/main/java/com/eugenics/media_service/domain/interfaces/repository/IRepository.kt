package com.eugenics.media_service.domain.interfaces.repository

import com.eugenics.media_service.data.database.enteties.StationDaoObject
import com.eugenics.media_service.data.dto.StationRespondObject
import com.eugenics.media_service.data.util.Response
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
}