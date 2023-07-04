package com.eugenics.freeradio.ui.compose.main.components

import android.content.res.Configuration
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.eugenics.core.enums.Commands
import com.eugenics.core.model.NowPlayingStation
import com.eugenics.core.model.Station
import com.eugenics.freeradio.R
import com.eugenics.freeradio.ui.compose.main.MainContent
import com.eugenics.freeradio.ui.theme.FreeRadioTheme
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection

@Composable
fun StationCard(
    paddingValues: PaddingValues = PaddingValues(),
    isActive: Boolean = false,
    index: Int = 0,
    size: Int = 0,
    station: Station,
    onCardClick: (mediaId: String) -> Unit = {},
    onFavoriteClick: (command: String, bundle: Bundle?) -> Unit = { _, _ -> }
) {
    val isFavorite = rememberSaveable { mutableStateOf(station.isFavorite) }
    val standardPadding = 0.dp
    val topPadding =
        if (index == 0) paddingValues.calculateTopPadding() + 0.dp
        else standardPadding
    val bottomPadding =
        if (index == size - 1) paddingValues.calculateBottomPadding() + 0.dp
        else standardPadding

    val showFavoriteDialog = rememberSaveable { mutableStateOf(false) }


    if (showFavoriteDialog.value) {
        favoriteDialog(
            onDismiss = { showFavoriteDialog.value = false },
            onConfirm = {
                isFavorite.value = 0
                showFavoriteDialog.value = false
                val bundle = Bundle()
                bundle.putString(
                    FreeRadioMediaServiceConnection.SET_FAVORITES_STATION_KEY,
                    station.stationuuid
                )
                bundle.putInt(
                    FreeRadioMediaServiceConnection.SET_FAVORITES_VALUE_KEY,
                    isFavorite.value
                )
                onFavoriteClick(station.stationuuid, bundle)
            }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 1.dp)
            .background(
                color = if (isActive) {
                    MaterialTheme.colorScheme.tertiaryContainer
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                }
            )
            .padding(
                top = topPadding,
                bottom = bottomPadding,
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
            )
            .clickable {
                onCardClick(station.stationuuid)
            }

    ) {
        SubcomposeAsyncImage(
            model = station.favicon,
            contentDescription = null,
            modifier = Modifier
                .size(65.dp, 65.dp)
                .padding(8.dp)
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Fit
        ) {
            val state = painter.state
            if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                Image(
                    painter = painterResource(R.drawable.pradio_wave),
                    contentDescription = null
                )
            } else {
                SubcomposeAsyncImageContent()
            }
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = station.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${station.codec}:${station.bitrate}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        IconButton(
            modifier = Modifier
                .padding(4.dp),
            onClick = {
                if (isFavorite.value == 0) {
                    isFavorite.value = 1

                    val bundle = Bundle()
                    bundle.putString(
                        FreeRadioMediaServiceConnection.SET_FAVORITES_STATION_KEY,
                        station.stationuuid
                    )
                    bundle.putInt(
                        FreeRadioMediaServiceConnection.SET_FAVORITES_VALUE_KEY,
                        isFavorite.value
                    )
                    onFavoriteClick(Commands.SET_FAVORITES_COMMAND.name, bundle)
                } else {
                    showFavoriteDialog.value = true
                }
            }
        ) {
            Icon(
                painter = painterResource(
                    if (isFavorite.value == 0) R.drawable.ic_favorite_border
                    else R.drawable.ic_favorite
                ),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun favoriteDialog(
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun StationCardNightPreviewDay() {
    FreeRadioTheme {
        val stations = remember { mutableStateOf(listOf(fakeStation, fakeStation, fakeStation)) }
        MainContent(
            paddingValues = PaddingValues(),
            stations = stations,
            onCardClick = {},
            onScrolled = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StationCardNightPreviewNight() {
    FreeRadioTheme {
        val stations = remember { mutableStateOf(listOf(fakeStation, fakeStation, fakeStation)) }
        MainContent(
            paddingValues = PaddingValues(),
            stations = stations,
            onCardClick = {},
            onScrolled = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun CardDayPreview() {
    FreeRadioTheme {
        StationCard(
            paddingValues = PaddingValues(8.dp),
            station = fakeStation
        )
    }
}

private val fakeStation = Station(
    stationuuid = "96202f73-0601-11e8-ae97-52543be04c81",
    name = "Radio Schizoid - Chillout / Ambient",
    tags = "Electronics",
    homepage = "",
    url = "http://94.130.113.214:8000/chill",
    urlResolved = "http://94.130.113.214:8000/chill",
    favicon = "",
    bitrate = 128,
    codec = "MP3",
    country = "India",
    countrycode = "IN",
    language = "hindi",
    languagecodes = "hi",
    changeuuid = "92c2fbdc-14ec-4861-af65-49dd7de7826f",
    isFavorite = 0
)

private val nowPlayingStation = NowPlayingStation(
    name = "Radio Schizoid - Chillout / Ambient",
    favicon = "",
    nowPlayingTitle = "Radio Schizoid - Chillout / Ambient",
    stationUUID = "96202f73-0601-11e8-ae97-52543be04c81",
    description = ""
)