package com.eugenics.data

import com.eugenics.core_database.database.enteties.asModel
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class PrefsRepositoryUnitTest {
    private val fakePrefsRepository = FakePrefsRepositoryFactory.create()

    @Test
    fun fetchPrefs_isCorrect() = runTest {
        assertEquals(
            FakePrefsLocalDataSource.fakePrefsDaoObject.asModel(),
            fakePrefsRepository.fetchPrefs()
        )
    }
}