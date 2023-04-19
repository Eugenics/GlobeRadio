package com.eugenics.data.data.datastore

import androidx.datastore.core.DataStore
import com.eugenics.core.model.CurrentPrefs
import com.eugenics.core.interfaces.IDataSource
import kotlinx.coroutines.flow.Flow

class PrefsDataSource(private val dataStore: DataStore<CurrentPrefs>) : IDataSource {

    fun getPrefs(): Flow<CurrentPrefs> = dataStore.data

    suspend fun setPrefs(prefs: CurrentPrefs) {
        dataStore.updateData {
            prefs
        }
    }
}