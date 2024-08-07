package com.eugenics.ui.compose.screens.main.data

import android.os.Bundle
import com.eugenics.ui_core.data.interfaces.ScreenHandlers

data class MainScreenHandlers(
    val onPlayClick: (mediaId: String?, playThemReady: Boolean) -> Unit,
    val onSearchClick: (query: String) -> Unit,
    val onGetTagItem: (itemId: String) -> Unit,
    val onSendCommand: (command: String,bundle: Bundle?) -> Unit,
    val onVisibleIndexChange: (index: Int) -> Unit,
) : ScreenHandlers {
    companion object {
        fun newInstance(
            onPlayClick: (mediaId: String?, playThenReady: Boolean) -> Unit,
            onSearchClick: (query: String) -> Unit,
            onGetTagItem: (itemId: String) -> Unit,
            onSendCommand: (command: String, bundle: Bundle?) -> Unit,
            onVisibleIndexChange: (index: Int) -> Unit
        ): MainScreenHandlers =
            MainScreenHandlers(
                onPlayClick = onPlayClick,
                onSearchClick = onSearchClick,
                onGetTagItem = onGetTagItem,
                onSendCommand = onSendCommand,
                onVisibleIndexChange = onVisibleIndexChange
            )
    }
}