package com.eugenics.data.interfaces

import com.eugenics.core.interfaces.IRepository
import com.eugenics.core.model.FavoriteStation
import com.eugenics.core.model.Station
import com.eugenics.data.data.util.Response
import kotlinx.coroutines.flow.Flow

interface IStationsRepository : IRepository {
    fun getRemoteStations(): Flow<Response<List<Station>>>

    suspend fun getLocalStations(): List<Station>

    suspend fun getLocalStationByTag(tag: String): List<Station>

    suspend fun getLocalStationByName(name: String): List<Station>

    suspend fun fetchStationsByFavorites(): List<Station>

    suspend fun insertStation(station: Station)

    suspend fun insertStations(stations: List<Station>)

    suspend fun addFavorite(stationUuid: String)

    suspend fun deleteFavorite(stationUuid: String)

    suspend fun reloadStations(stations: List<Station>)

    suspend fun checkLocalStations(): List<Station>

    suspend fun restoreFavorites(favorites: List<FavoriteStation>)

    suspend fun deleteEmptyTags()
}