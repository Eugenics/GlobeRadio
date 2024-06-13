package com.eugenics.ui.compose.screens.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eugenics.resource.R

@Composable
fun FavoriteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = stringResource(R.string.yes_string))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.no_string))
            }
        },
        title = { Text(text = stringResource(R.string.favorites_text)) },
        text = {
            Text(
                text = stringResource(R.string.favorites_confirm_string),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        shape = MaterialTheme.shapes.medium
    )
}