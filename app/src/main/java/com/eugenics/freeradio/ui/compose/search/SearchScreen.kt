package com.eugenics.freeradio.ui.compose.search

import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.Image
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.eugenics.freeradio.R
import com.eugenics.freeradio.ui.viewmodels.SearchViewModel
import com.eugenics.media_service.media.isPlaying


@Composable
fun SearchScreen(
    navController: NavHostController,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val state = searchViewModel.state.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (state.value.isPlaying) {
                        searchViewModel.pause()
                    } else {
                        searchViewModel.play()
                    }
                }
            ) {
                Image(
                    painter = if (state.value.isPlaying) {
                        painterResource(R.drawable.ic_pause)
                    } else {
                        painterResource(R.drawable.ic_play_arrow)
                    },
                    contentDescription = null
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        SearchContent(viewModel = searchViewModel)
    }
}

@Composable
@Preview
private fun SearchScreenPreview() {
    SearchScreen(rememberNavController())
}