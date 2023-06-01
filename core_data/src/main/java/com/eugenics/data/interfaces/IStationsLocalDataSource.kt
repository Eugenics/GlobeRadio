package com.eugenics.data.interfaces

import com.eugenics.core.interfaces.IDataSource
import com.eugenics.core_database.database.enteties.FavoritesTmpDaoObject
import com.eugenics.core_database.database.enteties.StationDaoObject

interface IStationsLocalDataSource : IDataSource {
    suspend fun fetchAllStations(): List<StationDaoObject>
    suspend fun fetchStationByName(name: String): List<StationDaoObject>
    suspend fun fetchStationByTag(tag: String): List<StationDaoObject>
    suspend fun insertStation(station: StationDaoObject)
    suspend fun insertStations(stations: List<StationDaoObject>)
    suspend fun deleteEmptyTags()
    suspend fun fetchStationsByFavorites(): List<StationDaoObject>
    suspend fun addFavorite(stationUuid: String)
    suspend fun deleteFavorite(stationUuid: String)
    suspend fun reloadStations(stations: List<StationDaoObject>)
    suspend fun checkStations(): List<StationDaoObject>
    suspend fun restoreFavorites(favorites: List<FavoritesTmpDaoObject>)
}