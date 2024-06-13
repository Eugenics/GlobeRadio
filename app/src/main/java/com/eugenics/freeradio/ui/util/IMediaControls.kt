package com.eugenics.freeradio.ui.util

interface IMediaControls {
    fun play()
    fun play(mediaId: String)
    fun search(query:String)
    fun stop()
    fun prev()
    fun next()
    fun pause()
}