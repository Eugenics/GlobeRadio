package com.eugenics.freeradio.ui.compose.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsTextRow(
    onRowClick: () -> Unit = {},
    nameText: String = "Setting name",
    valueText: String = "Setting value"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onRowClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = nameText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(10.dp)
                .weight(1f)
                .wrapContentSize(align = Alignment.CenterStart)
        )
        Text(
            text = valueText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(10.dp)
                .weight(1f)
                .wrapContentSize(align = Alignment.CenterEnd)
        )
    }
}