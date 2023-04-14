package com.eugenics.data.interfaces.repository

import com.eugenics.data.data.dto.StationRespondObject

interface IDataSource {
    suspend fun getStationsByName(name: String): List<StationRespondObject>
    suspend fun getStationsByTag(tag: String): List<StationRespondObject>
    suspend fun getStations(): List<StationRespondObject>
}