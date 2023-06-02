package com.eugenics.data

import android.util.Log
import com.eugenics.core.enums.Commands
import com.eugenics.core_database.database.enteties.PrefsDaoObject
import com.eugenics.data.interfaces.IPrefsLocalDataSource
import java.util.UUID

class FakePrefsLocalDataSource : IPrefsLocalDataSource {

    override suspend fun fetchPrefs(): PrefsDaoObject = fakePrefsDaoObject

    override suspend fun updatePrefs(prefs: PrefsDaoObject) {
        Log.d(TAG, "Prefs updated")
    }

    override suspend fun insertPrefs(prefs: PrefsDaoObject) {
        Log.d(TAG, "Prefs inserted")
    }

    override suspend fun deletePrefs() {
        Log.d(TAG, "Prefs deleted")
    }

    companion object {
        const val TAG = "FakePrefsDataSource"

        val fakePrefsDaoObject = PrefsDaoObject.newInstance(
            uuid = UUID.randomUUID().toString(),
            tag = "fake",
            stationUUID = UUID.randomUUID().toString(),
            command = Commands.FAVORITES_COMMAND.name,
            query = ""
        )
    }
}