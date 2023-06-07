package com.eugenics.data.interfaces

import com.eugenics.core.interfaces.IDataSource
import com.eugenics.core_database.database.enteties.PrefsDaoObject

interface IPrefsLocalDataSource : IDataSource {
    suspend fun fetchPrefs(): PrefsDaoObject
    suspend fun updatePrefs(prefs: PrefsDaoObject)
    suspend fun insertPrefs(prefs: PrefsDaoObject)
    suspend fun deletePrefs()
}