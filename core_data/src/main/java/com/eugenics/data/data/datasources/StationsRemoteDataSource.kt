package com.eugenics.data.data.datasources

import com.eugenics.core.model.Station
import com.eugenics.core.interfaces.IStationsApi
import com.eugenics.data.interfaces.IStationsRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StationsRemoteDataSource(
    private val IStationsApi: IStationsApi
) : IStationsRemoteDataSource {

    private val ioDispatcher = Dispatchers.IO

    override suspend fun fetchStationsByName(name: String): List<Station> =
        withContext(ioDispatcher) {
            IStationsApi.fetchStationsByName(name)
        }

    override suspend fun fetchStationsByTag(tag: String): List<Station> =
        withContext(ioDispatcher) {
            IStationsApi.fetchStationsByTag(tag)
        }

    override suspend fun fetchStations(): List<Station> =
        withContext(ioDispatcher) {
            IStationsApi.fetchAllStations()
        }
}