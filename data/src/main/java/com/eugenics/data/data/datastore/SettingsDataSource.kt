package com.eugenics.data.data.datastore

import androidx.datastore.core.DataStore
import com.eugenics.core.model.CurrentState
import com.eugenics.core.interfaces.IDataSource
import kotlinx.coroutines.flow.Flow

class SettingsDataSource(
    private val dataStore: DataStore<CurrentState>
) : IDataSource {

    fun getSettings(): Flow<CurrentState> = dataStore.data

    suspend fun setSettings(settings: CurrentState) {
        dataStore.updateData {
            settings
        }
    }

}