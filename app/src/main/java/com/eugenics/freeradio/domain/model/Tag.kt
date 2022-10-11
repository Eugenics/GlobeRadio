package com.eugenics.freeradio.domain.model

import com.google.gson.annotations.SerializedName

data class Tag(
    @SerializedName("key")
    val name: String = "",
    @SerializedName("value")
    val value: String = ""
)
