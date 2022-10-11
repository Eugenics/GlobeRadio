package com.eugenics.freeradio.ui.compose.settings.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eugenics.freeradio.R
import com.eugenics.freeradio.domain.model.Theme
import com.eugenics.freeradio.ui.theme.FreeRadioTheme


@Composable
fun ThemePicker(
    currentTheme: Theme = Theme.SYSTEM,
    onThemeChoose: (theme: Theme) -> Unit = { _ -> }
) {
    val showThemeDialog = remember { mutableStateOf(false) }
    val themeName = remember { mutableStateOf(" ") }

    themeName.value = when (currentTheme) {
        Theme.DARK -> stringResource(R.string.dark_text)
        Theme.LIGHT -> stringResource(R.string.light_text)
        else -> stringResource(R.string.system_text)
    }

    if (showThemeDialog.value) {
        ThemeChooseDialog(
            currentTheme = currentTheme,
            onThemeChoose = {
                onThemeChoose(it)
                showThemeDialog.value = false
            }
        ) {
            showThemeDialog.value = false
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                showThemeDialog.value = true
            }
    ) {
        Text(
            text = "Theme",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(10.dp)
                .weight(1f)
                .wrapContentSize(align = Alignment.CenterStart)
        )
        Text(
            text = themeName.value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(10.dp)
                .weight(1f)
                .wrapContentSize(align = Alignment.CenterEnd)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeChooseDialog(
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

@Preview
@Composable
private fun ThemePickerPreview() {
    FreeRadioTheme {
        Surface {
            ThemePicker()
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ThemePickerPreviewDark() {
    FreeRadioTheme {
        Surface {
            ThemePicker()
        }
    }
}