package com.eugenics.data.testing

import com.eugenics.data.data.repository.PrefsRepository
import com.eugenics.data.interfaces.IPrefsRepository

object FakePrefsRepositoryFactory {
    fun create(): IPrefsRepository = PrefsRepository(prefsDataSource = FakePrefsLocalDataSource())
}