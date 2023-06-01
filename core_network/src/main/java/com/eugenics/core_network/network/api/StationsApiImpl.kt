package com.eugenics.core_network.network.api

import com.eugenics.core.interfaces.IStationsApi
import com.eugenics.core.model.Station
import com.eugenics.core_network.dto.asModel

class StationsApiImpl(private val apiService: ApiService) : IStationsApi {
    override suspend fun fetchAllStations(): List<Station> =
        apiService.searchAll().map { stationRespondObject ->
            stationRespondObject.asModel()
        }

    override suspend fun fetchStationsByName(name: String): List<Station> =
        apiService.searchByName(name = name).map { stationRespondObject ->
            stationRespondObject.asModel()
        }

    override suspend fun fetchStationsByTag(tag: String): List<Station> =
        apiService.searchByTag(tag = tag).map { stationRespondObject ->
            stationRespondObject.asModel()
        }
}