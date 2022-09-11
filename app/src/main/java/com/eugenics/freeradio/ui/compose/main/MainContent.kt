package com.eugenics.freeradio.ui.compose.main

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
                    onCardClick = onCardClick
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StationCard(
    station: Station,
    onCardClick: (mediaId: String) -> Unit
) {
    Card(
        onClick = {
            onCardClick(station.stationuuid)
        },
        modifier = Modifier.padding(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
                .padding(5.dp)
        ) {
            SubcomposeAsyncImage(
                model = station.favicon.ifBlank {
                },
                contentDescription = null,
                modifier = Modifier
                    .size(75.dp, 75.dp)
                    .padding(5.dp),
                contentScale = ContentScale.Fit
            ) {
                val state = painter.state
                if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                    Image(
                        painter = painterResource(R.drawable.ic_music_note),
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
                    text = "(${station.tags})",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            IconButton(
                modifier = Modifier
                    .padding(8.dp),
                onClick = {}
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_info),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
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
)