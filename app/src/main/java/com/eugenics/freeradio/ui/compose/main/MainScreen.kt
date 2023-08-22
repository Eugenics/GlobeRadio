package com.eugenics.freeradio.ui.compose.main

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.eugenics.core.model.NowPlayingStation
import com.eugenics.core.model.Station
import com.eugenics.core.model.Tag
import com.eugenics.freeradio.R
import com.eugenics.freeradio.core.data.SystemMessage
import com.eugenics.freeradio.core.enums.DataState
import com.eugenics.freeradio.core.enums.MessageType
import com.eugenics.freeradio.core.enums.UIState
import com.eugenics.freeradio.navigation.Screen
import com.eugenics.freeradio.ui.compose.load.LoadContent
import com.eugenics.freeradio.ui.compose.main.components.MainTopAppBar
import com.eugenics.freeradio.ui.compose.main.components.MainBottomAppBar
import com.eugenics.freeradio.ui.compose.main.components.MainNavigationDrawer
import com.eugenics.freeradio.ui.compose.splash.SplashScreen
import com.eugenics.freeradio.ui.compose.warning.WarningDialog
import com.eugenics.freeradio.ui.theme.FreeRadioTheme
import com.eugenics.freeradio.ui.util.PlayBackState
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    uiState: State<UIState> = mutableStateOf(UIState.UI_STATE_SPLASH),
    dataState: State<DataState> = mutableStateOf(DataState.LOADING),
    playbackState: State<PlayBackState> = mutableStateOf(PlayBackState.Pause),
    stationsList: State<List<Station>> = mutableStateOf(listOf()),
    nowPlayingStation: State<NowPlayingStation> = mutableStateOf(NowPlayingStation.emptyInstance()),
    sendCommand: (command: String, parameters: Bundle?) -> Unit = { _, _ -> },
    tagsList: State<List<Tag>>,
    visibleIndex: Int = 0,
    message: State<SystemMessage> = mutableStateOf(SystemMessage.emptyInstance()),
    onPlayClick: () -> Unit = {},
    onPauseClick: () -> Unit = {},
    onItemClick: (mediaId: String) -> Unit = {},
    onSearchClick: (query: String) -> Unit = {},
    onFavoriteClick: (command: String, bundle: Bundle?) -> Unit = { _, _ -> },
    onVisibleIndexChange: (index: Int) -> Unit = {}
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val padding = WindowInsets.systemBars.asPaddingValues()
    val isScrolledUp = rememberSaveable { mutableStateOf(false) }

    val commonModifier = Modifier
        .padding(
            start = padding.calculateStartPadding(LayoutDirection.Ltr) + 5.dp,
            end = padding.calculateEndPadding(LayoutDirection.Ltr) + 5.dp
        )

    val showWarningDialog = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = message.value) {
        if (message.value.type == MessageType.WARNING) {
            showWarningDialog.value = true
        }
    }

    if (showWarningDialog.value) {
        WarningDialog(
            onClose = { showWarningDialog.value = false },
            warningText = message.value.message
        )
    }

    when (uiState.value) {
        UIState.UI_STATE_SPLASH -> SplashScreen(
            message =
            if (message.value.type == MessageType.INFO) message.value.message
            else ""
        )

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
                        MainTopAppBar(
                            modifier = commonModifier
                                .padding(top = padding.calculateTopPadding() + 5.dp),
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
                            modifier = commonModifier
                                .padding(bottom = padding.calculateBottomPadding() + 5.dp),
                            nowPlayingStation = nowPlayingStation,
                            playbackState = playbackState,
                            onPlayClick = onPlayClick,
                            onPauseClick = onPauseClick
                        )
                    }
                ) { paddingValues ->
                    when (dataState.value) {
                        DataState.LOADING -> {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                LoadContent(text = stringResource(R.string.loading_string))
                            }
                        }

                        else -> {
                            MainContent(
                                paddingValues = paddingValues,
                                stations = stationsList,
                                onCardClick = onItemClick,
                                onFavoriteClick = onFavoriteClick,
                                onScrolled = {
                                    isScrolledUp.value = it
                                },
                                visibleIndex = visibleIndex,
                                onVisibleIndexChange = onVisibleIndexChange,
                                nowPlayingStation = nowPlayingStation
                            )
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
    val tagsList = remember { mutableStateOf(listOf<Tag>()) }
    FreeRadioTheme {
        MainScreen(tagsList = tagsList)
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
private fun MainScreenPreviewDay() {
    val tagsList = remember { mutableStateOf(listOf<Tag>()) }
    FreeRadioTheme {
        MainScreen(tagsList = tagsList)
    }
}