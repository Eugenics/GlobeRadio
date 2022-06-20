package com.eugenics.freeradio.domain.usecases

import com.eugenics.freeradio.domain.model.Station
import com.eugenics.freeradio.domain.repository.Repository

class GetStationsByTagUseCase(private val repository: Repository) {
    suspend operator fun invoke(tag: String): List<Station> =
        repository.getStationsByTag(tag = tag)
            .filter { it.tags.isNotBlank() }
            .map { it.convertToModel() }
}