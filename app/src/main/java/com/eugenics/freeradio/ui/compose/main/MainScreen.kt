package com.eugenics.freeradio.ui.compose.main

import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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
    sendCommand: (command: String, parameters: String?) -> Unit = { _, _ -> },
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
                    floatingActionButton = {
                        if (listState.value == MainViewModel.UI_STATE_READY) {
                            FloatingActionButton(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.systemBarsPadding(),
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
                        MainViewModel.UI_STATE_READY -> MainContent(
                            paddingValues = paddingValues,
                            stations = stations.value,
                            onCardClick = onItemClick
                        )
                        MainViewModel.UI_STATE_LOADING -> {
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