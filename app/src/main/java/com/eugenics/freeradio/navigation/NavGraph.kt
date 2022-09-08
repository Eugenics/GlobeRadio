package com.eugenics.freeradio.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.eugenics.freeradio.ui.compose.search.SearchScreen
import com.eugenics.freeradio.ui.viewmodels.SearchViewModel

@Composable
fun NavGraph(navController: NavHostController) {

    val searchViewModel: SearchViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.SearchScreen.rout
    ) {
        composable(route = Screen.SearchScreen.rout) {
            SearchScreen(
                uiState = searchViewModel.uiState,
                playbackState = searchViewModel.state,
                stationsList = searchViewModel.stations,
                onPlayClick = { searchViewModel.play() },
                onPauseClick = { searchViewModel.pause() },
                onItemClick = { mediaId -> searchViewModel.onItemClick(mediaId = mediaId) }
            )
        }
    }
}