package com.eugenics.ui.compose.screens.detail.components

import android.graphics.Bitmap
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.eugenics.resource.R
import com.eugenics.ui.compose.util.PreviewBase

@Composable
fun AlbumArtBox(
    modifier: Modifier = Modifier,
    albumArtImageUri: String
) {
    val context = LocalContext.current
    val albumTemplateBitmap = AppCompatResources
        .getDrawable(context, R.drawable.pradio_wave)?.toBitmap()
        ?: Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    SubcomposeAsyncImage(
        model = albumArtImageUri,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier.padding(all = 8.dp)
            .fillMaxWidth()
    ) {
        val painterState = this.painter.state
        if (painterState is AsyncImagePainter.State.Error
            || painterState is AsyncImagePainter.State.Loading
        ) {
            Image(
                bitmap = albumTemplateBitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .padding(all = 8.dp)
            )
        } else {
            SubcomposeAsyncImageContent()
        }
    }
}

@Preview
@Composable
private fun AlbumArtBoxPreview() {
    PreviewBase(isDarkTheme = true) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                AlbumArtBox(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    albumArtImageUri = ""
                )
            }
        }
    }
}