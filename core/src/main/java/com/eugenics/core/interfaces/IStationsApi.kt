package com.eugenics.core.interfaces

import com.eugenics.core.model.Station

interface IStationsApi {

    suspend fun fetchAllStations(): List<Station>

    suspend fun fetchStationsByName(name: String): List<Station>

    suspend fun fetchStationsByTag(tag: String): List<Station>
}