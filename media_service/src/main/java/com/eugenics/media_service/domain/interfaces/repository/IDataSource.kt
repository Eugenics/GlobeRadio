package com.eugenics.media_service.domain.interfaces.repository

import com.eugenics.media_service.data.dto.StationRespondObject


interface IDataSource {
    suspend fun getStationsByName(name: String): List<StationRespondObject>
    suspend fun getStationsByTag(tag: String): List<StationRespondObject>
    suspend fun getStations(): List<StationRespondObject>
}