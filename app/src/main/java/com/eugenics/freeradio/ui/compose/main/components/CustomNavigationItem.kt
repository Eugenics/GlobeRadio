package com.eugenics.freeradio.ui.compose.main.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eugenics.freeradio.ui.theme.FreeRadioTheme

@Composable
fun CustomNavigationItem(
    text: String = "Settings",
    icon: ImageVector = Icons.Filled.Settings,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    NavigationDrawerItem(
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
                Text(
                    text = text,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

@Preview
@Composable
private fun SettingsItemPreview() {
    FreeRadioTheme {
        CustomNavigationItem()
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SettingsItemPreviewDark() {
    FreeRadioTheme {
        CustomNavigationItem()
    }
}