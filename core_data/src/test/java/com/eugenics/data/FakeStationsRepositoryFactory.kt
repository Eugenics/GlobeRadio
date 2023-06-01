package com.eugenics.data

import com.eugenics.data.data.repository.StationsRepository
import com.eugenics.data.interfaces.IStationsLocalDataSource
import com.eugenics.data.interfaces.IStationsRemoteDataSource
import com.eugenics.data.interfaces.IStationsRepository

class FakeStationsRepositoryFactory(
    private val fakeStationsLocalDataSource: IStationsLocalDataSource =
        FakeStationsLocalDataSource(),
    private val fakeStationsRemoteDataSource: IStationsRemoteDataSource =
        FakeStationsRemoteDataSource()
) {
    fun create(): IStationsRepository = StationsRepository(
        stationsLocalDataSource = fakeStationsLocalDataSource,
        stationsRemoteDataSource = fakeStationsRemoteDataSource
    )
}
