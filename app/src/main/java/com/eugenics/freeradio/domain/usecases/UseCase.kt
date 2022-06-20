package com.eugenics.freeradio.domain.usecases

data class UseCase(
    val getStationsByNameUseCase: GetStationsByNameUseCase,
    val getStationsByTagUseCase: GetStationsByTagUseCase,
    val getStationsUseCase: GetStationsUseCase,
    val getStationsLocalUseCase: GetStationsLocalUseCase,
    val getStationsByNameLocalUseCase: GetStationsByNameLocalUseCase,
    val getStationsByTagLocalUseCase: GetStationsByTagLocalUseCase,
    val insertStationIntoDbUseCase: InsertStationIntoDbUseCase
)
