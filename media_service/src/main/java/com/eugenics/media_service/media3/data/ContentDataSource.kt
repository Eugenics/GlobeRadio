package com.eugenics.media_service.media3.data

import android.content.Context
import android.content.res.AssetManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.SubtitleConfiguration
import androidx.media3.common.MediaMetadata
import androidx.media3.common.ThumbRating
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.SessionResult
import com.eugenics.core.model.Station
import com.eugenics.data.interfaces.IStationsRepository
import com.eugenics.media_service.util.IOHelper
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import org.json.JSONArray
import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Paths

class ContentDataSource(
    private val context: Context,
    private val mediaSourceRepository: IStationsRepository
) {

    private val ioDispatcher = Dispatchers.IO
    private val scopeErrorHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, exception.toString())
    }
    private val scope = CoroutineScope(scopeErrorHandler)

    private val mediaTree = MediaTree(mutableMapOf())

    fun getMediaTree(): MediaTree = mediaTree

    init {
        initialiseMediaTree()
    }

    private fun initialiseMediaRoot(): MediaItem {
        val rootItem = buildParentMediaItem(mediaId = ROOT_ID, title = "Root folder")
        mediaTree.addNode(
            MediaTreeNode.generate(
                id = ROOT_ID,
                rootItem
            )
        )
        return rootItem
    }

    private fun initialiseMediaTree() {
        initialiseMediaRoot()
        val nodes = JSONArray(loadJSONFromAsset(context.assets))
        for (i in 0 until nodes.length()) {
            val mediaItem = buildParentMediaItem(
                mediaId = nodes.getJSONObject(i).getString("key"),
                title = nodes.getJSONObject(i).getString("value")
            )
            mediaTree.addNode(
                MediaTreeNode.generate(
                    id = mediaItem.mediaId,
                    item = mediaItem
                )
            )
            mediaTree.nodes[ROOT_ID]?.addChild(mediaItem)
        }
        val favoritesMediaItem = buildParentMediaItem(mediaId = FAVORITES_ID, title = "favorites")
        mediaTree.addNode(MediaTreeNode.generate(id = FAVORITES_ID, item = favoritesMediaItem))
        mediaTree.nodes[ROOT_ID]?.addChild(favoritesMediaItem)
    }

    private fun loadJSONFromAsset(assets: AssetManager): String =
        assets.open(TAGS_JSON_FILE_NAME).bufferedReader().use(BufferedReader::readText)

    fun getRootItem(): MediaItem = mediaTree.nodes[ROOT_ID]?.item ?: initialiseMediaRoot()

    fun searchMediaItems(query: String): ListenableFuture<List<MediaItem>> {
        Log.d(TAG, "SEARCH_MEDIA_ITEMS: $query")
        val searchNode = mediaTree.nodes[SEARCH_ID] ?: mediaTree.addNode(
            MediaTreeNode.generate(
                id = SEARCH_ID, buildMediaItem(
                    title = "Search folder",
                    isPlayable = false,
                    isBrowsable = true,
                    mediaId = SEARCH_ID,
                    mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED
                )
            )
        )
        searchNode.children.clear()
        val searchResult = scope.future(ioDispatcher) {
            val stations =
                mediaSourceRepository.getLocalStationByName(name = query).map { station ->
                    station.toMediaItem()
                }
            searchNode.children.addAll(stations)
            stations
        }
        return Futures.immediateFuture(searchResult.get())
    }

    fun getMediaItem(mediaId: String): ListenableFuture<MediaItem> {
        Log.d(TAG, "GET_ITEM_BY_ID: $mediaId")
        val parentNode = mediaTree.nodes[mediaId] ?: mediaTree.addNode(
            MediaTreeNode.generate(
                id = mediaId, buildMediaItem(
                    title = "${mediaId.lowercase()} folder",
                    isPlayable = false,
                    isBrowsable = true,
                    mediaId = mediaId,
                    mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED
                )
            )
        )
        val result = scope.future(ioDispatcher) {
            clearNodeChildren(mediaId)
            if (mediaSourceRepository.checkLocalStations().isEmpty()) {
                uploadStationsFullList(context)
            }
            val mediaItemsList = when (mediaId) {
                FAVORITES_ID -> mediaSourceRepository.fetchStationsByFavorites()
                else -> {
                    mediaSourceRepository.getLocalStationByTag(tag = "%${mediaId.lowercase()}%")
                }
            }.map { station -> station.toMediaItem() }
            mediaItemsList.forEach { item ->
                mediaTree.addNode(
                    MediaTreeNode.generate(
                        id = item.mediaId,
                        item = item
                    )
                )
            }
            parentNode.children.addAll(mediaItemsList)
            parentNode.item
        }
        return Futures.immediateFuture(result.get())
    }

    fun resolveMediaItems(mediaItems: List<MediaItem>): List<MediaItem> {
        val resolveList = mutableListOf<MediaItem>()
        mediaItems.filter { it.mediaMetadata.isPlayable == true }.forEach { item ->
            mediaTree.nodes[item.mediaId]?.let { node ->
                resolveList.add(node.item)
            }
        }
        return resolveList
    }

    private suspend fun uploadStationsFullList(context: Context) {
        Log.d(TAG, "Start stations upload...")
        var cacheEnable = true
        if (!Files.exists(Paths.get("${context.cacheDir.absolutePath}/$STATIONS_JSON_NAME"))) {
            Log.d(TAG, "Cache copy...")
            cacheEnable = IOHelper.copyAssetToCache(STATIONS_ZIP_ASSET_NAME, context)
            if (cacheEnable) {
                Log.d(TAG, "Start stations unzip...")
                cacheEnable = IOHelper.unzipStationsFullList(
                    zipFrom = "${context.cacheDir.absolutePath}/$STATIONS_ZIP_ASSET_NAME",
                    pathTo = "${context.cacheDir.absolutePath}/$STATIONS_JSON_NAME"
                )
            }
        }

        if (cacheEnable) {
            Log.d(TAG, "Start stations upload to db...")
            val jsonFilePath = "${context.cacheDir.absolutePath}/$STATIONS_JSON_NAME"
            val stations = IOHelper.parseJsonToList(
                filePath = jsonFilePath,
                StationPojo::class.java
            )
                .map { it.mapToModel() }
            mediaSourceRepository.reloadStations(stations = stations)
            Log.d(TAG, "Send command for reload stations...")
        }
    }

    fun setFavorite(mediaId: String): SessionResult {
        val sessionResult = scope.future {
            mediaTree.nodes[mediaId]?.item?.let { mediaItem ->
                if (mediaItem.toStation().isFavorite == 1)
                    mediaSourceRepository.deleteFavorite(mediaId)
                else mediaSourceRepository.addFavorite(mediaId)
                SessionResult.RESULT_SUCCESS
            } ?: SessionResult.RESULT_ERROR_BAD_VALUE

        }
        return SessionResult(sessionResult.get())
    }

    private fun clearNodeChildren(parentNodeId: String) {
        mediaTree.nodes[parentNodeId]?.children?.forEach { child ->
            mediaTree.nodes.remove(child.mediaId)
        }
        mediaTree.nodes[parentNodeId]?.children?.clear()
    }

    companion object {
        private const val TAG = "MEDIA_CONTENT_DATA_SOURCE"
        const val ROOT_ID = "ROOT_ID"
        const val SEARCH_ID = "SEARCH_ID"
        const val FAVORITES_ID = "FAVORITES"
        private const val TAGS_JSON_FILE_NAME = "station_tags.json"
        const val MEDIA_ITEM_STATION_EXTRAS_KEY = "MEDIA_ITEM_STATION_SOURCE"
        private const val STATIONS_ZIP_ASSET_NAME = "full.zip"
        private const val STATIONS_JSON_NAME = "full.json"
        const val MEDIA_ID_KEY = "MEDIA_ID_KEY"

        @OptIn(UnstableApi::class)
        fun buildMediaItem(
            title: String,
            mediaId: String,
            isPlayable: Boolean,
            isBrowsable: Boolean,
            mediaType: @MediaMetadata.MediaType Int,
            subtitleConfigurations: List<SubtitleConfiguration> = mutableListOf(),
            album: String? = null,
            artist: String? = null,
            genre: String? = null,
            sourceUri: Uri? = null,
            imageUri: Uri? = null
        ): MediaItem {
            val metadata =
                MediaMetadata.Builder()
                    .setAlbumTitle(album)
                    .setTitle(title)
                    .setArtist(artist)
                    .setGenre(genre)
                    .setIsBrowsable(isBrowsable)
                    .setIsPlayable(isPlayable)
                    .setArtworkUri(imageUri)
                    .setMediaType(mediaType)
                    .setStation(sourceUri.toString())
                    .build()

            return MediaItem.Builder()
                .setMediaId(mediaId)
                .setSubtitleConfigurations(subtitleConfigurations)
                .setMediaMetadata(metadata)
                .setUri(sourceUri)
                .build()
        }

        fun buildParentMediaItem(mediaId: String, title: String): MediaItem = buildMediaItem(
            title = title,
            mediaId = mediaId,
            isPlayable = false,
            isBrowsable = true,
            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED
        )
    }
}

fun Station.toMediaItem(): MediaItem {
    val mediaExtras = Bundle()
    mediaExtras.putParcelable(ContentDataSource.MEDIA_ITEM_STATION_EXTRAS_KEY, this)
    val mediaMetadata = MediaMetadata.Builder()
        .setDisplayTitle(this.name)
        .setDescription(this.name + ":" + this.tags)
        .setSubtitle(this.tags)
        .setArtworkUri(Uri.parse(this.favicon))
        .setExtras(mediaExtras)
        .setIsPlayable(true)
        .setIsBrowsable(false)
        .setUserRating(ThumbRating(this.isFavorite == 1))
        .build()
    return MediaItem.Builder()
        .setMediaId(this.stationuuid)
        .setMediaMetadata(mediaMetadata)
        .setUri(Uri.parse(this.urlResolved))
        .build()
}

private fun MediaItem.toStation(): Station =
    (if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        this.mediaMetadata.extras?.getParcelable(ContentDataSource.MEDIA_ITEM_STATION_EXTRAS_KEY)
    } else {
        this.mediaMetadata.extras?.getParcelable(
            ContentDataSource.MEDIA_ITEM_STATION_EXTRAS_KEY, Station::class.java
        )
    }) ?: Station.newInstance()