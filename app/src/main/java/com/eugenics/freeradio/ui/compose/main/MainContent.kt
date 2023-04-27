package com.eugenics.freeradio.ui.compose.main

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.eugenics.core.model.Station
import com.eugenics.freeradio.R
import com.eugenics.freeradio.ui.theme.FreeRadioTheme

@Composable
fun MainContent(
    paddingValues: PaddingValues,
    stations: List<Station>,
    onFavoriteClick: (stationUuid: String, isFavorite: Int) -> Unit = { _, _ -> },
    onCardClick: (mediaId: String, mediaItemIndex: Int) -> Unit,
    onScrolled: (isScrolledUp: Boolean) -> Unit
) {
    val columnState = rememberLazyListState()
    val columnVisibleIndex = rememberSaveable { mutableStateOf(columnState.firstVisibleItemIndex) }
    val firstVisibleIndex = rememberSaveable { mutableStateOf(0) }

    if (columnVisibleIndex.value > firstVisibleIndex.value) {
        onScrolled(true)
        firstVisibleIndex.value = columnVisibleIndex.value
    } else {
        onScrolled(false)
        firstVisibleIndex.value = columnVisibleIndex.value
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = columnState
        ) {
            itemsIndexed(stations) { index, station ->
                StationCard(
                    paddingValues = paddingValues,
                    index = index,
                    size = stations.size,
                    station = station,
                    onCardClick = onCardClick,
                    onFavoriteClick = onFavoriteClick
                )
            }
        }
    }
}

@Composable
private fun StationCard(
    paddingValues: PaddingValues = PaddingValues(),
    index: Int = 0,
    size: Int = 0,
    station: Station,
    onCardClick: (mediaId: String, mediaItemIndex: Int) -> Unit = { _, _ -> },
    onFavoriteClick: (stationUuid: String, isFavorite: Int) -> Unit = { _, _ -> }
) {
    val isFavorite = rememberSaveable { mutableStateOf(station.isFavorite) }
    val standardPadding = 2.dp
    val topPadding =
        if (index == 0) paddingValues.calculateTopPadding() + 0.dp
        else standardPadding
    val bottomPadding =
        if (index == size - 1) paddingValues.calculateBottomPadding() + 0.dp
        else standardPadding

    val showFavoriteDialog = rememberSaveable { mutableStateOf(false) }
    if (showFavoriteDialog.value) {
        AlertDialog(
            onDismissRequest = { showFavoriteDialog.value = false },
            confirmButton = {
                Button(onClick = {
                    isFavorite.value = 0
                    showFavoriteDialog.value = false
                    onFavoriteClick(station.stationuuid, isFavorite.value)
                }) {
                    Text(text = stringResource(R.string.yes_string))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showFavoriteDialog.value = false }
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

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxSize()
            .padding(
                top = topPadding,
                bottom = bottomPadding,
                start = 8.dp,
                end = 8.dp
            )
            .clickable {
                onCardClick(station.stationuuid, index)
            }
    ) {
        SubcomposeAsyncImage(
            model = station.favicon,
            contentDescription = null,
            modifier = Modifier
                .size(65.dp, 65.dp)
                .padding(5.dp)
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
                    onFavoriteClick(station.stationuuid, isFavorite.value)
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

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun StationCardNightPreviewDay() {
    FreeRadioTheme {
        MainContent(
            paddingValues = PaddingValues(),
            stations = listOf(fakeStation, fakeStation, fakeStation),
            onCardClick = { _, _ -> },
            onScrolled = {}
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun StationCardNightPreviewNight() {
    FreeRadioTheme {
        MainContent(
            paddingValues = PaddingValues(),
            stations = listOf(fakeStation, fakeStation, fakeStation),
            onCardClick = { _, _ -> },
            onScrolled = {}
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
