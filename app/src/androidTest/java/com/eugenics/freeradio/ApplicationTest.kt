package com.eugenics.freeradio

import androidx.compose.ui.test.junit4.createComposeRule
import com.eugenics.freeradio.ui.compose.main.MainScreen
import com.eugenics.freeradio.ui.compose.settings.SettingsScreen
import com.eugenics.freeradio.ui.theme.FreeRadioTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ApplicationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun settingsScreenTest() {
        composeRule.setContent {
            FreeRadioTheme {
                SettingsScreen()
            }
        }
    }

    @Test
    fun mainScreenTest() {
        composeRule.setContent {
            FreeRadioTheme {
                MainScreen(tagsList = listOf())
            }
        }
    }
}