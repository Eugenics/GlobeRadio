package com.eugenics.data.data.datasources

import com.eugenics.data.data.api.ApiService
import com.eugenics.data.data.dto.StationRespondObject
import com.eugenics.data.data.api.ApiFactory
import com.eugenics.data.interfaces.repository.IDataSource

class NetworkIDataSourceImpl(
    private val apiService: ApiService = ApiFactory.create()
) : IDataSource {
    override suspend fun getStationsByName(name: String): List<StationRespondObject> =
        apiService.searchByName(name)

    override suspend fun getStationsByTag(tag: String): List<StationRespondObject> =
        apiService.searchByTag(tag)

    override suspend fun getStations(): List<StationRespondObject> =
        apiService.searchAll()

    companion object {
        fun newInstance(): IDataSource = NetworkIDataSourceImpl()
    }
}