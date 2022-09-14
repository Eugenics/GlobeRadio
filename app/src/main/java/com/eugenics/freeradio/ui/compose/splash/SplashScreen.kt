package com.eugenics.freeradio.ui.compose.splash

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eugenics.freeradio.R
import com.eugenics.freeradio.ui.theme.FreeRadioTheme
import com.eugenics.freeradio.ui.viewmodels.MainViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SplashScreen(
    uiState: StateFlow<Int> = MutableStateFlow(MainViewModel.UI_STATE_LOADING)
) {
    var targetValue by remember { mutableStateOf(0f) }
    val alpha: Float by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(1000),
        finishedListener = { }
    )

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.pradio_wave),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                        .alpha(alpha = alpha)
                )
                Text(
                    text = "Free Radio",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            if (uiState.collectAsState().value == MainViewModel.UI_STATE_LOADING) {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
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
        SplashScreen()
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
private fun SplashScreenPreviewDark() {
    FreeRadioTheme {
        SplashScreen()
    }
}