package com.eugenics.freeradio.navigation

sealed class Screen(val rout: String) {
    object SearchScreen : Screen("search_screen")
    object SettingsScreen : Screen("settings_screen")
}
