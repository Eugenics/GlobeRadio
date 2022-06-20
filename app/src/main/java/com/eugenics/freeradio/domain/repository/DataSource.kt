package com.eugenics.freeradio.domain.repository

import com.eugenics.freeradio.data.dto.SearchStationRespond
import kotlinx.coroutines.flow.Flow

interface DataSource {
    suspend fun getStationsByName(name: String): List<SearchStationRespond>
    suspend fun getStationsByTag(tag: String): List<SearchStationRespond>
    suspend fun getStations(): List<SearchStationRespond>
}