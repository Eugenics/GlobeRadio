package com.eugenics.freeradio.ui.compose.search

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


@Composable
fun SearchScreen(
    navController: NavHostController,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val state = searchViewModel.state.collectAsState()

    LaunchedEffect(key1 = true) {
        searchViewModel.getStationsByName("80s")
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (state.value) {
                        searchViewModel.pause()
                    } else {
                        searchViewModel.play(searchViewModel.itemIndex)
                    }
                }
            ) {

                Image(
                    painter = if (state.value) {
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