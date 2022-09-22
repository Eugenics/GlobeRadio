package com.eugenics.freeradio.data.local.ref

import androidx.datastore.core.DataStore
import com.eugenics.freeradio.domain.model.CurrentState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsDataSource @Inject constructor(
    private val dataStore: DataStore<CurrentState>
) {

    fun getSettings(): Flow<CurrentState> = dataStore.data

    suspend fun setSettings(settings: CurrentState) {
        dataStore.updateData {
            settings
        }
    }

}