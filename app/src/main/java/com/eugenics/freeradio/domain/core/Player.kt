package com.eugenics.freeradio.domain.core

import android.content.Context
import androidx.media3.common.MediaItem

interface Player {
    fun initialize(context: Context): Player
    fun prepare()
    fun play()
    fun pause()
    fun next()
    fun previous()
    fun release()
    fun getItemsCount(): Int
    fun seekPosition(mediaItemIndex: Int)
    fun addMediaItems(mediaItems: List<MediaItem>)
    fun addMediaItem(index: Int, item: MediaItem)
}