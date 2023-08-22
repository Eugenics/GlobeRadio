package com.eugenics.freeradio.ui.compose.util

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eugenics.freeradio.ui.theme.FreeRadioTheme

@Composable
fun PreviewBase(
    isDarkTheme: Boolean = false,
    composable: @Composable () -> Unit
) {
    FreeRadioTheme(useDarkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            composable()
        }
    }
}