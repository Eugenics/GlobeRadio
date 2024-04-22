package com.eugenics.freeradio.util.data

import com.eugenics.core.model.Tag
import com.fasterxml.jackson.annotation.JsonSetter

data class TagsPojo(
    @JsonSetter("key")
    val name: String = "",
    @JsonSetter("value")
    val value: String = ""
)

fun TagsPojo.mapToModel(): Tag =
    Tag(
        name = this.name,
        value = this.value
    )
