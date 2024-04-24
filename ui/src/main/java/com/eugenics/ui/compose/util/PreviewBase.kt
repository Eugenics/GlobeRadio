package com.eugenics.ui.compose.util

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eugenics.ui.theme.GlobeRadioTheme

@Composable
fun PreviewBase(
    isDarkTheme: Boolean = false,
    composable: @Composable () -> Unit
) {
    GlobeRadioTheme(useDarkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 5.dp
        ) {
            composable()
        }
    }
}