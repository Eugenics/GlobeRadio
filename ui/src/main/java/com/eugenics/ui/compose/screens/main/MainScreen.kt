package com.eugenics.ui.compose.screens.main

import android.os.Build
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.eugenics.ui_core.data.model.UIMessage
import com.eugenics.ui_core.data.enums.DataState
import com.eugenics.ui.compose.screens.load.LoadContent
import com.eugenics.ui.compose.screens.main.components.MainTopAppBar
import com.eugenics.ui.compose.screens.main.components.MainBottomAppBar
import com.eugenics.ui.compose.screens.main.components.MainNavigationDrawer
import com.eugenics.ui.compose.screens.components.warning.WarningDialog
import com.eugenics.ui.compose.screens.main.data.MainScreenHandlers
import com.eugenics.ui.compose.screens.main.data.MainScreenStates
import com.eugenics.ui.compose.util.PreviewBase
import com.eugenics.ui_core.data.enums.Screen
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    states: State<MainScreenStates>,
    handlers: State<MainScreenHandlers>
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val padding = WindowInsets.systemBars.asPaddingValues()
    val isScrolledUp = rememberSaveable { mutableStateOf(false) }


    val tags = remember(states.value.tagList) { mutableStateOf(states.value.tagList) }
    val nowPlayingStation =
        remember(states.value.nowPlaying) { mutableStateOf(states.value.nowPlaying) }
    val playbackState =
        remember(states.value.playBackState) { mutableStateOf(states.value.playBackState) }
    val dataState = remember(states.value.dataState) { mutableStateOf(states.value.dataState) }
    val stationsUiState =
        remember(states.value.stationsUiState) { mutableStateOf(states.value.stationsUiState) }
    val stationsList = remember(states.value.stations) { mutableStateOf(states.value.stations) }
    val message = remember(states.value.message) { mutableStateOf(states.value.message) }

    val commonModifier = Modifier
        .padding(
            start = padding.calculateStartPadding(LayoutDirection.Ltr) + 5.dp,
            end = padding.calculateEndPadding(LayoutDirection.Ltr) + 5.dp
        )

    val showWarningDialog = rememberSaveable { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }
    val lastMessageId = rememberSaveable { mutableStateOf("") }

    val showPlayerBar = rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(key1 = message.value.id) {
        if (message.value.id != lastMessageId.value) {
            when (message.value.messageType) {
                UIMessage.TYPE_WARNING -> showWarningDialog.value = true
                UIMessage.TYPE_INFO ->
                    snackBarHostState.showSnackbar(
                        message = message.value.getMessage(context),
                        duration = SnackbarDuration.Short
                    )

                UIMessage.TYPE_ERROR -> snackBarHostState.showSnackbar(
                    message = message.value.getMessage(context),
                    duration = SnackbarDuration.Short
                )

                else -> {}
            }
            lastMessageId.value = message.value.id
        }
    }

    LaunchedEffect(key1 = dataState.value) {
        when (dataState.value) {
            DataState.Loading -> showPlayerBar.value = false
            else -> showPlayerBar.value = true
        }
    }

    if (showWarningDialog.value) {
        WarningDialog(
            onClose = { showWarningDialog.value = false },
            warningText = message.value.getMessage(context)
        )
    }

    MainNavigationDrawer(
        drawerState = drawerState,
        onSettingsClick = {
            navController.navigate(Screen.SettingsScreen.rout)
            scope.launch {
                drawerState.close()
            }
        },
        sendCommand = handlers.value.onSendCommand,
        tagsList = tags
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
                        onSearchClick = handlers.value.onSearchClick
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
                        playbackState = playbackState,
                        onPlayClick = handlers.value.onPlayClick,
                        onBottomAppBarClick = { navController.navigate(Screen.DetailScreen.rout) }
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState)
            }
        ) { paddingValues ->
            when (dataState.value) {
                DataState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LoadContent(message.value.getMessage(context))
                    }
                }

                else -> {
                    MainContent(
                        paddingValues = paddingValues,
                        stations = stationsList,
                        onCardClick = handlers.value.onPlayClick,
                        onFavoriteClick = handlers.value.onFavoriteClick,
                        onScrolled = {
                            isScrolledUp.value = it
                        },
                        stationsUiState = stationsUiState,
                        onVisibleIndexChange = handlers.value.onVisibleIndexChange,
                        nowPlayingStation = nowPlayingStation
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview
private fun MainScreenPreviewNight() {
    val handlers = remember {
        mutableStateOf(MainScreenHandlers.newInstance({}, {}, { _, _ -> }, { _, _ -> }, {}))
    }
    val states = remember { mutableStateOf(MainScreenStates.emptyInstance()) }

//    val tags = remember { mutableStateOf(listOf<Tag>()) }
//    val nowPlayingStation = remember { mutableStateOf(NowPlayingStation.emptyInstance()) }
//    val playbackState = remember { mutableStateOf(PlayBackState.Pause) }
//    val dataState = remember { mutableStateOf(DataState.Loading) }
//    val stationsUiState = remember { mutableStateOf(StationsUiState.emptyInstance()) }
//    val stationsList = remember { mutableStateOf(listOf<Station>()) }
//    val message = remember { mutableStateOf(UIMessage.emptyInstance()) }
    PreviewBase {
        MainScreen(
            handlers = handlers,
            states = states
        )
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview
private fun MainScreenPreviewDay() {
    val handlers = remember {
        mutableStateOf(MainScreenHandlers.newInstance({}, {}, { _, _ -> }, { _, _ -> }, {}))
    }
    val states = remember { mutableStateOf(MainScreenStates.emptyInstance()) }

    PreviewBase(isDarkTheme = true) {
        MainScreen(
            handlers = handlers,
            states = states
        )
    }
}