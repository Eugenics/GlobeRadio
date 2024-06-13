package com.eugenics.ui.compose.screens.detail

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eugenics.core.model.NowPlayingStation
import com.eugenics.ui.compose.screens.detail.components.AlbumArtBox
import com.eugenics.ui.compose.screens.detail.components.DetailSreenTopBar
import com.eugenics.ui.compose.screens.detail.components.MediaInfoToolBar
import com.eugenics.ui.compose.screens.detail.components.MediaToolBar
import com.eugenics.ui.compose.screens.detail.data.DetailScreenHandlers
import com.eugenics.ui.compose.util.PreviewBase
import com.eugenics.ui_core.data.enums.PlayBackState

@Composable
fun DetailScreen(
    nowPlayingStation: State<NowPlayingStation>,
    playbackState: State<PlayBackState>,
    detailScreenHandlers: State<DetailScreenHandlers>
) {
    val paddings = WindowInsets.systemBars.asPaddingValues()
    val columnScrollableState = rememberScrollableState { 0f }

    val nextButtonState = remember { mutableStateOf(true) }
    val prevButtonState = remember { mutableStateOf(true) }

    Scaffold(topBar = {
        DetailSreenTopBar(onBackPressed = detailScreenHandlers.value.onBackPressed)
    }, bottomBar = {
        MediaToolBar(
            modifier = Modifier.padding(bottom = paddings.calculateBottomPadding() + 10.dp),
            playbackState = playbackState,
            onPrevPressed = detailScreenHandlers.value.onPrevPressed,
            onPlayPressed = detailScreenHandlers.value.onPlayPressed,
            onNextPressed = detailScreenHandlers.value.onNextPressed,
            nextButtonState = nextButtonState,
            prevButtonState = prevButtonState
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).scrollable(
                enabled = true,
                orientation = Orientation.Vertical,
                state = columnScrollableState
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp).weight(2f)
            ) {
                AlbumArtBox(albumArtImageUri = nowPlayingStation.value.favicon)
            }
            Spacer(modifier = Modifier.size(20.dp))
            MediaInfoToolBar(
                modifier = Modifier.weight(1f), nowPlayingStation = nowPlayingStation
            )
            Spacer(modifier = Modifier.size(20.dp))
        }
    }
}

@Preview
@Composable
private fun DetailScreenPreview() {
    val detailScreenHandlers = remember {
        mutableStateOf(DetailScreenHandlers.newInstance())
    }
    val nowPlayingStation = remember {
        mutableStateOf(NowPlayingStation.newInstance(name = "Sample name..."))
    }
    val playbackState = remember { mutableStateOf(PlayBackState.Pause) }

    PreviewBase(isDarkTheme = true) {
        DetailScreen(
            nowPlayingStation = nowPlayingStation,
            playbackState = playbackState,
            detailScreenHandlers = detailScreenHandlers
        )
    }
}