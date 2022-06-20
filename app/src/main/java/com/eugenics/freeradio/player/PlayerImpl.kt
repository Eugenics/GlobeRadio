package com.eugenics.freeradio.player

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.eugenics.freeradio.domain.core.Player
import java.lang.IllegalStateException

object PlayerImpl : Player {

    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L

    override fun initialize(context: Context): Player {
        player = ExoPlayer.Builder(context)
            .build()
            .apply {
                playWhenReady = true
                prepare()
            }
        return this
    }

    override fun prepare() {
        player?.let { exoPlayer ->
            exoPlayer.playWhenReady = true
            exoPlayer.prepare()
        }
    }

    override fun play() {
        player?.play()
    }

    override fun pause() {
        player?.pause()
    }

    override fun next() {
        player?.seekToNextMediaItem()
    }

    override fun previous() {
        player?.seekToPreviousMediaItem()
    }

    override fun release() {
        player?.let { exoPlayer ->
            currentItem = exoPlayer.currentMediaItemIndex
            playbackPosition = exoPlayer.currentPosition
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }

        player = null
    }

    override fun getItemsCount(): Int = player?.mediaItemCount ?: 0

    override fun seekPosition(mediaItemIndex: Int) {
        player?.seekTo(mediaItemIndex, 0L)

    }

    override fun addMediaItems(mediaItems: List<MediaItem>) {
        player?.addMediaItems(mediaItems)
    }

    override fun addMediaItem(index: Int, item: MediaItem) {
        player?.let {
            try {
                it.addMediaItem(index, item)
            } catch (ex: IllegalStateException) {
                Log.d("ITEMS", ex.message.toString() + " - item: ${item.mediaMetadata.displayTitle}")
            }
        }
    }
}