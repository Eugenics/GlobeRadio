package com.eugenics.ui_core.data.enums

private const val MAIN_SCREEN_ROUT = "main_screen"
private const val SETTINGS_SCREEN_ROUT = "settings_screen"
private const val SPLASH_SCREEN_ROUT = "splash_screen"

sealed class Screen(val rout: String) {
    data object MainScreen : Screen(rout = MAIN_SCREEN_ROUT)
    data object SettingsScreen : Screen(rout = SETTINGS_SCREEN_ROUT)
    data object SplashScreen : Screen(rout = SPLASH_SCREEN_ROUT)
}