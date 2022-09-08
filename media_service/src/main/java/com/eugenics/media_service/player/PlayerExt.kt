package com.eugenics.media_service.player

import android.os.Bundle
import android.util.Log
import androidx.core.net.toUri
import com.eugenics.media_service.domain.model.PlayerMediaItem
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import java.lang.IllegalStateException

fun Player.getItemsCount(): Int = this.mediaItemCount

fun Player.addMediaItems(mediaItems: List<PlayerMediaItem>) {
    val items = mutableListOf<MediaItem>()
    for ((index, item) in mediaItems.withIndex()) {
        val extras = Bundle()
        extras.putParcelable("STATION", item)

        val mediaItem = MediaItem.Builder()
            .setMediaId(item.uuid)
            .setTag(item.tags)
            .setUri(item.urlResolved)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(item.name)
                    .setSubtitle(item.tags)
                    .setDisplayTitle(item.name)
                    .setExtras(extras)
                    .setArtworkUri(item.favicon.toUri())
                    .build()
            )
            .build()
        items.add(index, mediaItem)
    }
    this.addMediaItems(items)
}

fun Player.addMediaItem(
    index: Int,
    item: PlayerMediaItem
) {
    val extras = Bundle()
    extras.putParcelable("STATION", item)

    MediaItem.Builder()
        .setUri(item.urlResolved)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setDisplayTitle(item.name)
                .setExtras(extras)
                .build()
        )
        .build()
        .also { mediaItem ->
            try {
                this.addMediaItem(index, mediaItem)
            } catch (ex: IllegalStateException) {
                Log.d(
                    "ITEMS", ex.message.toString()
                            + " - item: ${mediaItem.mediaMetadata.displayTitle}"
                )
            }
        }
}

fun Player.getMediaItems(): List<MediaItem> {
    val mediaItemsList = mutableListOf<MediaItem>()
    val items = this.getItemsCount()
    if (items > 0) {
        for (i in 0 until items) {
            mediaItemsList.add(
                this.getMediaItemAt(i)
            )
        }
    }
    return mediaItemsList.toList()
}