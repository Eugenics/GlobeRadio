package com.eugenics.data.data.datasources

import com.eugenics.core_database.database.dao.PrefsDao
import com.eugenics.core_database.database.enteties.PrefsDaoObject
import com.eugenics.data.interfaces.IPrefsLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrefsLocalDataSource(private val prefsDao: PrefsDao) : IPrefsLocalDataSource {

    private val ioDispatcher = Dispatchers.IO

    override suspend fun fetchPrefs(): PrefsDaoObject =
        withContext(ioDispatcher) {
            prefsDao.fetchPrefs() ?: PrefsDaoObject.newInstance()
        }

    override suspend fun updatePrefs(prefs: PrefsDaoObject) {
        withContext(ioDispatcher) {
            prefsDao.updatePrefs(prefsObject = prefs)
        }
    }

    override suspend fun insertPrefs(prefs: PrefsDaoObject) {
        withContext(ioDispatcher) {
            prefsDao.insertPrefs(prefsObject = prefs)
        }
    }

    override suspend fun deletePrefs() {
        withContext(ioDispatcher) {
            prefsDao.deletePrefs()
        }
    }
}