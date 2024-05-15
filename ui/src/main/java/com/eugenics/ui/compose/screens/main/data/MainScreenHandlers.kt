package com.eugenics.ui.compose.screens.main.data

import android.os.Bundle
import com.eugenics.ui_core.data.interfaces.ScreenHandlers

data class MainScreenHandlers(
    val onPlayClick: (mediaId: String?) -> Unit,
    val onSearchClick: (query: String) -> Unit,
    val onSendCommand: (command: String, parameters: Bundle?) -> Unit,
    val onFavoriteClick: (command: String, bundle: Bundle?) -> Unit,
    val onVisibleIndexChange: (index: Int) -> Unit,
) : ScreenHandlers {
    companion object {
        fun newInstance(
            onPlayClick: (mediaId: String?) -> Unit,
            onSearchClick: (query: String) -> Unit,
            onSendCommand: (command: String, parameters: Bundle?) -> Unit,
            onFavoriteClick: (command: String, bundle: Bundle?) -> Unit,
            onVisibleIndexChange: (index: Int) -> Unit
        ): MainScreenHandlers =
            MainScreenHandlers(
                onPlayClick = onPlayClick,
                onSearchClick = onSearchClick,
                onSendCommand = onSendCommand,
                onFavoriteClick = onFavoriteClick,
                onVisibleIndexChange = onVisibleIndexChange
            )
    }
}