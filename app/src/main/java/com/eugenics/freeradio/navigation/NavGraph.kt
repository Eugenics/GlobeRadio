package com.eugenics.freeradio.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.eugenics.ui.compose.screens.main.MainScreen
import com.eugenics.ui.compose.screens.settings.SettingsScreen
import com.eugenics.freeradio.ui.viewmodels.MainViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eugenics.ui.compose.screens.detail.DetailScreen
import com.eugenics.ui.compose.screens.detail.data.DetailScreenHandlers
import com.eugenics.ui.compose.screens.main.data.MainScreenHandlers
import com.eugenics.ui.compose.screens.settings.data.SettingsScreenHandlers
import com.eugenics.ui_core.data.enums.Screen

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val mainStates = mainViewModel.mainScreenStates.collectAsState()

    NavHost(
        navController = navController, startDestination = Screen.MainScreen.rout
    ) {
        composable(route = Screen.MainScreen.rout) {
            val mainScreenHandlers: State<MainScreenHandlers> = remember {
                mutableStateOf(
                    MainScreenHandlers.newInstance(
                        onPlayClick = mainViewModel::onPlayClick,
                        onSearchClick = mainViewModel::onSearch,
                        onSendCommand = mainViewModel::sendCommand,
                        onFavoriteClick = mainViewModel::sendCommand,
                        onVisibleIndexChange = { index -> mainViewModel.setSettings(visibleIndex = index) }
                    )
                )
            }

            MainScreen(
                navController = navController,
                handlers = mainScreenHandlers,
                states = mainStates
            )
        }
        composable(route = Screen.SettingsScreen.rout, enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(500)
            )
        }, exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(500)
            )
        }) {
            val settingsScreenHandlers = remember {
                mutableStateOf(
                    SettingsScreenHandlers.newInstance(
                        onBackPressed = { navController.popBackStack() },
                        onThemePick = { theme -> mainViewModel.setSettings(theme = theme) },
                        onSendCommand = mainViewModel::sendCommand
                    )
                )
            }

            val currentState = mainViewModel.currentState.collectAsState()

            SettingsScreen(
                settings = currentState,
                handlers = settingsScreenHandlers
            )
        }

        composable(route = Screen.DetailScreen.rout) {
            val detailScreenHandlers = remember {
                mutableStateOf(
                    DetailScreenHandlers.newInstance(
                        onMenuPressed = {},
                        onBackPressed = navController::popBackStack,
                        onNextPressed = mainViewModel::onNextClick,
                        onPlayPressed = mainViewModel::onPlayClick,
                        onPrevPressed = mainViewModel::onPrevClick,
                        onFavoritePressed = mainViewModel::sendCommand
                    )
                )
            }

            val playBackState =
                remember(mainStates.value.playBackState) {
                    mutableStateOf(mainStates.value.playBackState)
                }
            val nowPlayingStation =
                remember(mainStates.value.nowPlaying) {
                    mutableStateOf(mainStates.value.nowPlaying)
                }

            DetailScreen(
                playbackState = playBackState,
                nowPlayingStation = nowPlayingStation,
                detailScreenHandlers = detailScreenHandlers
            )
        }
    }
}