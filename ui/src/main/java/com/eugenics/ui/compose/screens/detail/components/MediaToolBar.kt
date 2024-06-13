package com.eugenics.ui.compose.screens.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eugenics.resource.R
import com.eugenics.ui.compose.util.PreviewBase
import com.eugenics.ui_core.data.enums.PlayBackState

@Composable
fun MediaToolBar(
    modifier: Modifier = Modifier,
    playbackState: State<PlayBackState>,
    prevButtonState: State<Boolean>,
    nextButtonState: State<Boolean>,
    onPlayPressed: () -> Unit,
    onPrevPressed: () -> Unit,
    onNextPressed: () -> Unit
) {
    val buttonSize = 60.dp
    val spacerSize = 15.dp

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous button
        CustomIconButton(
            onClick = onPrevPressed,
            enabled = prevButtonState.value,
            stateLayerSize = 60.dp,
            modifier = Modifier.clip(CircleShape)
                .size(buttonSize)
        ) {
            Icon(
                painter = painterResource(R.drawable.skip_previous),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.size(spacerSize))

        // Play button
        CustomIconButton(
            onClick = onPlayPressed,
            stateLayerSize = 80.dp,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor =
                if (playbackState.value == PlayBackState.Playing)
                    MaterialTheme.colorScheme.tertiaryContainer
                else MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Icon(
                painter = painterResource(
                    if (playbackState.value == PlayBackState.Playing)
                        R.drawable.pause else R.drawable.play_arrow
                ),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.size(spacerSize))

        // Next button
        CustomIconButton(
            onClick = onNextPressed,
            enabled = nextButtonState.value,
            stateLayerSize = 60.dp,
            modifier = Modifier.clip(CircleShape)
                .size(buttonSize)
        ) {
            Icon(
                painter = painterResource(R.drawable.skip_next),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
@Preview
private fun PreviewMediaToolBar() {
    val playbackState = remember { mutableStateOf(PlayBackState.Pause) }
    val nextButtonState = remember { mutableStateOf(false) }
    val prevButtonState = remember { mutableStateOf(false) }
    PreviewBase {
        MediaToolBar(
            onPlayPressed = {},
            onPrevPressed = {},
            onNextPressed = {},
            playbackState = playbackState,
            nextButtonState = nextButtonState,
            prevButtonState = prevButtonState
        )
    }
}