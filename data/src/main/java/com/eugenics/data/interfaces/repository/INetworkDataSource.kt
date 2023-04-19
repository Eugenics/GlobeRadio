package com.eugenics.data.interfaces.repository

import com.eugenics.data.data.dto.StationRespondObject
import com.eugenics.core.interfaces.IDataSource

interface INetworkDataSource: IDataSource {
    suspend fun getStationsByName(name: String): List<StationRespondObject>
    suspend fun getStationsByTag(tag: String): List<StationRespondObject>
    suspend fun getStations(): List<StationRespondObject>
}