package com.eugenics.ui.compose.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.eugenics.core.model.CurrentState
import com.eugenics.core.enums.Theme
import com.eugenics.resource.R
import com.eugenics.ui.compose.screens.settings.components.CategoryRow
import com.eugenics.ui.compose.screens.settings.components.SettingsAppBar
import com.eugenics.ui.compose.screens.settings.components.SettingsTextRow
import com.eugenics.ui.compose.screens.settings.components.ThemeChooseDialog
import com.eugenics.ui.compose.screens.settings.data.SettingsScreenHandlers
import com.eugenics.ui.compose.util.PreviewBase
import com.eugenics.ui_core.data.enums.UICommand

@Composable
fun SettingsScreen(
    settings: State<CurrentState>,
    handlers: State<SettingsScreenHandlers>
) {
    val showThemeDialog = remember { mutableStateOf(false) }
    val themeName = rememberSaveable { mutableStateOf(settings.value.theme.name) }

    themeName.value = when (settings.value.theme) {
        Theme.DARK -> stringResource(R.string.dark_text)
        Theme.LIGHT -> stringResource(R.string.light_text)
        Theme.CONTENT_LIGHT -> stringResource(R.string.content_light_text)
        Theme.CONTENT_DARK -> stringResource(R.string.content_dark_text)
        else -> stringResource(R.string.system_text)
    }

    if (showThemeDialog.value) {
        ThemeChooseDialog(
            currentTheme = settings.value.theme,
            onThemeChoose = { theme ->
                handlers.value.onThemePick(theme)
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
                onBackPressed = handlers.value.onBackPressed
            )

            CategoryRow(categoryName = stringResource(R.string.display_text))

            SettingsTextRow(
                onRowClick = { showThemeDialog.value = true },
                nameText = stringResource(R.string.theme_title_string),
                valueText = themeName.value
            )

            CategoryRow(categoryName = stringResource(R.string.utility_text))

            SettingsTextRow(
                onRowClick = {
                    handlers.value.onSendCommand(
                        UICommand.UI_COMMAND_BACKUP_FAVORITES.name,
                        null
                    )
                },
                nameText = stringResource(R.string.backup_favorites),
                valueText = ""
            )

            SettingsTextRow(
                onRowClick = {
                    handlers.value.onSendCommand(
                        UICommand.UI_COMMAND_RESTORE_FAVORITES.name,
                        null
                    )
                    handlers.value.onBackPressed()
                },
                nameText = stringResource(R.string.restore_favorites),
                valueText = ""
            )

            SettingsTextRow(
                onRowClick = {
                    handlers.value.onSendCommand(
                        UICommand.UI_EXPORT_FAVORITES_PLAYLIST.name,
                        null
                    )
                    handlers.value.onBackPressed()
                },
                nameText = stringResource(R.string.export_favorites_playlist),
                valueText = ""
            )
        }
    }
}

@Composable
@Preview
private fun SettingsScreenPreview() {
    PreviewBase {
        val settings = remember { mutableStateOf(CurrentState.emptyInstance()) }
        val settingsHandlers =
            remember { mutableStateOf(SettingsScreenHandlers({}, {}, { _, _ -> })) }
        SettingsScreen(
            settings = settings,
            handlers = settingsHandlers
        )
    }
}