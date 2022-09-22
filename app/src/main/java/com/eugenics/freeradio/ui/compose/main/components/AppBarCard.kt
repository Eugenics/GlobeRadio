package com.eugenics.freeradio.ui.compose.main.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eugenics.freeradio.ui.theme.FreeRadioTheme

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppBarCard(
    paddingValues: PaddingValues = PaddingValues(),
    onMenuClick: () -> Unit = {},
    onSearchClick: (query: String) -> Unit = { _ -> }
) {
    val text = remember { mutableStateOf(TextFieldValue("")) }
    val searchState = rememberSaveable { mutableStateOf(false) }

    val keyboardControl = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Card(
        shape = RoundedCornerShape(percent = 35),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier
            .padding(
                top = paddingValues.calculateTopPadding() + 5.dp,
                start = 8.dp,
                end = 8.dp
            )
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!searchState.value) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Image(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = null
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (searchState.value) {
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent
                            ),
                            value = text.value,
                            onValueChange = { value ->
                                text.value = value
                            },
                            textStyle = MaterialTheme.typography.bodyMedium,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                onSearchClick(text.value.text)
                                keyboardControl?.hide()
                                focusManager.clearFocus()
                            }),
                            placeholder = {
                                Text(
                                    text = "Search here...",
                                    modifier = Modifier.alpha(ContentAlpha.medium)
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        if (text.value.text.isNotBlank()) {
                                            text.value = TextFieldValue("")
                                        } else {
                                            searchState.value = false
                                            keyboardControl?.hide()
                                            focusManager.clearFocus()
                                        }
                                    },
                                    modifier = Modifier.padding(end = 16.dp)
                                ) {
                                    Image(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    } else {
                        IconButton(
                            onClick = { searchState.value = true },
                            modifier = Modifier
                                .padding(end = 16.dp)
                        ) {
                            Image(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultTopAppBar(
    paddingValues: PaddingValues = PaddingValues(),
    onMenuClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    TopAppBar(
        colors = TopAppBarDefaults.mediumTopAppBarColors(

        ),
        modifier = Modifier
            .padding(
                top = paddingValues.calculateTopPadding(),
                start = 8.dp,
                end = 8.dp
            ),
        title = {},
        navigationIcon = {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Image(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .padding(end = 16.dp)
            ) {
                Image(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                )
            }
        }
    )
}

@Composable
@Preview
private fun AppBarCardPreview() {
    FreeRadioTheme {
        AppBarCard()
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
private fun AppBarCardPreviewDark() {
    FreeRadioTheme {
        AppBarCard()
    }
}

//@Composable
//@Preview
//private fun TopAppPreview() {
//    FreeRadioTheme {
//        DefaultTopAppBar()
//    }
//}
//
//@Composable
//@Preview(uiMode = UI_MODE_NIGHT_YES)
//private fun TopAppPreviewDark() {
//    FreeRadioTheme {
//        DefaultTopAppBar()
//    }
//}