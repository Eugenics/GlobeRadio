package com.eugenics.media_service.util

import android.util.Log
import com.eugenics.core.model.CurrentPrefs
import com.eugenics.data.data.database.enteties.PrefsDaoObject
import com.eugenics.data.interfaces.repository.IRepository

class PrefsHelper(private val repository: IRepository) {

    suspend fun setPrefs(
        tag: String,
        stationUuid: String,
        command: String,
        query: String
    ) {
        Log.d(TAG, "SET PREFS: $command")
        val curPrefs = repository.fetchPrefs()
        if (curPrefs.isNotEmpty()) {
            repository.updatePrefs(
                PrefsDaoObject.newInstance(
                    uuid = curPrefs.first().uuid,
                    tag = tag,
                    stationUUID = stationUuid,
                    command = command,
                    query = query
                )
            )
        }
    }

    suspend fun getPrefs(): CurrentPrefs {
        Log.d(TAG, "GET PREFS...")
        val prefsList = repository.fetchPrefs()
        return if (prefsList.isEmpty()) {
            val prefsDaoObject = PrefsDaoObject.newInstance()
            repository.insertPrefs(prefs = prefsDaoObject)
            prefsDaoObject.convertToModel()
        } else {
            prefsList.first().convertToModel()
        }
    }

    companion object {
        const val TAG = "PrefsHelper"
    }
}