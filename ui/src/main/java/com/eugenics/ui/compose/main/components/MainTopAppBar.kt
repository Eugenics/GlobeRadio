package com.eugenics.ui.compose.main.components

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.eugenics.ui.compose.util.PreviewSimple

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

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview
private fun MainTopAppBarPreview() {
    PreviewSimple {
        MainTopAppBar()
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun MainTopAppBarPreviewDark() {
    PreviewSimple(isDarkTheme = true) {
        MainTopAppBar()
    }
}