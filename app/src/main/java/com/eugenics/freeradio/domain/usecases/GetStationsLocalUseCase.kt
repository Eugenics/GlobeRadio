package com.eugenics.freeradio.domain.usecases

import com.eugenics.freeradio.domain.model.Station
import com.eugenics.freeradio.domain.repository.Repository

class GetStationsLocalUseCase(private val repository: Repository) {
    suspend operator fun invoke(): List<Station> =
        repository.getLocalStations().map { it.convertToModel() }
}