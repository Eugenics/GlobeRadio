package com.eugenics.freeradio.ui.compose.warning

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eugenics.resource.R

@Composable
fun WarningDialog(
    onClose: () -> Unit,
    warningText: String = ""
) {
    AlertDialog(
        onDismissRequest = onClose,
        dismissButton = {
            Button(
                onClick = onClose,
            ) {
                Text(text = stringResource(R.string.close_string))
            }
        },
        confirmButton = {},
        title = {
            Text(text = "Warning...")
        },
        text = {
            Text(text = warningText)
        }
    )
}