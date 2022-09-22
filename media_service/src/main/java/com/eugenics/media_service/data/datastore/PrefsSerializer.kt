package com.eugenics.media_service.data.datastore

import android.util.Log
import androidx.datastore.core.Serializer
import com.eugenics.media_service.domain.model.CurrentPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object PrefsSerializer : Serializer<CurrentPrefs> {
    private const val TAG = "ServiceDataStoreSerializer"
    override val defaultValue: CurrentPrefs = CurrentPrefs.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): CurrentPrefs =
        try {
            Json.decodeFromString(
                deserializer = CurrentPrefs.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (ex: Exception) {
            Log.e(TAG, ex.message.toString())
            CurrentPrefs.getDefaultInstance()
        }

    override suspend fun writeTo(t: CurrentPrefs, output: OutputStream) {
        try {
            withContext(Dispatchers.IO) {
                output.write(
                    Json.encodeToString(
                        serializer = CurrentPrefs.serializer(),
                        value = t
                    ).encodeToByteArray()
                )
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.message.toString())
        }
    }
}