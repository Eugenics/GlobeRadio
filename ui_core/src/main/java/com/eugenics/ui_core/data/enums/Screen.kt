package com.eugenics.ui_core.data.enums

sealed class Screen(val rout: String) {
    data object MainScreen : Screen(rout = "main_screen")
    data object SettingsScreen : Screen(rout = "settings_screen")
    data object SplashScreen : Screen(rout = "splash_screen")
}