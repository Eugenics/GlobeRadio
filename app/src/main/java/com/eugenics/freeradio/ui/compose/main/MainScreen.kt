package com.eugenics.freeradio.ui.compose.main

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ART_URI
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.eugenics.freeradio.R
import com.eugenics.freeradio.domain.model.Station
import com.eugenics.freeradio.navigation.Screen
import com.eugenics.freeradio.ui.compose.main.components.MainNavigationDrawer
import com.eugenics.freeradio.ui.compose.main.components.MainTopBar
import com.eugenics.freeradio.ui.compose.splash.SplashScreen
import com.eugenics.freeradio.ui.theme.FreeRadioTheme
import com.eugenics.freeradio.ui.viewmodels.MainViewModel
import com.eugenics.media_service.media.isPlaying
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    uiState: StateFlow<Int> = MutableStateFlow(MainViewModel.UI_STATE_LOADING),
    playbackState: StateFlow<PlaybackStateCompat> = MutableStateFlow(MainViewModel.STATE_PAUSE),
    stationsList: StateFlow<List<Station>> = MutableStateFlow(listOf()),
    sendCommand: (command: String, parameters: Bundle?) -> Unit = { _, _ -> },
    onPlayClick: () -> Unit = {},
    onPauseClick: () -> Unit = {},
    onItemClick: (mediaId: String) -> Unit = {},
    onSearchClick: (query: String) -> Unit = {},
    onFavoriteClick: (stationUuid: String, isFavorite: Int) -> Unit = { _, _ -> },
    onInfoClick: () -> Unit = {},
    nowPlayingStation: StateFlow<Station> = MutableStateFlow(Station())
) {
    val state = playbackState.collectAsState()
    val stations = stationsList.collectAsState()
    val listState = uiState.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    when (listState.value) {
        MainViewModel.UI_STATE_LOADING -> SplashScreen()
        MainViewModel.UI_STATE_IDL -> SplashScreen()
        else -> {
            MainNavigationDrawer(
                drawerState = drawerState,
                onSettingsClick = {
                    navController.navigate(Screen.SettingsScreen.rout)
                    scope.launch {
                        drawerState.close()
                    }
                },
                sendCommand = sendCommand
            ) {
                Scaffold(
                    topBar = {
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
                    },
                    bottomBar = {
                        BottomAppBar {
                            if (listState.value == MainViewModel.UI_STATE_READY) {
                                SubcomposeAsyncImage(
                                    model = nowPlayingStation.collectAsState().value.favicon,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(50.dp, 50.dp)
                                        .padding(5.dp),
                                    contentScale = ContentScale.Fit
                                ) {
                                    val painterState = painter.state
                                    if (painterState is AsyncImagePainter.State.Loading || painterState is AsyncImagePainter.State.Error) {
                                        Image(
                                            painter = painterResource(R.drawable.pradio_wave),
                                            contentDescription = null
                                        )
                                    } else {
                                        SubcomposeAsyncImageContent()
                                    }
                                }
                                Text(
                                    text = nowPlayingStation.collectAsState().value.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
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
                        }
                    }
                ) { paddingValues ->
                    when (listState.value) {
                        MainViewModel.UI_STATE_READY -> {
//                            if (stations.value.isNotEmpty()) {
//                                currentStation.value = stations.value[0]
//                            }

                            MainContent(
                                paddingValues = paddingValues,
                                stations = stations.value,
                                onCardClick = onItemClick,
                                onFavoriteClick = onFavoriteClick,
                                onInfoClick = onInfoClick
                            )
                        }
                        MainViewModel.UI_STATE_REFRESH -> {
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
    }
}

@Composable
@Preview
private fun SearchScreenPreview() {
    FreeRadioTheme {
        MainScreen()
    }
}