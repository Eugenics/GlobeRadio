package com.eugenics.freeradio.ui.compose.settings.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eugenics.freeradio.R

@Composable
fun SoftwareInfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            Button(
                onClick = onDismiss,
                content = {
                    Text(text = stringResource(R.string.close_string))
                }
            )
        },
        text = {
            Text(
                text = "Created by Eugene Podzorov ${'\u00A9'} 2022"
            )
        }
    )
}