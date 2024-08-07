package com.eugenics.media_service.media3.data

import com.eugenics.core.model.Tag
import com.fasterxml.jackson.annotation.JsonSetter

data class TagsPojo(
    @JsonSetter("key")
    val name: String = "",
    @JsonSetter("value")
    val value: String = ""
)
