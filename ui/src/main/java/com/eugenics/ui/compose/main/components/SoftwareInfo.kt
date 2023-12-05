package com.eugenics.ui.compose.main.components

import android.content.res.Configuration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.eugenics.resource.R
import com.eugenics.ui.compose.util.PreviewBase
import java.util.Calendar

@Composable
fun SoftwareInfoDialog(onDismiss: () -> Unit) {
    val calendar = Calendar.getInstance()
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
                text = "${stringResource(R.string.info_string)} ${'\u00A9'} ${calendar.get(Calendar.YEAR)}"
            )
        }
    )
}

@Composable
@Preview(showBackground = true)
private fun SoftwareInfoDialogPreview() {
    PreviewBase {
        SoftwareInfoDialog {}
    }
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun SoftwareInfoDialogNightPreview() {
    PreviewBase(isDarkTheme = true) {
        SoftwareInfoDialog {}
    }
}