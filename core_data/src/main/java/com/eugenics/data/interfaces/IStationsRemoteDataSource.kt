package com.eugenics.data.interfaces

import com.eugenics.core.interfaces.IDataSource
import com.eugenics.core.model.Station

interface IStationsRemoteDataSource:IDataSource {
    suspend fun fetchStationsByName(name: String): List<Station>
    suspend fun fetchStationsByTag(tag: String): List<Station>
    suspend fun fetchStations(): List<Station>
}