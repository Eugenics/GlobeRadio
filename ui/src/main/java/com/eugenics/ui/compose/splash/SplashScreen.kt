package com.eugenics.ui.compose.splash

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.eugenics.resource.R
import com.eugenics.ui.compose.service.components.ServiceCard
import com.eugenics.ui.compose.util.PreviewBase

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    message: String = ""
) {
    val targetValue = rememberSaveable { mutableStateOf(0f) }

    val alpha: Float by animateFloatAsState(
        targetValue = targetValue.value,
        animationSpec = tween(500),
        finishedListener = { },
        label = ""
    )

    val systemPadding = WindowInsets.systemBars.asPaddingValues()

    val animationVisibility = remember { mutableStateOf(true) }

    Box(
        modifier = modifier.fillMaxSize()
            .background(color = colorResource(R.color.main_brand))
    ) {
        Image(
            painter = painterResource(R.drawable.logo_foreground),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(250.dp)
                .clip(shape = CircleShape)
                .align(alignment = Alignment.Center),
            alpha = alpha
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
                ServiceCard(infoText = message, textColor = Color.White)
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
    PreviewBase {
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
    PreviewBase {
        SplashScreen()
    }
}

@Composable
@Preview(
    uiMode = UI_MODE_NIGHT_YES, apiLevel = 27, name = "SplashDarkMode"
)
private fun SplashScreenDarkModePreview() {
    PreviewBase(isDarkTheme = true) {
        SplashScreen()
    }
}

@Composable
@Preview(
    uiMode = UI_MODE_NIGHT_NO, apiLevel = 27, name = "SplashLightModeInit"
)
private fun SplashScreenInitPreview() {
    PreviewBase {
        val message = remember { mutableStateOf("Loading...") }
        SplashScreen(message = message.value)
    }
}