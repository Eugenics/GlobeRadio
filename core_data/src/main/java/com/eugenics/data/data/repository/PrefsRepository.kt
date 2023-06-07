package com.eugenics.data.data.repository

import com.eugenics.core.model.CurrentPrefs
import com.eugenics.core_database.database.enteties.asModel
import com.eugenics.data.data.util.asDao
import com.eugenics.data.interfaces.IPrefsLocalDataSource
import com.eugenics.data.interfaces.IPrefsRepository

class PrefsRepository(private val prefsDataSource: IPrefsLocalDataSource) : IPrefsRepository {
    override suspend fun fetchPrefs(): CurrentPrefs =
        prefsDataSource.fetchPrefs().asModel()

    override suspend fun updatePrefs(prefs: CurrentPrefs) {
        prefsDataSource.updatePrefs(prefs = prefs.asDao())
    }

    override suspend fun insertPrefs(prefs: CurrentPrefs) {
        prefsDataSource.insertPrefs(prefs = prefs.asDao())
    }

    override suspend fun deletePrefs() {
        prefsDataSource.deletePrefs()
    }
}