package com.eugenics.freeradio.domain.repository

import com.eugenics.freeradio.data.database.dao.StationDaoObject

interface ILocalDataSource {
    suspend fun fetchAllStations(): List<StationDaoObject>
    suspend fun fetchStationByName(name: String): List<StationDaoObject>
    suspend fun fetchStationByTag(tag: String): List<StationDaoObject>
    suspend fun insertStation(station: StationDaoObject)
}