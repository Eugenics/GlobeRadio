package com.eugenics.freeradio.ui.compose.load

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eugenics.freeradio.R
import com.eugenics.freeradio.ui.theme.FreeRadioTheme

@Composable
fun LoadGears(modifier: Modifier = Modifier.fillMaxSize()) {
    val rotationPosition = remember { mutableStateOf(0f) }
    val antiClockWisePosition = remember { mutableStateOf(360f) }
    val rotation = remember { Animatable(rotationPosition.value) }
    val antiClockWise = remember { Animatable(antiClockWisePosition.value) }

    LaunchedEffect(key1 = null) {
        rotation.animateTo(
            targetValue = 360f + rotationPosition.value,
            animationSpec = infiniteRepeatable(
                animation = tween(6000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        ) {
            rotationPosition.value = value
        }
    }

    LaunchedEffect(key1 = null) {
        antiClockWise.animateTo(
            targetValue = 360f - antiClockWisePosition.value,
            animationSpec = infiniteRepeatable(
                animation = tween(6000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        ) {
            antiClockWisePosition.value = value
        }
    }
    Box(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(R.drawable.gear_red),
            contentDescription = null,
            modifier = Modifier
                .rotate(rotationPosition.value)
        )
        Box(modifier = Modifier.offset(x = 70.dp, y = 70.dp)) {
            Image(
                painter = painterResource(R.drawable.gear_blue),
                contentDescription = null,
                modifier = Modifier
                    .rotate(antiClockWisePosition.value)
            )
        }
        Box(modifier = Modifier.offset(x = (-70).dp, y = 70.dp)) {
            Image(
                painter = painterResource(R.drawable.gear_green),
                contentDescription = null,
                modifier = Modifier
                    .rotate(antiClockWisePosition.value)
            )
        }
    }
}

@Composable
@Preview
private fun PreviewLoadingScreen() {
    FreeRadioTheme {
        LoadGears()
    }
}