package com.eugenics.freeradio.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.eugenics.freeradio.ui.compose.main.MainScreen
import com.eugenics.freeradio.ui.compose.settings.SettingsScreen
import com.eugenics.freeradio.ui.compose.splash.SplashScreen
import com.eugenics.freeradio.ui.viewmodels.MainViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun NavGraph(
    navController: NavHostController = rememberAnimatedNavController(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.MainScreen.rout
    ) {
        composable(route = Screen.MainScreen.rout) {
            val playbackState = mainViewModel.playBackState.collectAsState()
            val uiState = mainViewModel.uiState.collectAsState()
            val dataState = mainViewModel.dataState.collectAsState()
            val stationsList = mainViewModel.stations.collectAsState()
            val nowPlayingStation = mainViewModel.nowPlaying.collectAsState()
            val tagsList = mainViewModel.tagList.collectAsState()
            val message = mainViewModel.message.collectAsState()
            val stationsUiState = mainViewModel.stationsUiState.collectAsState()
            val onPlayClick = remember { mainViewModel::onPlayClick }
            val onSearchClick = remember { mainViewModel::onSearch }
            val onSendCommand = remember { mainViewModel::sendCommand }
            val onFavoriteClick = remember { mainViewModel::sendCommand }

            MainScreen(
                navController = navController,
                uiState = uiState,
                dataState = dataState,
                playbackState = playbackState,
                stationsList = stationsList,
                message = message,
                onPlayClick = onPlayClick,
                onSearchClick = onSearchClick,
                sendCommand = onSendCommand,
                onFavoriteClick = onFavoriteClick,
                nowPlayingStation = nowPlayingStation,
                tagsList = tagsList,
                onVisibleIndexChange = { mainViewModel.setSettings(visibleIndex = it) },
                stationsUiState = stationsUiState
            )
        }
        composable(
            route = Screen.SettingsScreen.rout,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentScope.SlideDirection.Up,
                    animationSpec = tween(500)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentScope.SlideDirection.Down,
                    animationSpec = tween(500)
                )
            }
        ) {
            val settings = mainViewModel.currentStateObject.collectAsState()
            val onSendCommand = remember { mainViewModel::sendCommand }

            SettingsScreen(
                settings = settings,
                onBackPressed = { navController.popBackStack() },
                onThemePick = { theme -> mainViewModel.setSettings(theme = theme) },
                sendCommand = onSendCommand
            )
        }
        composable(route = Screen.SplashScreen.rout) {
            SplashScreen()
        }
    }
}