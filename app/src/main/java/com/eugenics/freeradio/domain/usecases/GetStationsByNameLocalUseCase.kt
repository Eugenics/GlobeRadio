package com.eugenics.freeradio.domain.usecases

import com.eugenics.freeradio.domain.model.Station
import com.eugenics.freeradio.domain.repository.Repository

class GetStationsByNameLocalUseCase(private val repository: Repository) {
    suspend operator fun invoke(name: String): List<Station> =
        repository.getLocalStationByName(name = name).map { it.convertToModel() }
}