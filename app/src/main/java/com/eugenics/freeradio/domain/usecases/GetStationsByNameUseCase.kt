package com.eugenics.freeradio.domain.usecases

import com.eugenics.freeradio.domain.model.Station
import com.eugenics.freeradio.domain.repository.Repository

class GetStationsByNameUseCase(private val repository: Repository) {
    suspend operator fun invoke(name: String): List<Station> =
        repository.getStationsByName(name = name)
            .filter { it.tags.isNotBlank() }
            .map { it.convertToModel() }
}