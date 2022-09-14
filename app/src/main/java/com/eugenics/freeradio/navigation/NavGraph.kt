package com.eugenics.freeradio.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.eugenics.freeradio.ui.compose.main.MainScreen
import com.eugenics.freeradio.ui.compose.settings.SettingsScreen
import com.eugenics.freeradio.ui.viewmodels.MainViewModel

@Composable
fun NavGraph(navController: NavHostController) {

    val mainViewModel: MainViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.SearchScreen.rout
    ) {
        composable(route = Screen.SearchScreen.rout) {
            MainScreen(
                navController = navController,
                uiState = mainViewModel.uiState,
                playbackState = mainViewModel.state,
                stationsList = mainViewModel.stations,
                onPlayClick = { mainViewModel.play() },
                onPauseClick = { mainViewModel.pause() },
                onItemClick = { mediaId -> mainViewModel.onItemClick(mediaId = mediaId) },
                onSearchClick = { query -> mainViewModel.search(query = query) },
                sendCommand = { command, parameters ->
                    mainViewModel.sendCommand(
                        command = command,
                        extras = parameters
                    )
                }
            )
        }
        composable(route = Screen.SettingsScreen.rout) {
            SettingsScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}