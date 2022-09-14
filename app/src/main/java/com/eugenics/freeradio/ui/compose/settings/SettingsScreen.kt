package com.eugenics.freeradio.ui.compose.settings

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eugenics.freeradio.ui.compose.settings.components.ThemePicker
import com.eugenics.freeradio.ui.theme.FreeRadioTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackPressed: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleMedium
                )
            },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            Text(
                text = "Settings",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
                    .padding(5.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            ThemePicker { }
        }
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
private fun SettingsScreenPreview() {
    FreeRadioTheme {
        SettingsScreen()
    }
}