package com.eugenics.freeradio.ui.compose.settings

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eugenics.core.model.CurrentState
import com.eugenics.core.enums.Theme
import com.eugenics.freeradio.ui.compose.settings.components.ThemePicker
import com.eugenics.freeradio.ui.theme.FreeRadioTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.eugenics.freeradio.R
import com.eugenics.freeradio.ui.compose.main.components.CustomNavigationItem
import com.eugenics.freeradio.ui.util.UICommands

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: StateFlow<CurrentState> = MutableStateFlow(CurrentState.getDefaultValueInstance()),
    onBackPressed: () -> Unit = {},
    onThemePick: (theme: Theme) -> Unit = { _ -> },
    sendCommand: (command: String, parameters: Bundle?) -> Unit = { _, _ -> }
) {
    val theme = settings.collectAsState().value.theme

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = stringResource(R.string.settings_text),
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
                text = stringResource(R.string.settings_text),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
                    .padding(5.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            ThemePicker(
                currentTheme = theme,
                onThemeChoose = onThemePick
            )


            CustomNavigationItem(
                text = stringResource(R.string.backup_favorites),
                icon = ImageVector.vectorResource(R.drawable.baseline_file_download_24),
                onClick = {
                    sendCommand(
                        UICommands.UICommand_BACKUP_FAVORITES.name,
                        null
                    )
                }
            )

            CustomNavigationItem(
                text = stringResource(R.string.restore_favorites),
                icon = ImageVector.vectorResource(R.drawable.baseline_file_upload_24),
                onClick = {
                    sendCommand(
                        UICommands.UICommand_RESTORE_FAVORITES.name,
                        null
                    )
                    onBackPressed()
                }
            )

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