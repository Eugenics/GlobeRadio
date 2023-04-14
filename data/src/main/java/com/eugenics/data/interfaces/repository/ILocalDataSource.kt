package com.eugenics.data.interfaces.repository

import com.eugenics.data.data.database.enteties.StationDaoObject


interface ILocalDataSource {
    suspend fun fetchAllStations(): List<StationDaoObject>
    suspend fun fetchStationByName(name: String): List<StationDaoObject>
    suspend fun fetchStationByTag(tag: String): List<StationDaoObject>
    suspend fun insertStation(station: StationDaoObject)
    suspend fun insertStations(stations: List<StationDaoObject>)
    suspend fun deleteEmptyTags()
    suspend fun refreshStations(stations: List<StationDaoObject>)

    suspend fun fetchStationsByFavorites(): List<StationDaoObject>
    suspend fun addFavorite(stationUuid: String)
    suspend fun deleteFavorite(stationUuid: String)

    suspend fun reloadStations(stations: List<StationDaoObject>)
}