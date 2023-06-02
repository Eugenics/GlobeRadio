package com.eugenics.data

import com.eugenics.data.data.repository.PrefsRepository
import com.eugenics.data.interfaces.IPrefsRepository

object FakePrefsRepositoryFactory {
    fun create(): IPrefsRepository = PrefsRepository(prefsDataSource = FakePrefsLocalDataSource())
}