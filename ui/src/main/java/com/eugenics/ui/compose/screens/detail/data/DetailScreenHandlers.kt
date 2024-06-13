package com.eugenics.ui.compose.screens.detail.data

import android.os.Bundle

data class DetailScreenHandlers(
    val onBackPressed: () -> Unit,
    val onMenuPressed: () -> Unit,
    val onPlayPressed: () -> Unit,
    val onPrevPressed: () -> Unit,
    val onNextPressed: () -> Unit,
    val onFavoritePressed: (command: String, bundle: Bundle?) -> Unit,
) {
    companion object {
        fun newInstance(
            onBackPressed: () -> Unit = {},
            onMenuPressed: () -> Unit = {},
            onPlayPressed: () -> Unit = {},
            onPrevPressed: () -> Unit = {},
            onNextPressed: () -> Unit = {},
            onFavoritePressed: (command: String, bundle: Bundle?) -> Unit = { _, _ -> }
        ): DetailScreenHandlers =
            DetailScreenHandlers(
                onBackPressed = onBackPressed,
                onMenuPressed = onMenuPressed,
                onPlayPressed = onPlayPressed,
                onPrevPressed = onPrevPressed,
                onNextPressed = onNextPressed,
                onFavoritePressed = onFavoritePressed
            )
    }
}