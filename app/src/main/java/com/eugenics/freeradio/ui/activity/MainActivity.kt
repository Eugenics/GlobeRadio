package com.eugenics.freeradio.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.eugenics.freeradio.domain.model.Theme
import com.eugenics.freeradio.navigation.NavGraph
import com.eugenics.freeradio.ui.theme.FreeRadioTheme
import com.eugenics.freeradio.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val theme = mainViewModel.settings.collectAsState().value.theme
            FreeRadioTheme(
                useDarkTheme =
                when (theme) {
                    Theme.DARK -> true
                    Theme.LIGHT -> false
                    else -> isSystemInDarkTheme()
                }
            ) {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}