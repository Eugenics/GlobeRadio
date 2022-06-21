package com.eugenics.media_service.player

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import com.eugenics.media_service.domain.model.PlayerMediaItem
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

    override fun addMediaItems(mediaItems: List<PlayerMediaItem>) {
        val items = mutableListOf<MediaItem>()
        for ((index, item) in mediaItems.withIndex()) {
            items.add(index, MediaItem.fromUri(item.urlResolved))
        }
        player?.addMediaItems(items)
    }

    override fun addMediaItem(
        index: Int,
        item: PlayerMediaItem
    ) {
        MediaItem.Builder()
            .setUri(item.urlResolved)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setDisplayTitle(item.name)
                    .build()
            )
            .build()
            .also { mediaItem ->
                player?.let {
                    try {
                        it.addMediaItem(index, mediaItem)
                    } catch (ex: IllegalStateException) {
                        Log.d(
                            "ITEMS", ex.message.toString()
                                    + " - item: ${mediaItem.mediaMetadata.displayTitle}"
                        )
                    }
                }
            }
    }
}