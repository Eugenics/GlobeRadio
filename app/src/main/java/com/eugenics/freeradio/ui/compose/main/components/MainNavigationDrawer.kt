package com.eugenics.freeradio.ui.compose.main.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eugenics.freeradio.R
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationDrawer(
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Open),
    paddingValues: PaddingValues = PaddingValues(),
    onSettingsClick: () -> Unit = {},
    sendCommand: (command: String, parameters: Bundle?) -> Unit = { _, _ -> },
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(10.dp))
                    Image(
                        painter = painterResource(R.drawable.pradio_wave),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(Modifier.width(20.dp))
                    Text(
                        text = "Free Radio",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                Divider()
                Spacer(Modifier.height(12.dp))

                CustomNavigationItem(
                    text = "Stations",
                    icon = Icons.Filled.Refresh,
                    onClick = {
                        sendCommand(
                            FreeRadioMediaServiceConnection.STATIONS_COMMAND,
                            null
                        )
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )

                CustomNavigationItem(
                    text = "Favorites",
                    icon = Icons.Filled.Favorite,
                    onClick = {
                        sendCommand(
                            FreeRadioMediaServiceConnection.FAVORITES_COMMAND,
                            null
                        )
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )

                Divider()
                Spacer(Modifier.height(12.dp))

                CustomNavigationItem(
                    text = "Settings",
                    icon = Icons.Filled.Settings,
                    onClick = onSettingsClick
                )
                CustomNavigationItem(
                    text = "About",
                    icon = Icons.Filled.Info,
                    onClick = { }
                )

            }
        },
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(uiMode = UI_MODE_NIGHT_NO)
private fun MainNavigationDrawerPreview() {
    MainNavigationDrawer { }
}