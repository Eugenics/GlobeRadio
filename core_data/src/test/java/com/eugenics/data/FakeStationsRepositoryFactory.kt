package com.eugenics.data

import com.eugenics.data.data.repository.StationsRepository
import com.eugenics.data.interfaces.IStationsRepository

object FakeStationsRepositoryFactory {
    fun create(): IStationsRepository = StationsRepository(
        stationsLocalDataSource = FakeStationsLocalDataSource(),
        stationsRemoteDataSource = FakeStationsRemoteDataSource()
    )
}
