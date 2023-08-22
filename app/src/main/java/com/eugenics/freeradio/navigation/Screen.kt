package com.eugenics.freeradio.navigation

sealed class Screen(val rout: String) {
    object MainScreen : Screen("main_screen")
    object SettingsScreen : Screen("settings_screen")
    object SplashScreen : Screen("splash_screen")
}
