package com.eugenics.media_service.util

import android.util.Log
import com.eugenics.core.model.CurrentPrefs
import com.eugenics.data.interfaces.IPrefsRepository

class PrefsHelper(private val prefsRepository: IPrefsRepository) {

    suspend fun setPrefs(
        tag: String,
        stationUuid: String,
        command: String,
        query: String
    ) {
        Log.d(TAG, "SET PREFS: $command")
        prefsRepository.fetchPrefs().apply {
            prefsRepository.insertPrefs(
                CurrentPrefs(
                    uuid = this.uuid,
                    tag = tag,
                    stationUuid = stationUuid,
                    command = command,
                    query = query
                )
            )
        }
    }

    suspend fun getPrefs(): CurrentPrefs {
        Log.d(TAG, "GET PREFS...")
        return prefsRepository.fetchPrefs()
    }

    companion object {
        const val TAG = "PrefsHelper"
    }
}