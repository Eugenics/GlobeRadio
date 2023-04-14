package com.eugenics.data.data.datasources

import com.eugenics.data.data.api.ApiService
import com.eugenics.data.data.dto.StationRespondObject
import com.eugenics.data.interfaces.repository.INetworkDataSource

class NetworkINetworkDataSourceImpl(
    private val apiService: ApiService
) : INetworkDataSource {
    override suspend fun getStationsByName(name: String): List<StationRespondObject> =
        apiService.searchByName(name)

    override suspend fun getStationsByTag(tag: String): List<StationRespondObject> =
        apiService.searchByTag(tag)

    override suspend fun getStations(): List<StationRespondObject> =
        apiService.searchAll()
}