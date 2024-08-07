package com.eugenics.freeradio.ui.application

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.eugenics.core.enums.Theme
import com.eugenics.freeradio.navigation.NavGraph
import com.eugenics.ui.theme.ContentDynamicTheme
import com.eugenics.ui.theme.DarkColors
import com.eugenics.ui.theme.LightColors
import com.eugenics.freeradio.ui.viewmodels.MediaViewModel

private const val THEME_ANIMATION_LABEL = "theme_dynamic_color"
private const val TWEEN_ANIMATION_DURATION = 250

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Application(
    viewModel: MediaViewModel = hiltViewModel()
) {
    val theme = remember(viewModel.currentUiState.collectAsState().value.theme) {
        viewModel.currentUiState.value.theme
    }

    val isSystemTheme = !listOf(Theme.CONTENT_DARK, Theme.CONTENT_LIGHT).contains(theme)
    val isContentTheme = listOf(Theme.CONTENT_DARK, Theme.CONTENT_LIGHT).contains(theme)

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

//    LaunchedEffect(dynamicColor) {
//        if (dynamicColor.value == 0) {
//            viewModel.setPrimaryDynamicColor(defaultColor.toArgb())
//        }
//    }

    val color = animateColorAsState(
        targetValue =
        if (isContentTheme) {
            Color(dynamicColor.value)
        } else {
            defaultColor
        },
        animationSpec = tween(TWEEN_ANIMATION_DURATION),
        label = THEME_ANIMATION_LABEL
    )

    ContentDynamicTheme(
        isDarkColorsScheme = isDarkTheme,
        color = color.value,
        isSystemTheme = isSystemTheme
    ){
        val navController = rememberNavController()
        Surface(tonalElevation = 5.dp) {
            NavGraph(
                navController = navController,
                mediaViewModel = viewModel
            )
        }
    }
}