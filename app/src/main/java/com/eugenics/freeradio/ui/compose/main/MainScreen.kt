package com.eugenics.freeradio.ui.compose.main

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.eugenics.freeradio.domain.model.Station
import com.eugenics.freeradio.navigation.Screen
import com.eugenics.freeradio.ui.compose.main.components.AppBarCard
import com.eugenics.freeradio.ui.compose.main.components.MainBottomAppBar
import com.eugenics.freeradio.ui.compose.main.components.MainNavigationDrawer
import com.eugenics.freeradio.ui.compose.splash.SplashScreen
import com.eugenics.freeradio.ui.theme.FreeRadioTheme
import com.eugenics.freeradio.ui.viewmodels.MainViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.min


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

    val padding = WindowInsets.systemBars.asPaddingValues()

    when (listState.value) {
        MainViewModel.UI_STATE_FIRST_INIT -> SplashScreen()
        MainViewModel.UI_STATE_IDL -> SplashScreen()
        MainViewModel.UI_STATE_EMPTY -> SplashScreen()
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
                        AppBarCard(
                            paddingValues = padding,
                            onMenuClick = {
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
                        MainBottomAppBar(
                            paddingValues = padding,
                            nowPlayingStation = nowPlayingStation.collectAsState().value,
                            playbackState = playbackState.collectAsState().value,
                            onPlayClick = onPlayClick,
                            onPauseClick = onPauseClick
                        )
                    }
                ) { paddingValues ->
                    when (listState.value) {
                        MainViewModel.UI_STATE_READY -> {
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
                                modifier = Modifier.padding(
                                    top = paddingValues.calculateTopPadding()
                                )
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