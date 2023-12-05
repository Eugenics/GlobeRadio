package com.eugenics.ui.compose.main

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.eugenics.core.model.NowPlayingStation
import com.eugenics.core.model.Station
import com.eugenics.core.model.StationsUiState
import com.eugenics.ui_core.data.SystemMessage
import com.eugenics.core.model.Tag
import com.eugenics.resource.R
import com.eugenics.freeradio.ui.compose.main.components.MainNavigationDrawer
import com.eugenics.ui.compose.common.InfoDialog
import com.eugenics.ui.compose.load.LoadContent
import com.eugenics.ui.compose.main.components.MainBottomAppBar
import com.eugenics.ui.compose.main.components.MainTopAppBar
import com.eugenics.ui.compose.splash.SplashScreen
import com.eugenics.ui.compose.util.PreviewBase
import com.eugenics.ui_core.enums.DataState
import com.eugenics.ui_core.enums.UIState
import com.eugenics.ui_core.classes.Screen
import com.eugenics.ui_core.enums.MessageType
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    uiState: Int = UIState.UI_STATE_SPLASH.code,
    dataState: State<DataState> = mutableStateOf(DataState.LOADING),
    playback: Boolean = false,
    stationsList: State<List<Station>> = mutableStateOf(listOf()),
    nowPlayingStation: State<NowPlayingStation> = mutableStateOf(NowPlayingStation.emptyInstance()),
    sendCommand: (command: String, parameters: Bundle?) -> Unit = { _, _ -> },
    tagsList: State<List<Tag>>,
    stationsUiState: State<StationsUiState> = mutableStateOf(StationsUiState.emptyInstance()),
    message: State<SystemMessage> = mutableStateOf(SystemMessage.emptyInstance()),
    onPlayClick: (mediaId: String?) -> Unit = {},
    onSearchClick: (query: String) -> Unit = {},
    onFavoriteClick: (command: String, bundle: Bundle?) -> Unit = { _, _ -> },
    onVisibleIndexChange: (index: Int) -> Unit = {}
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val padding = WindowInsets.systemBars.asPaddingValues()
    val isScrolledUp = rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    val commonModifier = Modifier
        .padding(
            start = padding.calculateStartPadding(LayoutDirection.Ltr) + 5.dp,
            end = padding.calculateEndPadding(LayoutDirection.Ltr) + 5.dp
        )

    val showWarningDialog = rememberSaveable { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }
    val lastMessageId = rememberSaveable { mutableStateOf("") }

    val showPlayerBar = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = message.value.id) {
        if (message.value.message.isNotBlank() &&
            message.value.id != lastMessageId.value
        ) {
            when (message.value.type) {
                MessageType.WARNING -> showWarningDialog.value = true
                MessageType.INFO -> snackBarHostState.showSnackbar(
                    message = message.value.message,
                    duration = SnackbarDuration.Short
                )

                MessageType.ERROR -> snackBarHostState.showSnackbar(
                    message = message.value.message,
                    duration = SnackbarDuration.Short
                )

                else -> {}
            }
            lastMessageId.value = message.value.id
        }
    }

    LaunchedEffect(key1 = dataState.value) {
        when (dataState.value) {
            DataState.LOADING -> showPlayerBar.value = false
            else -> showPlayerBar.value = true
        }
    }

    if (showWarningDialog.value) {
        InfoDialog(
            title = stringResource(R.string.warning),
            onConfirm = null,
            onDismiss = { showWarningDialog.value = false },
            content = { Text(text = message.value.message) }
        )
    }

    when (uiState) {
        UIState.UI_STATE_SPLASH.code ->
            SplashScreen(message = context.getString(R.string.init_load_text))

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
                        AnimatedVisibility(
                            visible = showPlayerBar.value,
                            label = "media bar animation",
                            enter = slideIn(
                                animationSpec = tween(durationMillis = 1000)
                            ) { fullSize ->
                                IntOffset(0, -fullSize.height)
                            },
                            exit = slideOut(
                                animationSpec = tween(durationMillis = 1000)
                            ) { fullSize ->
                                IntOffset(0, -fullSize.width)
                            }
                        ) {
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
                        }
                    },
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showPlayerBar.value,
                            label = "media bar animation",
                            enter = slideIn(
                                animationSpec = tween(
                                    durationMillis = 1000,
                                    easing = LinearOutSlowInEasing
                                )
                            ) { fullSize ->
                                IntOffset(0, fullSize.height)
                            },
                            exit = slideOut(
                                animationSpec = tween(
                                    durationMillis = 1000,
                                    easing = LinearOutSlowInEasing
                                )
                            ) { fullSize ->
                                IntOffset(0, fullSize.width)
                            }
                        ) {
                            MainBottomAppBar(
                                modifier = commonModifier
                                    .padding(bottom = padding.calculateBottomPadding() + 5.dp),
                                nowPlayingStation = nowPlayingStation,
                                playback = playback,
                                onPlayClick = onPlayClick
                            )
                        }
                    },
                    snackbarHost = {
                        SnackbarHost(hostState = snackBarHostState)
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
                                onCardClick = onPlayClick,
                                onFavoriteClick = onFavoriteClick,
                                onScrolled = {
                                    isScrolledUp.value = it
                                },
                                stationsUiState = stationsUiState,
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
    PreviewBase {
        MainScreen(tagsList = tagsList, uiState = UIState.UI_STATE_MAIN.code)
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
private fun MainScreenPreviewDay() {
    val tagsList = remember { mutableStateOf(listOf<Tag>()) }
    PreviewBase {
        MainScreen(tagsList = tagsList, uiState = UIState.UI_STATE_MAIN.code)
    }
}