package com.eugenics.freeradio.domain.repository

import com.eugenics.freeradio.data.database.dao.StationDaoObject
import com.eugenics.freeradio.data.dto.SearchStationRespond
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getStationsByName(name: String): List<SearchStationRespond>
    suspend fun getStationsByTag(tag: String): List<SearchStationRespond>
    suspend fun getAllStations(): List<SearchStationRespond>

    suspend fun getLocalStations(): List<StationDaoObject>
    suspend fun getLocalStationByName(name: String): List<StationDaoObject>
    suspend fun getLocalStationByTag(tag: String): List<StationDaoObject>
    suspend fun insertStation(stationDao: StationDaoObject)
}