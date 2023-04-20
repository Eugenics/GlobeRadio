package com.eugenics.freeradio.navigation

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.eugenics.freeradio.ui.compose.main.MainScreen
import com.eugenics.freeradio.ui.compose.settings.SettingsScreen
import com.eugenics.freeradio.ui.viewmodels.MainViewModel
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection.Companion.SET_FAVORITES_COMMAND
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection.Companion.SET_FAVORITES_STATION_KEY
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection.Companion.SET_FAVORITES_VALUE_KEY

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun NavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
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
                },
                onFavoriteClick = { stationUuid, isFavorite ->
                    val bundle = Bundle()
                    bundle.putString(SET_FAVORITES_STATION_KEY, stationUuid)
                    bundle.putInt(SET_FAVORITES_VALUE_KEY, isFavorite)
                    mainViewModel.sendCommand(
                        command = SET_FAVORITES_COMMAND,
                        extras = bundle
                    )
                },
                nowPlayingStation = mainViewModel.nowPlaying,
                tagsList = mainViewModel.tagList.collectAsState().value
            )
        }
        composable(route = Screen.SettingsScreen.rout) {
            SettingsScreen(
                settings = mainViewModel.settings,
                onBackPressed = { navController.popBackStack() },
                onThemePick = { theme -> mainViewModel.setSettings(theme = theme) }
            )
        }
    }
}