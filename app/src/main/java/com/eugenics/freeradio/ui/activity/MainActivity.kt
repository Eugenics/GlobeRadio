package com.eugenics.freeradio.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.eugenics.freeradio.navigation.NavGraph
import com.eugenics.freeradio.ui.theme.FreeRadioTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FreeRadioTheme(useDarkTheme = false) {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController
                )
            }
        }
    }
}