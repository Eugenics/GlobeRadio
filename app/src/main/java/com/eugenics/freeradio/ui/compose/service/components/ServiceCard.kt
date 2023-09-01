package com.eugenics.freeradio.ui.compose.service.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eugenics.freeradio.ui.theme.FreeRadioTheme

@Composable
fun ServiceCard(
    infoText: String,
    textColor: Color = Color.Black
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 25.dp)
    ) {
        Text(
            text = infoText,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = textColor,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
@Preview
private fun ServiceCardPreview() {
    FreeRadioTheme {
        val infoText = "Service info text..."
        ServiceCard(infoText)
    }
}