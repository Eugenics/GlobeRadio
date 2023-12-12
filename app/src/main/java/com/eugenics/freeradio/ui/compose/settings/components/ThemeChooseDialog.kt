package com.eugenics.freeradio.ui.compose.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eugenics.core.enums.Theme
import com.eugenics.resource.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeChooseDialog(
    currentTheme: Theme = Theme.SYSTEM,
    onThemeChoose: (theme: Theme) -> Unit,
    onDismissButtonClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissButtonClick,
        title = {
            Text(
                text = stringResource(R.string.choose_theme_text),
                style = MaterialTheme.typography.titleLarge
            )
        },
        dismissButton = {
            Button(
                onClick = onDismissButtonClick,
            ) {
                Text(text = stringResource(R.string.close_string))
            }
        },
        confirmButton = {},
        text = {
            Column(modifier = Modifier.selectableGroup()) {
                for (theme in Theme.values()) {
                    Card(
                        onClick = { onThemeChoose(theme) },
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = BorderStroke(0.dp, Color.Transparent),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentTheme == theme,
                                enabled = true,
                                onClick = { onThemeChoose(theme) }
                            )
                            Text(
                                text = when (theme) {
                                    Theme.DARK -> stringResource(R.string.dark_text)
                                    Theme.LIGHT -> stringResource(R.string.light_text)
                                    Theme.CONTENT_LIGHT -> stringResource(R.string.content_light_text)
                                    Theme.CONTENT_DARK -> stringResource(R.string.content_dark_text)
                                    else -> stringResource(R.string.system_text)
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    )
}