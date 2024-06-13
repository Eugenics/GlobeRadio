package com.eugenics.ui.compose.screens.detail.components

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eugenics.core.constants.SET_FAVORITES_STATION_KEY
import com.eugenics.core.constants.SET_FAVORITES_VALUE_KEY
import com.eugenics.core.enums.Commands
import com.eugenics.core.model.NowPlayingStation
import com.eugenics.resource.R
import com.eugenics.ui.compose.screens.components.dialogs.FavoriteDialog
import com.eugenics.ui.compose.util.PreviewBase

@Composable
fun MediaInfoToolBar(
    modifier: Modifier = Modifier,
    nowPlayingStation: State<NowPlayingStation>,
    onFavoriteClick: (command: String, bundle: Bundle?) -> Unit = { _, _ -> }
) {
    val spacerSize = 25.dp
    val favoritesState = remember(nowPlayingStation.value.isFavorite) {
        mutableStateOf(nowPlayingStation.value.isFavorite)
    }
    val showFavoriteDialog = rememberSaveable { mutableStateOf(false) }

    if (showFavoriteDialog.value) {
        FavoriteDialog(
            onDismiss = { showFavoriteDialog.value = false },
            onConfirm = {
                favoritesState.value = !favoritesState.value
                showFavoriteDialog.value = false
                val bundle = Bundle()
                bundle.putString(
                    SET_FAVORITES_STATION_KEY,
                    nowPlayingStation.value.stationUUID
                )
                bundle.putInt(SET_FAVORITES_VALUE_KEY, 0)
                onFavoriteClick(Commands.SET_FAVORITES_COMMAND.name, bundle)
            }
        )
    }
    Row(
        modifier = modifier.fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CustomIconButton(
            onClick = {
                if (!favoritesState.value) {
                    favoritesState.value = !favoritesState.value
                    val bundle = Bundle()
                    bundle.putString(
                        SET_FAVORITES_STATION_KEY,
                        nowPlayingStation.value.stationUUID
                    )
                    bundle.putInt(SET_FAVORITES_VALUE_KEY, 1)
                    onFavoriteClick(Commands.SET_FAVORITES_COMMAND.name, bundle)
                } else {
                    showFavoriteDialog.value = true
                }
            },
            modifier = Modifier.weight(0.25f)
        ) {
            Icon(
                painter = painterResource(
                    if (favoritesState.value) R.drawable.ic_favorite
                    else R.drawable.ic_favorite_border
                ),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.size(spacerSize))
        Column(modifier = Modifier.weight(0.5f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = nowPlayingStation.value.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.size(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = nowPlayingStation.value.nowPlayingTitle,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.size(spacerSize))
        CustomIconButton(
            onClick = {},
            modifier = Modifier.weight(0.25f)
        ) {
            Icon(painter = painterResource(R.drawable.more_vert), contentDescription = null)
        }
    }
}

@Composable
@Preview
private fun MediaInfoToolBarPreview() {
    val nowPlayingStation = remember { mutableStateOf(NowPlayingStation.emptyInstance()) }
    PreviewBase {
        MediaInfoToolBar(
            nowPlayingStation = nowPlayingStation
        )
    }
}