package com.eugenics.freeradio.ui.util

data class StreamTitle(
    val artist: String = "",
    val title: String = ""
) {
    override fun toString(): String =
        "Artist: $artist; Title: $title"
}
