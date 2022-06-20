package com.eugenics.freeradio.domain.usecases

import com.eugenics.freeradio.domain.model.Station
import com.eugenics.freeradio.domain.repository.Repository

class GetStationsByTagLocalUseCase(private val repository: Repository) {
    suspend operator fun invoke(tag: String): List<Station> =
        repository.getLocalStationByTag(tag = tag).map { it.convertToModel() }
}