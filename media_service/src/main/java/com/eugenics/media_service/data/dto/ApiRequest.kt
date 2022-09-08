package com.eugenics.media_service.data.dto

import com.google.gson.annotations.SerializedName

data class ApiRequest(
    @SerializedName("codec[]")
    val codec: List<String> = listOf("MP3", "AAC%2B")
)
