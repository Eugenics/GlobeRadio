package com.eugenics.freeradio.ui.compose.main

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.eugenics.freeradio.R
import com.eugenics.freeradio.domain.model.Station
import com.eugenics.freeradio.ui.theme.FreeRadioTheme

@Composable
fun MainContent(
    paddingValues: PaddingValues,
    stations: List<Station>,
    onFavoriteClick: (stationUuid: String, isFavorite: Int) -> Unit = { _, _ -> },
    onInfoClick: () -> Unit = {},
    onCardClick: (mediaId: String) -> Unit
) {
    val columnState = rememberLazyListState()

    Column(
        modifier = Modifier
            .padding(paddingValues = paddingValues)
            .fillMaxWidth()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = columnState
        ) {
            itemsIndexed(stations) { _, station ->
                StationCard(
                    station = station,
                    onCardClick = onCardClick,
                    onFavoriteClick = onFavoriteClick,
                    onInfoClick = onInfoClick
                )
            }
        }
    }

}

@Composable
private fun StationCard(
    station: Station,
    onCardClick: (mediaId: String) -> Unit = {},
    onFavoriteClick: (stationUuid: String, isFavorite: Int) -> Unit = { _, _ -> },
    onInfoClick: () -> Unit = {}
) {
    val isFavorite = rememberSaveable { mutableStateOf(station.isFavorite) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxSize()
            .padding(2.dp)
            .clickable {
                onCardClick(station.stationuuid)
            }
    ) {
        SubcomposeAsyncImage(
            model = station.favicon,
            contentDescription = null,
            modifier = Modifier
                .size(75.dp, 75.dp)
                .padding(5.dp),
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
        }

        IconButton(
            modifier = Modifier
                .padding(4.dp),
            onClick = {
                if (isFavorite.value == 0) {
                    isFavorite.value = 1
                } else {
                    isFavorite.value = 0
                }
                onFavoriteClick(station.stationuuid, isFavorite.value)
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

        IconButton(
            modifier = Modifier
                .padding(4.dp),
            onClick = onInfoClick
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun StationCardPreview() {
    FreeRadioTheme {
        MainContent(
            paddingValues = PaddingValues(),
            stations = listOf(fakeStation, fakeStation, fakeStation),
            onCardClick = {}
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun StationCardNightPreview() {
    FreeRadioTheme {
        MainContent(
            paddingValues = PaddingValues(),
            stations = listOf(fakeStation, fakeStation, fakeStation),
            onCardClick = {}
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