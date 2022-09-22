package com.eugenics.freeradio.ui.compose.main.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.eugenics.freeradio.R
import com.eugenics.freeradio.domain.model.Station
import com.eugenics.freeradio.ui.theme.FreeRadioTheme
import com.eugenics.media_service.media.isPlaying

@Composable
fun MainBottomAppBar(
    paddingValues: PaddingValues = PaddingValues(),
    nowPlayingStation: Station = Station(name = "Sample name..."),
    playbackState: PlaybackStateCompat,
    onPlayClick: () -> Unit = {},
    onPauseClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(percent = 35),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier
            .padding(
                bottom = paddingValues.calculateBottomPadding() + 5.dp,
                start = 8.dp,
                end = 8.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            SubcomposeAsyncImage(
                model = nowPlayingStation.favicon,
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
            Text(
                text = nowPlayingStation.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
                    .padding(8.dp)
            )
            IconButton(
                onClick = {
                    if (playbackState.isPlaying) {
                        onPauseClick()
                    } else {
                        onPlayClick()
                    }
                },
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Image(
                    painter = if (playbackState.isPlaying) {
                        painterResource(R.drawable.ic_pause)
                    } else {
                        painterResource(R.drawable.ic_play_arrow)
                    },
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}

@Composable
@Preview
private fun bottomAppCardPreview() {
    FreeRadioTheme {
        MainBottomAppBar(
            playbackState = PlaybackStateCompat
                .Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0L, 0f)
                .build()
        )
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
private fun bottomAppCardPreviewDark() {
    FreeRadioTheme {
        MainBottomAppBar(
            playbackState = PlaybackStateCompat
                .Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0L, 0f)
                .build()
        )
    }
}