package com.eugenics.ui.compose.screens.load

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eugenics.resource.R
import com.eugenics.ui.compose.util.PreviewBase

@Composable
fun LoadGears(modifier: Modifier = Modifier) {
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
        modifier = modifier.size(250.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.gear_red),
            contentDescription = null,
            modifier = Modifier
                .offset(x = 0.dp, y = (-40).dp)
                .rotate(rotationPosition.value)
                .align(Alignment.Center)
        )
        Image(
            painter = painterResource(R.drawable.gear_blue),
            contentDescription = null,
            modifier = Modifier
                .offset(x = 70.dp, y = 30.dp)
                .rotate(antiClockWisePosition.value)
                .align(Alignment.Center)
        )
        Image(
            painter = painterResource(R.drawable.gear_green),
            contentDescription = null,
            modifier = Modifier
                .offset(x = (-70).dp, y = 30.dp)
                .rotate(antiClockWisePosition.value)
                .align(Alignment.Center)
        )
    }
}

@Composable
@Preview(
    name = "GearsPreview",
    apiLevel = 34,
)
private fun PreviewLoadingScreen() {
    PreviewBase(isDarkTheme = true) {
        LoadGears()
    }
}