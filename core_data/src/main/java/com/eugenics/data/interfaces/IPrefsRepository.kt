package com.eugenics.data.interfaces

import com.eugenics.core.interfaces.IRepository
import com.eugenics.core.model.CurrentPrefs

interface IPrefsRepository : IRepository {
    suspend fun fetchPrefs(): CurrentPrefs
    suspend fun updatePrefs(prefs: CurrentPrefs)
    suspend fun insertPrefs(prefs: CurrentPrefs)
    suspend fun deletePrefs()
}