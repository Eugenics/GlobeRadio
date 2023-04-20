package com.eugenics.core.model

data class StreamTitle(
    val artist: String = "",
    val title: String = ""
) {
    override fun toString(): String =
        "Artist: $artist; Title: $title"
}
