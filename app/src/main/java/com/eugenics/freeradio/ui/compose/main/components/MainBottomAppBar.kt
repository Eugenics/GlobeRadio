package com.eugenics.freeradio.ui.compose.main.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.eugenics.core.model.NowPlayingStation
import com.eugenics.freeradio.R
import com.eugenics.freeradio.ui.theme.FreeRadioTheme
import com.eugenics.freeradio.ui.util.PlayBackState

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainBottomAppBar(
    modifier: Modifier = Modifier,
    nowPlayingStation: State<NowPlayingStation> =
        mutableStateOf(NowPlayingStation.newInstance(name = "Sample name...")),
    playbackState: State<PlayBackState>,
    onPlayClick: () -> Unit = {},
    onPauseClick: () -> Unit = {}
) {

    val onClick = remember(playbackState.value) {
        derivedStateOf {
            if (playbackState.value == PlayBackState.Playing) {
                onPauseClick
            } else {
                onPlayClick
            }
        }
    }

    val clickIcon = remember {
        derivedStateOf {
            if (playbackState.value is PlayBackState.Playing) {
                R.drawable.ic_pause
            } else {
                R.drawable.ic_play_arrow
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        SubcomposeAsyncImage(
            model = nowPlayingStation.value.favicon,
            contentDescription = null,
            modifier = Modifier
                .size(65.dp, 65.dp)
                .padding(10.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Fit
        ) {
            val painterState = painter.state
            if (painterState is AsyncImagePainter.State.Loading || painterState is AsyncImagePainter.State.Error) {
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
                .padding(8.dp)
        ) {
            Text(
                text = nowPlayingStation.value.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )
            Text(
                text = nowPlayingStation.value.nowPlayingTitle,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )
        }
        IconButton(
            onClick = onClick.value,
            modifier = Modifier.padding(end = 10.dp)
        ) {
            Icon(
                painter = painterResource(clickIcon.value),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview
private fun BottomAppCardPreview() {
    FreeRadioTheme {
        val playbackState = remember { mutableStateOf(PlayBackState.Playing) }
        MainBottomAppBar(playbackState = playbackState)
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
private fun BottomAppCardPreviewDark() {
    FreeRadioTheme {
        val playbackState = remember { mutableStateOf(PlayBackState.Playing) }
        MainBottomAppBar(playbackState = playbackState)
    }
}