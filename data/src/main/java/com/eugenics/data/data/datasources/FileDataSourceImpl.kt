package com.eugenics.data.data.datasources

import android.content.Context
import android.util.Log
import com.eugenics.core.model.Tag
import com.eugenics.data.interfaces.repository.IFileDataSource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FileDataSourceImpl(
    private val applicationContext: Context
) : IFileDataSource {
    override fun getTags(): List<Tag> =
        try {
            val jsonString = applicationContext.assets.open("station_tags.json")
                .bufferedReader()
                .readText()
            val tagsListType = object : TypeToken<List<Tag>>() {}.type
            Gson().fromJson(jsonString, tagsListType)
        } catch (e: Exception) {
            Log.e("Read JSON", e.message.toString())
            listOf()
        }
}