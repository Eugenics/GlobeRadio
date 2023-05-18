package com.eugenics.freeradio.ui.compose.main

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.eugenics.core.model.NowPlayingStation
import com.eugenics.core.model.Station
import com.eugenics.core.model.Tag
import com.eugenics.freeradio.R
import com.eugenics.freeradio.navigation.Screen
import com.eugenics.freeradio.ui.compose.load.LoadContent
import com.eugenics.freeradio.ui.compose.main.components.AppBarCard
import com.eugenics.freeradio.ui.compose.main.components.MainBottomAppBar
import com.eugenics.freeradio.ui.compose.main.components.MainNavigationDrawer
import com.eugenics.freeradio.ui.compose.splash.SplashScreen
import com.eugenics.freeradio.ui.theme.FreeRadioTheme
import com.eugenics.freeradio.ui.viewmodels.MainViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    uiState: StateFlow<Int> = MutableStateFlow(MainViewModel.UI_STATE_UPDATE_DATA),
    playbackState: StateFlow<PlaybackStateCompat> = MutableStateFlow(MainViewModel.STATE_PAUSE),
    stationsList: StateFlow<List<Station>> = MutableStateFlow(listOf()),
    sendCommand: (command: String, parameters: Bundle?) -> Unit = { _, _ -> },
    onPlayClick: () -> Unit = {},
    onPauseClick: () -> Unit = {},
    onItemClick: (mediaId: String) -> Unit = {},
    onSearchClick: (query: String) -> Unit = {},
    onFavoriteClick: (stationUuid: String, isFavorite: Int) -> Unit = { _, _ -> },
    nowPlayingStation: StateFlow<NowPlayingStation> =
        MutableStateFlow(NowPlayingStation.emptyInstance()),
    tagsList: List<Tag>,
    visibleIndex: Int = 0,
    onVisibleIndexChange: (index: Int) -> Unit = {}
) {
    val stations = stationsList.collectAsState()
    val listState = uiState.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val padding = WindowInsets.systemBars.asPaddingValues()

    val isScrolledUp = rememberSaveable { mutableStateOf(false) }

    when (listState.value) {
        MainViewModel.UI_STATE_SPLASH -> SplashScreen()
        else -> {
            MainNavigationDrawer(
                drawerState = drawerState,
                onSettingsClick = {
                    navController.navigate(Screen.SettingsScreen.rout)
                    scope.launch {
                        drawerState.close()
                    }
                },
                sendCommand = sendCommand,
                tagsList = tagsList
            ) {
                Scaffold(
                    topBar = {
                        AppBarCard(
                            modifier = Modifier
                                .padding(
                                    top = padding.calculateTopPadding() + 5.dp,
                                    bottom = 16.dp,
                                    start = 16.dp,
                                    end = 16.dp
                                )
                                .fillMaxWidth()
                                .animateContentSize(
                                    animationSpec = tween(durationMillis = 300)
                                ),
//                                    .height(if (isScrolledUp.value) 0.dp else 56.dp),
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
                            modifier = Modifier
                                .padding(
                                    bottom = padding.calculateBottomPadding() + 5.dp,
                                    start = 16.dp,
                                    end = 16.dp
                                ),
                            nowPlayingStation = nowPlayingStation.collectAsState().value,
                            playbackState = playbackState.collectAsState().value,
                            onPlayClick = onPlayClick,
                            onPauseClick = onPauseClick
                        )
                    }
                ) { paddingValues ->
                    when (listState.value) {
                        MainViewModel.UI_STATE_MAIN -> {
                            MainContent(
                                paddingValues = paddingValues,
                                stations = stations.value,
                                onCardClick = onItemClick,
                                onFavoriteClick = onFavoriteClick,
                                onScrolled = {
                                    isScrolledUp.value = it
                                },
                                visibleIndex = visibleIndex,
                                onVisibleIndexChange = onVisibleIndexChange
                            )
                        }

                        MainViewModel.UI_STATE_UPDATE_DATA -> {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                LoadContent(text = stringResource(R.string.loading_string))
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview(uiMode = UI_MODE_NIGHT_NO)
private fun MainScreenPreviewNight() {
    FreeRadioTheme {
        MainScreen(tagsList = listOf())
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
private fun MainScreenPreviewDay() {
    FreeRadioTheme {
        MainScreen(tagsList = listOf())
    }
}