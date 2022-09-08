package com.eugenics.media_service.data.datasources

import com.eugenics.media_service.data.api.ApiService
import com.eugenics.media_service.data.dto.StationRespondObject
import com.eugenics.media_service.data.api.ApiFactory
import com.eugenics.media_service.domain.interfaces.repository.IDataSource

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