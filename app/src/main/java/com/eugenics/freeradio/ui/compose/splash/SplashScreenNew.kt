package com.eugenics.freeradio.ui.compose.splash

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
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
import com.eugenics.freeradio.ui.viewmodels.MainViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SplashScreenNew(
    uiState: StateFlow<Int> = MutableStateFlow(MainViewModel.UI_STATE_LOADING)
) {
    var targetValue by remember { mutableStateOf(0f) }

    val alpha: Float by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(2000),
        finishedListener = { }
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.free_radio_logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(shape = CircleShape),
                alpha = alpha
            )
        }
        SideEffect {
            targetValue = 1f
        }
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_NO)
private fun SplashScreenPreview() {
    FreeRadioTheme {
        SplashScreenNew()
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
private fun SplashScreenPreviewDark() {
    FreeRadioTheme {
        SplashScreenNew()
    }
}