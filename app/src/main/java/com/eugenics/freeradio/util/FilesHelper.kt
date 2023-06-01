package com.eugenics.freeradio.util

import android.content.Context
import android.util.Log
import com.eugenics.core.model.Tag
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FilesHelper {

    fun getTags(context: Context): List<Tag> =
        try {
            val jsonString = context.assets.open("station_tags.json")
                .bufferedReader()
                .readText()
            val tagsListType = object : TypeToken<List<Tag>>() {}.type
            Gson().fromJson(jsonString, tagsListType)
        } catch (e: Exception) {
            Log.e("Read JSON", e.message.toString())
            listOf()
        }
}