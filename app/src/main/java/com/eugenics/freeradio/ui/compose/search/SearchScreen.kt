package com.eugenics.freeradio.ui.compose.search

import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.Image
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.eugenics.freeradio.R
import com.eugenics.freeradio.domain.model.Station
import com.eugenics.freeradio.ui.theme.FreeRadioTheme
import com.eugenics.freeradio.ui.viewmodels.SearchViewModel
import com.eugenics.media_service.media.isPlaying
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


@Composable
fun SearchScreen(
    uiState: StateFlow<Int> = MutableStateFlow(SearchViewModel.UI_STATE_LOADING),
    playbackState: StateFlow<PlaybackStateCompat> = MutableStateFlow(SearchViewModel.STATE_PAUSE),
    stationsList: StateFlow<List<Station>> = MutableStateFlow(listOf()),
    onPlayClick: () -> Unit = {},
    onPauseClick: () -> Unit = {},
    onItemClick: (mediaId: String) -> Unit = {}
) {
    val state = playbackState.collectAsState()
    val stations = stationsList.collectAsState()
    val listState = uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            if (listState.value == SearchViewModel.UI_STATE_READY) {
                FloatingActionButton(
                    onClick = {
                        if (state.value.isPlaying) {
                            onPauseClick()
                        } else {
                            onPlayClick()
                        }
                    }
                ) {
                    Image(
                        painter = if (state.value.isPlaying) {
                            painterResource(R.drawable.ic_pause)
                        } else {
                            painterResource(R.drawable.ic_play_arrow)
                        },
                        contentDescription = null
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        when (listState.value) {
            SearchViewModel.UI_STATE_READY -> SearchContent(
                stations = stations.value,
                onCardClick = onItemClick
            )
            SearchViewModel.UI_STATE_LOADING -> {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.h3,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
@Preview
private fun SearchScreenPreview() {
    FreeRadioTheme {
        SearchScreen()
    }
}