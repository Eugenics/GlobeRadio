package com.eugenics.freeradio.ui.compose.main.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eugenics.core.enums.TagsCommands
import com.eugenics.core.model.Tag
import com.eugenics.freeradio.R
import com.eugenics.freeradio.ui.compose.settings.components.SoftwareInfoDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationDrawer(
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Open),
    onSettingsClick: () -> Unit = {},
    sendCommand: (command: String, parameters: Bundle?) -> Unit = { _, _ -> },
    tagsList: List<Tag>,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    val tagDialog = rememberSaveable { mutableStateOf(false) }
    if (tagDialog.value) {
        AlertDialog(
            onDismissRequest = { tagDialog.value = false },
            confirmButton = {},
            dismissButton = {
                Button(onClick = { tagDialog.value = false }) {
                    Text(text = stringResource(R.string.close_string))
                }
            },
            title = { Text(text = stringResource(R.string.tags_select_string)) },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    itemsIndexed(tagsList) { index, item ->
                        Row(
                            modifier = Modifier
                                .clickable {
                                    tagDialog.value = false
                                    val extras = Bundle()
                                    extras.putString("TAG", item.value)
                                    sendCommand(
                                        TagsCommands.STATIONS_COMMAND.name,
                                        extras
                                    )
                                }
                                .fillMaxWidth()
                                .padding(10.dp)
                                .wrapContentWidth(align = Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = item.name.uppercase(),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            },
            shape = MaterialTheme.shapes.medium
        )
    }

    var showLicenseDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    if (showLicenseDialog) {
        SoftwareInfoDialog { showLicenseDialog = false }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Spacer(Modifier.width(10.dp))
                    Image(
                        painter = painterResource(R.drawable.free_radio_logo),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(75.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.width(20.dp))
                    Text(
                        text = stringResource(R.string.free_radio_text),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                Divider()
                Spacer(Modifier.height(12.dp))

                CustomNavigationItem(
                    text = stringResource(R.string.stations_text),
                    icon = Icons.Filled.List,
                    onClick = {
                        tagDialog.value = true
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )

                CustomNavigationItem(
                    text = stringResource(R.string.favorites_text),
                    icon = Icons.Filled.Favorite,
                    onClick = {
                        sendCommand(
                            TagsCommands.FAVORITES_COMMAND.name,
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
                    text = stringResource(R.string.reload_all_stations_string),
                    icon = Icons.Filled.Refresh,
                    onClick = {
                        sendCommand(
                            TagsCommands.RELOAD_ALL_STATIONS_COMMAND.name,
                            null
                        )
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )

                CustomNavigationItem(
                    text = stringResource(R.string.settings_text),
                    icon = Icons.Filled.Settings,
                    onClick = onSettingsClick
                )
                CustomNavigationItem(
                    text = stringResource(R.string.about_text),
                    icon = Icons.Filled.Info,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        showLicenseDialog = true
                    }
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
    MainNavigationDrawer(tagsList = listOf()) { }
}