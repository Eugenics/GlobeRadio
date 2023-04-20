package com.eugenics.freeradio.ui.compose.main.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.media.session.PlaybackState
import android.os.Build
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
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
import com.eugenics.core.model.NowPlayingStation
import com.eugenics.freeradio.R
import com.eugenics.freeradio.ui.theme.FreeRadioTheme

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainBottomAppBar(
    paddingValues: PaddingValues = PaddingValues(),
    nowPlayingStation: NowPlayingStation =
        NowPlayingStation.newInstance(name = "Sample name..."),
    playbackState: PlaybackStateCompat,
    onPlayClick: () -> Unit = {},
    onPauseClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(percent = 35),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.primary
//        ),
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
            Column(
                modifier = Modifier.weight(1f)
                    .padding(8.dp)
            ) {
                Text(
                    text = nowPlayingStation.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = nowPlayingStation.nowPlayingTitle.ifBlank { "" },
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            IconButton(
                onClick = {
                    if (playbackState.state == PlaybackState.STATE_PLAYING) {
                        onPauseClick()
                    } else {
                        onPlayClick()
                    }
                },
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Image(
                    painter = if (playbackState.state == PlaybackState.STATE_PLAYING) {
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

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview
private fun BottomAppCardPreview() {
    FreeRadioTheme {
        MainBottomAppBar(
            playbackState = PlaybackStateCompat
                .Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0L, 0f)
                .build()
        )
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
private fun BottomAppCardPreviewDark() {
    FreeRadioTheme {
        MainBottomAppBar(
            playbackState = PlaybackStateCompat
                .Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0L, 0f)
                .build()
        )
    }
}