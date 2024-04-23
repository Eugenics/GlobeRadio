package com.eugenics.freeradio.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.eugenics.freeradio.ui.compose.main.MainScreen
import com.eugenics.ui.compose.screens.settings.SettingsScreen
import com.eugenics.ui.compose.screens.splash.SplashScreen
import com.eugenics.freeradio.ui.viewmodels.MainViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.MainScreen.rout
    ) {
        composable(route = Screen.MainScreen.rout) {
            val playbackState = mainViewModel.playBackState.collectAsState()
            val dataState = mainViewModel.uiDataState.collectAsState()
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
                uiDataState = dataState,
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
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(500)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
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