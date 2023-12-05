package com.eugenics.ui.compose.common

import android.content.res.Configuration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.eugenics.resource.R
import com.eugenics.ui.compose.util.PreviewBase

@Composable
fun InfoDialog(
    title: String,
    onDismiss: (() -> Unit)? = null,
    onConfirm: (() -> Unit)? = null,
    content: @Composable (() -> Unit)
) {
    AlertDialog(
        onDismissRequest = { onDismiss },
        confirmButton = {
            if (onConfirm != null) {
                Button(onClick = onConfirm) {
                    Text(text = stringResource(R.string.yes_string))
                }
            }
        },
        dismissButton = {
            if (onDismiss != null) {
                Button(
                    onClick = onDismiss
                ) {
                    Text(text = stringResource(R.string.no_string))
                }
            }
        },
        title = { Text(text = title) },
        text = { content },
        shape = MaterialTheme.shapes.medium
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun InfoDialogNightPreviewDay() {
    PreviewBase(isDarkTheme = false) {
        InfoDialog(
            title = "Info dialog",
            onConfirm = {},
            onDismiss = {},
            content = { Text(text = "Info content...") }
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InfoDialogNightPreviewNight() {
    PreviewBase(isDarkTheme = true) {
        InfoDialog(
            title = "Info dialog",
            onConfirm = {},
            onDismiss = {},
            content = { Text(text = "Info content...") }
        )
    }
}