package com.eugenics.freeradio.ui.compose.splash

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eugenics.freeradio.R
import com.eugenics.freeradio.ui.theme.FreeRadioTheme

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    isAnimated: Boolean = false
) {
    var targetValue by remember { mutableStateOf(0f) }

    val alpha: Float by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(100),
        finishedListener = { }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.free_radio_logo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(125.dp)
                .clip(shape = CircleShape)
                .align(Alignment.Center),
            alpha = if (isAnimated) alpha else 1f
        )
    }
    SideEffect {
        targetValue = 1f
    }
}

@Composable
@Preview(
    uiMode = UI_MODE_NIGHT_NO,
    apiLevel = 27,
    name = "SplashLightMode"
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
    uiMode = UI_MODE_NIGHT_YES,
    apiLevel = 27,
    name = "SplashDarkMode"
)
private fun SplashScreenDarkModePreview() {
    FreeRadioTheme {
        SplashScreen()
    }
}