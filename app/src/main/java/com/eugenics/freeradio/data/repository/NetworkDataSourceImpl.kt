package com.eugenics.freeradio.data.repository

import com.eugenics.freeradio.data.dto.SearchStationRespond
import com.eugenics.freeradio.data.network.ApiService
import com.eugenics.freeradio.domain.repository.DataSource

class NetworkDataSourceImpl(private val apiService: ApiService) : DataSource {
    override suspend fun getStationsByName(name: String): List<SearchStationRespond> =
        apiService.searchByName(name)

    override suspend fun getStationsByTag(tag: String): List<SearchStationRespond> =
        apiService.searchByTag(tag)

    override suspend fun getStations(): List<SearchStationRespond> =
        apiService.searchAll()
}