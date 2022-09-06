package com.eugenics.media_service.domain.interfaces.repository

import com.eugenics.media_service.data.database.enteties.StationDaoObject

interface ILocalDataSource {
    suspend fun fetchAllStations(): List<StationDaoObject>
    suspend fun fetchStationByName(name: String): List<StationDaoObject>
    suspend fun fetchStationByTag(tag: String): List<StationDaoObject>
    suspend fun insertStation(station: StationDaoObject)
    suspend fun insertStations(stations: List<StationDaoObject>)
}