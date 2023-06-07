package com.eugenics.freeradio.ui.application

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eugenics.core.enums.Theme
import com.eugenics.freeradio.navigation.NavGraph
import com.eugenics.freeradio.ui.theme.ContentDynamicTheme
import com.eugenics.freeradio.ui.theme.DarkColors
import com.eugenics.freeradio.ui.theme.LightColors
import com.eugenics.freeradio.ui.viewmodels.MainViewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Application(
    viewModel: MainViewModel = hiltViewModel()
) {
    val theme = viewModel.settings.collectAsState().value.theme
    val isDarkTheme = when (theme) {
        Theme.DARK -> true
        Theme.CONTENT_DARK -> true
        Theme.LIGHT -> false
        Theme.CONTENT_LIGHT -> false
        else -> isSystemInDarkTheme()
    }

    val dynamicColor = viewModel.primaryDynamicColor.collectAsState()
    val defaultColor = if (isDarkTheme) {
        DarkColors.primary
    } else {
        LightColors.primary
    }

    LaunchedEffect(dynamicColor) {
        if (dynamicColor.value == 0) {
            viewModel.setPrimaryDynamicColor(defaultColor.toArgb())
        }
    }

    val content = @Composable {
        val navController = rememberAnimatedNavController()
        Surface(tonalElevation = 5.dp) {
            NavGraph(
                navController = navController,
                mainViewModel = viewModel
            )
        }
    }

    val isSystemTheme = !listOf(Theme.CONTENT_DARK, Theme.CONTENT_LIGHT).contains(theme)

    val color = animateColorAsState(
        targetValue =
        if (listOf(Theme.CONTENT_DARK, Theme.CONTENT_LIGHT).contains(theme)) {
            Color(dynamicColor.value)
        } else {
            defaultColor
        },
        animationSpec = tween(1500)
    )

    ContentDynamicTheme(
        isDarkColorsScheme = isDarkTheme,
        color = color.value,
        content = content,
        isSystemTheme = isSystemTheme
    )
}