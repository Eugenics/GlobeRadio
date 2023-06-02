package com.eugenics.data

import com.eugenics.core_database.database.enteties.asModel
import com.eugenics.data.data.util.LOADING_MESSAGE
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class StationsRepositoryUnitTest {
    private val fakeStationsRepository = FakeStationsRepositoryFactory.create()
    private val stationsList = FakeStationsLocalDataSource.fakeStationsDaoObjects
        .map { stationDaoObject -> stationDaoObject.asModel() }

    @Test
    fun fetchRemoteStations_isCorrect() = runTest {
        val response = fakeStationsRepository.getRemoteStations().first()
        assertTrue(response.message == LOADING_MESSAGE)
    }

    @Test
    fun fetchLocalStations_isCorrect() = runTest {
        assertTrue(fakeStationsRepository.getLocalStations().isNotEmpty())
    }

    @Test
    fun fetchLocalStations_isCorrectList() = runTest {
        assertEquals(
            stationsList,
            fakeStationsRepository.getLocalStations()
        )
    }
}