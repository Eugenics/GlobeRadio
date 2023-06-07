package com.eugenics.core.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    @SerializedName("key")
    val name: String = "",
    @SerializedName("value")
    val value: String = ""
)
