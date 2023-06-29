package com.eugenics.freeradio.ui.compose.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eugenics.core.model.NowPlayingStation
import com.eugenics.core.model.Station
import com.eugenics.freeradio.ui.compose.main.components.StationCard
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun MainContent(
    paddingValues: PaddingValues,
    stations: List<Station>,
    onFavoriteClick: (stationUuid: String, isFavorite: Int) -> Unit = { _, _ -> },
    onCardClick: (mediaId: String) -> Unit,
    onScrolled: (isScrolledUp: Boolean) -> Unit,
    visibleIndex: Int = 0,
    onVisibleIndexChange: (index: Int) -> Unit = {}
) {
    val columnState = rememberLazyListState(
        initialFirstVisibleItemIndex = visibleIndex
    )
    val columnVisibleIndex = rememberSaveable { mutableStateOf(columnState.firstVisibleItemIndex) }
    val firstVisibleIndex = rememberSaveable { mutableStateOf(0) }

    val activeCardIndex = rememberSaveable { mutableStateOf(0) }

    if (columnVisibleIndex.value > firstVisibleIndex.value) {
        onScrolled(true)
        firstVisibleIndex.value = columnVisibleIndex.value
    } else {
        onScrolled(false)
        firstVisibleIndex.value = columnVisibleIndex.value
    }

    LaunchedEffect(columnState) {
        snapshotFlow {
            columnState.firstVisibleItemIndex
        }
            .debounce(500L)
            .collectLatest { index ->
                onVisibleIndexChange(index)
            }
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
                    onCardClick = { mediaId ->
                        onCardClick(mediaId)
                        activeCardIndex.value = index
                    },
                    onFavoriteClick = onFavoriteClick,
                    isActive = activeCardIndex.value == index
                )
            }
        }
    }
}
