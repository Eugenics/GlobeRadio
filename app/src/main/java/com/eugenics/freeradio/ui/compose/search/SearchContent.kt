package com.eugenics.freeradio.ui.compose.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.eugenics.freeradio.domain.model.Station
import com.eugenics.freeradio.ui.viewmodels.SearchViewModel

@Composable
fun SearchContent(
    viewModel: SearchViewModel
) {
    val stations by remember { mutableStateOf(viewModel.stations) }

    Column {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(stations) { item, station ->
                viewModel.addMediaItem(item, station)
                StationCard(station) {
                    viewModel.play(item)
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun StationCard(
    station: Station,
    onCardClick: () -> Unit
) {
    Card(
        onClick = onCardClick,
        modifier = Modifier.padding(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
                .padding(5.dp)
        ) {
            SubcomposeAsyncImage(
                model = station.favicon,
                loading = { CircularProgressIndicator() },
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp, 50.dp)
                    .padding(5.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = station.name
            )
        }
    }
}

@Preview
@Composable
private fun StationCardPreview() {
    StationCard(fakeStation) {}
}

private val fakeStation = Station(
    stationuuid = "96202f73-0601-11e8-ae97-52543be04c81",
    name = "Radio Schizoid - Chillout / Ambient",
    tags = "Electronics",
    homepage = "",
    url = "http://94.130.113.214:8000/chill",
    urlResolved = "http://94.130.113.214:8000/chill",
    favicon = "http://static.radio.net/images/broadcasts/db/08/33694/c175.png",
    bitrate = 128,
    codec = "MP3",
    country = "India",
    countrycode = "IN",
    language = "hindi",
    languagecodes = "hi",
    changeuuid = "92c2fbdc-14ec-4861-af65-49dd7de7826f",
)