package com.eugenics.media_service.data.datastore

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.eugenics.media_service.domain.model.CurrentPrefs
import java.io.File

object PrefsDataStoreFactory {
    fun create(application: Application): DataStore<CurrentPrefs> =
        DataStoreFactory.create(
            serializer = PrefsSerializer,
            produceFile = {
                File(
                    application.filesDir,
                    "service_datastore/prefs.pb"
                )
            }
        )
}