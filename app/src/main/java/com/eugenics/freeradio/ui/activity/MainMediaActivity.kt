package com.eugenics.freeradio.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eugenics.freeradio.ui.viewmodels.MediaViewModel
import com.eugenics.ui.theme.ContentDynamicTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainMediaActivity : ComponentActivity() {

    private val mediaViewModel: MediaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //mediaViewModel.initialize()

        setContent {
            ContentDynamicTheme(
                isSystemTheme = true,
                isDarkColorsScheme = false,
                color = Color.Cyan
            ) {
                TestMediaView("title.value")
            }
        }

    }

    companion object {
        private const val TAG = "MEDIA_ACTIVITY"
    }
}

@Composable
private fun TestMediaView(title: String) {
    Surface(tonalElevation = 5.dp) {
        Scaffold() { paddingValues ->
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                        start = 10.dp, end = 10.dp
                    )
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}