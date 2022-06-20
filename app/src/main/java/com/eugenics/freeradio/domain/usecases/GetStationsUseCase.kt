package com.eugenics.freeradio.domain.usecases

import com.eugenics.freeradio.domain.model.Station
import com.eugenics.freeradio.domain.repository.Repository

class GetStationsUseCase(private val repository: Repository) {
    suspend operator fun invoke(): List<Station> =
        repository.getAllStations()
            .filter { it.tags.isNotBlank() }
            .map { it.convertToModel() }
}