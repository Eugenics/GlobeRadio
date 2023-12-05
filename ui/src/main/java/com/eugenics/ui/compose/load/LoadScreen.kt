package com.eugenics.ui.compose.load

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eugenics.ui.compose.theme.FreeRadioTheme

@Composable
fun LoadContent(text: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoadGears(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 60.dp)
                .alpha(50f)
        )
    }
}

@Composable
@Preview(
    name = "LoadScreenLightMode",
    uiMode = UI_MODE_NIGHT_NO,
    apiLevel = 27,
    device = "spec:parent=pixel_5,orientation=portrait"
)
private fun loadScreenLightModePreview() {
    FreeRadioTheme {
        LoadContent(text = "Light test load...")
    }
}

@Composable
@Preview(
    name = "LoadScreenLightLandscapeMode",
    uiMode = UI_MODE_NIGHT_NO,
    apiLevel = 27,
    device = "spec:parent=pixel_5,orientation=landscape"
)
private fun loadScreenLightLandscapeModePreview() {
    FreeRadioTheme {
        LoadContent(text = "Light test load...")
    }
}

@Composable
@Preview(
    name = "LoadScreenDarkMode",
    uiMode = UI_MODE_NIGHT_YES,
    apiLevel = 27,
    device = "spec:parent=pixel_5,orientation=portrait"
)
private fun loadScreenDarkModePreview() {
    FreeRadioTheme {
        LoadContent(text = "Dark test load...")
    }
}