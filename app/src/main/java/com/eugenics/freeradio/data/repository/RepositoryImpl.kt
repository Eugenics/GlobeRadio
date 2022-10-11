package com.eugenics.freeradio.data.repository

import android.app.Application
import androidx.media3.common.util.Log
import com.eugenics.freeradio.domain.interfaces.Repository
import com.eugenics.freeradio.domain.model.Tag
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RepositoryImpl(private val application: Application) : Repository {
    override fun getTags(): List<Tag> =
        try {
            val jsonString = application.assets.open("station_tags.json")
                .bufferedReader()
                .readText()
            val tagsListType = object : TypeToken<List<Tag>>() {}.type
            Gson().fromJson(jsonString, tagsListType)
        } catch (e: Exception) {
            Log.e("Read JSON", e.message.toString())
            listOf()
        }
}