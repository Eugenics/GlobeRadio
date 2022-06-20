package com.eugenics.freeradio.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.eugenics.freeradio.ui.compose.search.SearchScreen


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.SearchScreen.rout
    ) {
        composable(route = Screen.SearchScreen.rout) {
            SearchScreen(navController)
        }
    }
}