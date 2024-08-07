package com.eugenics.ui.compose.screens.detail.data

import android.os.Bundle

data class DetailScreenHandlers(
    val onBackPressed: () -> Unit,
    val onMenuPressed: () -> Unit,
    val onPlayPressed: () -> Unit,
    val onNextPressed: (Boolean) -> Unit,
    val onFavoritePressed: (command: String, bundle: Bundle?) -> Unit,
) {
    companion object {
        fun newInstance(
            onBackPressed: () -> Unit = {},
            onMenuPressed: () -> Unit = {},
            onPlayPressed: () -> Unit = {},
            onNextPressed: (Boolean) -> Unit = {},
            onFavoritePressed: (command: String, bundle: Bundle?) -> Unit = { _, _ -> }
        ): DetailScreenHandlers =
            DetailScreenHandlers(
                onBackPressed = onBackPressed,
                onMenuPressed = onMenuPressed,
                onPlayPressed = onPlayPressed,
                onNextPressed = onNextPressed,
                onFavoritePressed = onFavoritePressed
            )
    }
}