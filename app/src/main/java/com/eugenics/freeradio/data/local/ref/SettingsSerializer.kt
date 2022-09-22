package com.eugenics.freeradio.data.local.ref

import android.util.Log
import androidx.datastore.core.Serializer
import com.eugenics.freeradio.domain.model.CurrentState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object SettingsSerializer : Serializer<CurrentState> {
    private const val TAG = "SettingsSerializer"
    override val defaultValue: CurrentState = CurrentState.getDefaultValueInstance()

    override suspend fun readFrom(input: InputStream): CurrentState =
        try {
            Json.decodeFromString(
                CurrentState.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (ex: Exception) {
            Log.e(TAG, ex.message.toString())
            defaultValue
        }

    override suspend fun writeTo(t: CurrentState, output: OutputStream) {
        withContext(Dispatchers.IO) {
            try {
                output.write(
                    Json.encodeToString(
                        serializer = CurrentState.serializer(),
                        value = t
                    ).encodeToByteArray()
                )

            } catch (ex: Exception) {
                Log.e(TAG, ex.message.toString())
            }
        }
    }
}