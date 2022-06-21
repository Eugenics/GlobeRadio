package com.eugenics.freeradio.domain.usecases

import com.eugenics.freeradio.domain.model.Station
import com.eugenics.freeradio.domain.model.convertToDao
import com.eugenics.freeradio.domain.repository.Repository

class InsertStationIntoDbUseCase(private val repository: Repository) {
    suspend operator fun invoke(station: Station) {
        repository.insertStation(stationDao = station.convertToDao())
    }
}