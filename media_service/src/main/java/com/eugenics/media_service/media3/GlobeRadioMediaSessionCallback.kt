package com.eugenics.media_service.media3

import android.os.Bundle
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSession.ConnectionResult.AcceptedResultBuilder
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.eugenics.core.constants.MEDIA_ITEM_ID_KEY
import com.eugenics.core.constants.SET_FAVORITES_COMMAND
import com.eugenics.media_service.media3.core.GlobeRadioSessionCommands
import com.eugenics.media_service.media3.data.ContentDataSource
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

@UnstableApi
class GlobeRadioMediaSessionCallback(
    private val contentDataSource: ContentDataSource
) : MediaLibrarySession.Callback {

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): ConnectionResult {
        Log.d(TAG, "ON_CONNECT...")

        val customCommands =
            mutableListOf<SessionCommand>().apply {
                addAll(GlobeRadioSessionCommands.customSessionCommandsList)
                addAll(ConnectionResult.DEFAULT_SESSION_COMMANDS.commands)
            }

        val sessionCommands = ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS
            .buildUpon()
            .apply {
                GlobeRadioSessionCommands.customSessionCommandsList.forEach { command ->
                    this.add(command)
                }
            }
            .build()
        return AcceptedResultBuilder(session)
            .setAvailableSessionCommands(sessionCommands)
            .build()
    }

    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> {
        return Futures.immediateFuture(
            LibraryResult.ofItem(contentDataSource.getRootItem(), params)
        )
    }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        Log.d(TAG, "ON_ADD_MEDIA_ITEMS: ${mediaItems.size}")
        val resoledMediaItems = contentDataSource.resolveMediaItems(mediaItems)

        return Futures.immediateFuture(resoledMediaItems.toMutableList())
    }

    override fun onGetItem(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String
    ): ListenableFuture<LibraryResult<MediaItem>> {
        Log.d(TAG, "ON_GET_MEDIA_ITEM...:$mediaId")
        return Futures.immediateFuture(
            LibraryResult.ofItem(contentDataSource.getMediaItem(mediaId).get(), null)
        )
    }


    override fun onSetMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        Log.d(TAG, "ON_SET_MEDIA_ITEMS...")
        return Futures.immediateFuture(
            MediaSession.MediaItemsWithStartPosition(
                contentDataSource.resolveMediaItems(mediaItems),
                startIndex,
                startPositionMs
            )
        )
    }

    override fun onSearch(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        query: String,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<Void>> {
        Log.d(TAG, "ON_SEARCH...")
        contentDataSource.searchMediaItems(query = query).addListener(
            {
                session.notifySearchResultChanged(
                    browser, query,
                    contentDataSource.getMediaTree()
                        .nodes[ContentDataSource.SEARCH_ID]
                        ?.children
                        ?.size
                        ?: 0, params
                )
            },
            MoreExecutors.directExecutor()
        )
        return Futures.immediateFuture(LibraryResult.ofVoid())
    }

    override fun onGetSearchResult(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        query: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
        return Futures.immediateFuture(
            LibraryResult.ofItemList(
                contentDataSource.getMediaTree()
                    .nodes[ContentDataSource.SEARCH_ID]
                    ?.children
                    ?: listOf(),
                params
            )
        )
    }

    override fun onGetChildren(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
        val mediaTree = contentDataSource.getMediaTree()
        val parentNode = mediaTree.nodes[parentId]
        val childItemsList = parentNode?.children ?: listOf()
        Log.d(TAG, "ON_GET_CHILDREN: $parentId")
        return Futures.immediateFuture(LibraryResult.ofItemList(childItemsList, params))
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        Log.d(TAG, "SESSION CUSTOM COMMAND...")
        val sessionResult = when (customCommand.customAction) {
            SET_FAVORITES_COMMAND -> {
                contentDataSource.setFavorite(
                    args.getString(MEDIA_ITEM_ID_KEY).toString()
                )
            }

            else -> SessionResult(SessionResult.RESULT_ERROR_BAD_VALUE)

        }
        return Futures.immediateFuture(sessionResult)
    }

    companion object {
        private const val TAG = "GLOBAL_RADIO_MEDIA_SESSION_CALLBACK"
    }
}