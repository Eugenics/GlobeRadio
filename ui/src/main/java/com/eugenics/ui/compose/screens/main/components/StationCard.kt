package com.eugenics.ui.compose.screens.main.components

import android.content.res.Configuration
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.eugenics.core.constants.SET_FAVORITES_STATION_KEY
import com.eugenics.core.constants.SET_FAVORITES_VALUE_KEY
import com.eugenics.core.enums.Commands
import com.eugenics.core.model.Station
import com.eugenics.resource.R
import com.eugenics.ui.compose.screens.components.dialogs.FavoriteDialog
import com.eugenics.ui.compose.screens.main.MainContent
import com.eugenics.ui.theme.GlobeRadioTheme

@Composable
fun StationCard(
    isActive: Boolean = false,
    station: Station,
    onCardClick: (mediaId: String) -> Unit = {},
    onFavoriteClick: (command: String, bundle: Bundle?) -> Unit = { _, _ -> }
) {
    val isFavorite = rememberSaveable { mutableIntStateOf(station.isFavorite) }
    val showFavoriteDialog = rememberSaveable { mutableStateOf(false) }

    if (showFavoriteDialog.value) {
        FavoriteDialog(
            onDismiss = { showFavoriteDialog.value = false },
            onConfirm = {
                isFavorite.intValue = 0
                showFavoriteDialog.value = false
                val bundle = Bundle()
                bundle.putString(
                    SET_FAVORITES_STATION_KEY,
                    station.stationuuid
                )
                bundle.putInt(
                    SET_FAVORITES_VALUE_KEY,
                    isFavorite.intValue
                )
                onFavoriteClick(Commands.SET_FAVORITES_COMMAND.name, bundle)
            }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isActive) {
                    MaterialTheme.colorScheme.background
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                }
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
                if (isFavorite.intValue == 0) {
                    isFavorite.intValue = 1

                    val bundle = Bundle()
                    bundle.putString(
                        SET_FAVORITES_STATION_KEY,
                        station.stationuuid
                    )
                    bundle.putInt(
                        SET_FAVORITES_VALUE_KEY,
                        isFavorite.intValue
                    )
                    onFavoriteClick(Commands.SET_FAVORITES_COMMAND.name, bundle)
                } else {
                    showFavoriteDialog.value = true
                }
            }
        ) {
            Icon(
                painter = painterResource(
                    if (isFavorite.intValue == 0) R.drawable.ic_favorite_border
                    else R.drawable.ic_favorite
                ),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun StationCardNightPreviewDay() {
    GlobeRadioTheme {
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
    GlobeRadioTheme {
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
    GlobeRadioTheme {
        StationCard(
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
    isFavorite = 0,
    votes = 0
)