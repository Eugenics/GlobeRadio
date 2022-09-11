package com.eugenics.freeradio.ui.compose.main.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.eugenics.freeradio.R
import com.eugenics.freeradio.ui.theme.FreeRadioTheme

@Composable
fun MainTopBar(
    onDrawerClick: () -> Unit,
    onSearchClick: (query: String) -> Unit
) {
    var quetyText by rememberSaveable { mutableStateOf("Search text") }
    Row(
        modifier = Modifier.wrapContentSize(
            align = Alignment.Center
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onDrawerClick,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_drawer),
                contentDescription = null
            )
        }
        BasicTextField(
            value = quetyText,
            onValueChange = {
                quetyText = it
            },
            singleLine = true,
            textStyle = TextStyle(
                fontStyle = MaterialTheme.typography.titleLarge.fontStyle,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                color = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .weight(1f)
                .wrapContentSize(align = Alignment.CenterEnd)
        )

        IconButton(
            onClick = {
                onSearchClick(quetyText)
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun previewMainTopBar() {
    FreeRadioTheme {
        MainTopBar({}) { }
    }
}