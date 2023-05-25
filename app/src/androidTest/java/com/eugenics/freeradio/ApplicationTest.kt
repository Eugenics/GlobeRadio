package com.eugenics.freeradio

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import com.eugenics.freeradio.application.FreeRadioApplication
import com.eugenics.freeradio.ui.activity.MainActivity
import com.eugenics.freeradio.ui.application.Application
import com.eugenics.freeradio.ui.viewmodels.MainViewModel
import dagger.hilt.android.testing.CustomTestApplication
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@CustomTestApplication(FreeRadioApplication::class)
interface HiltTestApplication

@HiltAndroidTest
class ApplicationTest:HiltTestApplication{

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()

        composeRule.setContent {
            val viewModel = hiltViewModel<MainViewModel>()
            Application(viewModel)
        }
    }

    @Test
    fun startApplication() {
    }
}