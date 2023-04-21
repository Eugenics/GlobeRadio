package com.eugenics.media_service.util

import com.eugenics.core.model.CurrentPrefs
import com.eugenics.data.interfaces.repository.IRepository
import kotlinx.coroutines.flow.MutableStateFlow

class PrefsHelper(private val repository: IRepository) {

    suspend fun setPrefs(
        tag: String,
        stationUuid: String,
        command: String
    ) {
        repository.setPrefs(
            prefs = CurrentPrefs(
                tag = tag,
                stationUuid = stationUuid,
                command = command
            )
        )
    }

    suspend fun collectPrefs(
        prefs: MutableStateFlow<CurrentPrefs>,
        onPrefsChanged: (prefs: CurrentPrefs) -> Unit = { _ -> }
    ) {
        repository.getPrefs().collect {
            prefs.value = it
            onPrefsChanged(it)
        }
    }
}