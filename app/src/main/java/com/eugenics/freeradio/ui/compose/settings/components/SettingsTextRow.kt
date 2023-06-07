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
import androidx.compose.ui.unit.sp

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
            modifier = Modifier.padding(10.dp)
                .weight(1f)
                .wrapContentSize(align = Alignment.CenterStart),
            text = nameText,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp
        )
        Text(
            modifier = Modifier.padding(10.dp)
                .weight(
                    if (valueText.isNotBlank()) 1f else 0.1f
                )
                .wrapContentSize(align = Alignment.CenterEnd),
            text = valueText,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp
        )
    }
}