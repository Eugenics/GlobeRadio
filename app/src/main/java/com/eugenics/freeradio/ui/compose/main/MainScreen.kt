package com.eugenics.freeradio.ui.compose.main

import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.TopAppBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.eugenics.freeradio.R
import com.eugenics.freeradio.domain.model.Station
import com.eugenics.freeradio.ui.compose.main.components.MainTopBar
import com.eugenics.freeradio.ui.theme.FreeRadioTheme
import com.eugenics.freeradio.ui.viewmodels.SearchViewModel
import com.eugenics.media_service.media.isPlaying
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    uiState: StateFlow<Int> = MutableStateFlow(SearchViewModel.UI_STATE_LOADING),
    playbackState: StateFlow<PlaybackStateCompat> = MutableStateFlow(SearchViewModel.STATE_PAUSE),
    stationsList: StateFlow<List<Station>> = MutableStateFlow(listOf()),
    onPlayClick: () -> Unit = {},
    onPauseClick: () -> Unit = {},
    onItemClick: (mediaId: String) -> Unit = {},
    onSearchClick: (query: String) -> Unit = {}
) {
    val state = playbackState.collectAsState()
    val stations = stationsList.collectAsState()
    val listState = uiState.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text(text = "Label 1") },
                    selected = false,
                    onClick = {},
                    modifier = Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar {
                    MainTopBar(
                        onDrawerClick = {
                            scope.launch {
                                if (drawerState.isOpen) {
                                    drawerState.close()
                                } else {
                                    drawerState.open()
                                }
                            }
                        },
                        onSearchClick = onSearchClick
                    )
                }
            },
            floatingActionButton = {
                if (listState.value == SearchViewModel.UI_STATE_READY) {
                    FloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.surface,
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
        ) { paddingValues ->
            when (listState.value) {
                SearchViewModel.UI_STATE_READY -> MainContent(
                    paddingValues = paddingValues,
                    stations = stations.value,
                    onCardClick = onItemClick
                )
                SearchViewModel.UI_STATE_LOADING -> {
                    Text(
                        text = "Loading...",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(paddingValues = paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun SearchScreenPreview() {
    FreeRadioTheme {
        MainScreen()
    }
}