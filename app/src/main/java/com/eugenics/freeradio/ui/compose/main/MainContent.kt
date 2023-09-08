package com.eugenics.freeradio.ui.compose.main

import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
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
    stations: State<List<Station>>,
    visibleIndex: State<Int> = mutableStateOf(0),
    nowPlayingStation: State<NowPlayingStation> = mutableStateOf(NowPlayingStation.emptyInstance()),
    onFavoriteClick: (command: String, bundle: Bundle?) -> Unit = { _, _ -> },
    onCardClick: (mediaId: String) -> Unit,
    onScrolled: (isScrolledUp: Boolean) -> Unit,
    onVisibleIndexChange: (index: Int) -> Unit = {}
) {
    val columnState = rememberLazyListState(
        initialFirstVisibleItemIndex = visibleIndex.value
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
            .debounce(250L)
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
            itemsIndexed(stations.value) { index, station ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(1.dp)
                        .padding(
                            top = if (index == 0) paddingValues.calculateTopPadding() + 0.dp
                            else 0.dp,
                            bottom = if (index == stations.value.size - 1) paddingValues.calculateBottomPadding() + 0.dp
                            else 0.dp,
                            start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                            end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                        )

                ) {
                    StationCard(
                        station = station,
                        onCardClick = { mediaId ->
                            onCardClick(mediaId)
                            activeCardIndex.value = index
                        },
                        onFavoriteClick = onFavoriteClick,
                        isActive = station.stationuuid == nowPlayingStation.value.stationUUID
                    )
                }
            }
        }
    }
}
