package com.eugenics.media_service.util

import android.util.Log
import com.eugenics.core.model.CurrentPrefs
import com.eugenics.data.interfaces.IPrefsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class PrefsHelper(private val prefsRepository: IPrefsRepository) {

    private val prefsChannel = Channel<CurrentPrefs>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        collectPrefsChannel()
    }

    fun setPrefs(prefs: CurrentPrefs) {
        coroutineScope.launch {
            prefsChannel.send(prefs)
            Log.d(TAG, "SEND PREFS: $prefs")
        }
    }

    suspend fun getPrefs(): CurrentPrefs {
        Log.d(TAG, "GET PREFS...")
        return prefsRepository.fetchPrefs()
    }

    private fun collectPrefsChannel() {
        coroutineScope.launch {
            prefsChannel.consumeEach {
                prefsRepository.deletePrefs()
                prefsRepository.insertPrefs(it)
                Log.d(TAG, "WRITE PREFS: $it")
            }
        }
    }

    companion object {
        const val TAG = "PrefsHelper"
    }
}