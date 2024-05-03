package com.eugenics.ui.compose.screens.main.data

import android.os.Bundle

data class MainScreenHandlers(
    val onPlayClick: (mediaId: String?) -> Unit,
    val onSearchClick: (query: String) -> Unit,
    val sendCommand: (command: String, parameters: Bundle?) -> Unit,
    val onFavoriteClick: (command: String, bundle: Bundle?) -> Unit,
    val onVisibleIndexChange: () -> Unit,
)