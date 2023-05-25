package com.eugenics.freeradio.ui.compose.settings

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.eugenics.core.model.CurrentState
import com.eugenics.core.enums.Theme
import com.eugenics.freeradio.ui.theme.FreeRadioTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.eugenics.freeradio.R
import com.eugenics.freeradio.ui.compose.settings.components.CategoryRow
import com.eugenics.freeradio.ui.compose.settings.components.SettingsAppBar
import com.eugenics.freeradio.ui.compose.settings.components.SettingsTextRow
import com.eugenics.freeradio.ui.compose.settings.components.ThemeChooseDialog
import com.eugenics.freeradio.ui.util.UICommands

@Composable
fun SettingsScreen(
    settings: StateFlow<CurrentState> = MutableStateFlow(CurrentState.getDefaultValueInstance()),
    onBackPressed: () -> Unit = {},
    onThemePick: (theme: Theme) -> Unit = { _ -> },
    sendCommand: (command: String, parameters: Bundle?) -> Unit = { _, _ -> }
) {
    val theme = settings.collectAsState().value.theme
    val showThemeDialog = remember { mutableStateOf(false) }
    val themeName = rememberSaveable { mutableStateOf(theme.name) }

    themeName.value = when (theme) {
        Theme.DARK -> stringResource(R.string.dark_text)
        Theme.LIGHT -> stringResource(R.string.light_text)
        Theme.CONTENT_LIGHT -> stringResource(R.string.content_light_text)
        Theme.CONTENT_DARK -> stringResource(R.string.content_dark_text)
        else -> stringResource(R.string.system_text)
    }

    if (showThemeDialog.value) {
        ThemeChooseDialog(
            currentTheme = theme,
            onThemeChoose = {
                onThemePick(it)
                showThemeDialog.value = false
            }
        ) {
            showThemeDialog.value = false
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            SettingsAppBar(
                onBackPressed = onBackPressed
            )

            CategoryRow(categoryName = "Display")

            SettingsTextRow(
                onRowClick = { showThemeDialog.value = true },
                nameText = stringResource(R.string.theme_title_string),
                valueText = themeName.value
            )

            CategoryRow(categoryName = "Utility")

            SettingsTextRow(
                onRowClick = {
                    sendCommand(
                        UICommands.UI_COMMAND_BACKUP_FAVORITES.name,
                        null
                    )
                },
                nameText = stringResource(R.string.backup_favorites),
                valueText = ""
            )

            SettingsTextRow(
                onRowClick = {
                    sendCommand(
                        UICommands.UI_COMMAND_RESTORE_FAVORITES.name,
                        null
                    )
                    onBackPressed()
                },
                nameText = stringResource(R.string.restore_favorites),
                valueText = ""
            )
        }
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_NO)
private fun SettingsScreenPreview() {
    FreeRadioTheme {
        SettingsScreen()
    }
}