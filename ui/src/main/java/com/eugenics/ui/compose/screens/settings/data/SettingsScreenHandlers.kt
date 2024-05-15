package com.eugenics.ui.compose.screens.settings.data

import android.os.Bundle
import com.eugenics.core.enums.Theme
import com.eugenics.ui_core.data.interfaces.ScreenHandlers

data class SettingsScreenHandlers(
    val onBackPressed: () -> Unit,
    val onThemePick: (theme: Theme) -> Unit,
    val onSendCommand: (command: String, parameters: Bundle?) -> Unit
) : ScreenHandlers {
    companion object {
        fun newInstance(
            onBackPressed: () -> Unit,
            onThemePick: (theme: Theme) -> Unit,
            onSendCommand: (command: String, parameters: Bundle?) -> Unit
        ): SettingsScreenHandlers =
            SettingsScreenHandlers(
                onBackPressed = onBackPressed,
                onThemePick = onThemePick,
                onSendCommand = onSendCommand
            )
    }
}
