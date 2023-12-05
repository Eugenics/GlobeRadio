package com.eugenics.ui.compose.main.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eugenics.ui.compose.util.PreviewSimple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    paddingValues: PaddingValues = PaddingValues(),
    onDrawerClick: () -> Unit,
    onSearchClick: (query: String) -> Unit
) {
    TopAppBar(
        modifier = Modifier
            .padding(top = paddingValues.calculateTopPadding()),
        navigationIcon = {
            IconButton(onClick = onDrawerClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = null
                )
            }
        },
        title = {},
        actions = {
            SearchText(onSearchClick)
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SearchText(
    onSearchClick: (queryText: String) -> Unit
) {
    var queryText by rememberSaveable { mutableStateOf("") }
    val keyboardControl = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = queryText,
        onValueChange = {
            queryText = it
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
            .padding(start = 50.dp),
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearchClick(queryText)
            keyboardControl?.hide()
            focusManager.clearFocus()
        }),
        leadingIcon = {
            IconButton(
                onClick = {
                    onSearchClick(queryText)
                    keyboardControl?.hide()
                    focusManager.clearFocus()
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            }
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    queryText = ""
                    onSearchClick(queryText)
                    keyboardControl?.hide()
                    focusManager.clearFocus()
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
private fun previewMainTopBar() {
    PreviewSimple {
        Row {
            SearchText { }
        }
    }
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
private fun previewMainTopBarDark() {
    PreviewSimple(isDarkTheme = true) {
        Row {
            SearchText { }
        }
    }
}