package com.eugenics.data.data.datastore

import android.util.Log
import androidx.datastore.core.Serializer
import com.eugenics.core.model.Favorites
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object FavoritesSerializer : Serializer<Favorites> {
    private const val TAG = "FAVORITES_SERIALIZER"

    override val defaultValue: Favorites
        get() = Favorites.emptyInstance()

    override suspend fun readFrom(input: InputStream): Favorites =
        try {
            Json.decodeFromString(
                deserializer = Favorites.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            defaultValue
        }

    override suspend fun writeTo(t: Favorites, output: OutputStream) {
        withContext(Dispatchers.IO) {
            try {
                output.write(
                    Json.encodeToString(
                        serializer = Favorites.serializer(), value = t
                    ).toByteArray(Charsets.UTF_8)
                )
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }
}