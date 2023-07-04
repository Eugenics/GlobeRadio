package com.eugenics.freeradio.ui.compose.main.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MainTopAppBar(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {},
    onSearchClick: (String) -> Unit = {}
) {
    AppBarCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(durationMillis = 300)
            ),
        onMenuClick = onMenuClick,
        onSearchClick = onSearchClick
    )
}