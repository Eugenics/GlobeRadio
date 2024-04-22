package com.eugenics.freeradio

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AssetsTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setup() {
    }

    @Test
    fun getAssets() {
        val assets = context.assets.list("")
        Assert.assertEquals(assets != null, true)
    }
}