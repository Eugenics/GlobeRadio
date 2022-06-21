package com.eugenics.media_service.player

import android.content.Context
import com.eugenics.media_service.domain.model.PlayerMediaItem

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
    fun addMediaItems(mediaItems: List<PlayerMediaItem>)
    fun addMediaItem(index: Int, item: PlayerMediaItem)
}