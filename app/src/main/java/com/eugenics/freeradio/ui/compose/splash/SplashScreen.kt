package com.eugenics.freeradio.ui.compose.splash

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.eugenics.freeradio.R
import com.eugenics.freeradio.navigation.Screen
import com.eugenics.freeradio.ui.compose.service.components.ServiceCard
import com.eugenics.freeradio.ui.theme.FreeRadioTheme
import com.eugenics.freeradio.ui.viewmodels.MainViewModel

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    uiState: State<Int> = mutableStateOf(MainViewModel.UI_STATE_SPLASH),
    isAnimated: Boolean = false
) {
    val targetValue = rememberSaveable { mutableStateOf(0f) }
    val splashText = rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current

    val alpha: Float by animateFloatAsState(
        targetValue = targetValue.value,
        animationSpec = tween(500),
        finishedListener = { },
        label = ""
    )

    val systemPadding = WindowInsets.systemBars.asPaddingValues()

    val animationVisibility = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = uiState.value) {
        when (uiState.value) {
            MainViewModel.UI_STATE_SPLASH_FIRST_INIT -> {
                splashText.value = context.getString(R.string.init_load_text)
                animationVisibility.value = true
            }

            MainViewModel.UI_STATE_MAIN -> {
                animationVisibility.value = false
                navHostController.navigate(Screen.MainScreen.rout) {
                    popUpTo(Screen.SplashScreen.rout) {
                        inclusive = true
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.free_radio_logo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(125.dp)
                .clip(shape = CircleShape)
                .align(alignment = Alignment.Center),
            alpha = if (isAnimated) alpha else 1f
        )
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = systemPadding.calculateBottomPadding() + 5.dp),
            visible = animationVisibility.value,
            enter = slideIn(
                tween(
                    durationMillis = 500,
                    easing = LinearOutSlowInEasing
                )
            ) { fullSize ->
                IntOffset(0, fullSize.height)
            },
            exit = slideOut(
                tween(
                    durationMillis = 500,
                    easing = LinearOutSlowInEasing
                )
            ) { fullSize ->
                IntOffset(0, fullSize.height)
            },
            label = "splash text animation"
        ) {
            Box {
                ServiceCard(infoText = splashText.value)
            }
        }
    }
    SideEffect {
        targetValue.value = 1f
    }
}

@Composable
@Preview(
    uiMode = UI_MODE_NIGHT_NO, apiLevel = 27, name = "SplashLightMode"
)
private fun SplashScreenLightModePreview() {
    FreeRadioTheme {
        SplashScreen()
    }
}

@Composable
@Preview(
    uiMode = UI_MODE_NIGHT_NO,
    apiLevel = 27,
    name = "SplashLightLandscapeMode",
    device = "spec:parent=pixel_5,orientation=landscape"
)
private fun SplashScreenLightModeLandscapePreview() {
    FreeRadioTheme {
        SplashScreen()
    }
}

@Composable
@Preview(
    uiMode = UI_MODE_NIGHT_YES, apiLevel = 27, name = "SplashDarkMode"
)
private fun SplashScreenDarkModePreview() {
    FreeRadioTheme {
        SplashScreen()
    }
}

@Composable
@Preview(
    uiMode = UI_MODE_NIGHT_NO, apiLevel = 27, name = "SplashLightModeInit"
)
private fun SplashScreenInitPreview() {
    FreeRadioTheme {
        val state = remember { mutableStateOf(MainViewModel.UI_STATE_SPLASH_FIRST_INIT) }
        SplashScreen(uiState = state)
    }
}