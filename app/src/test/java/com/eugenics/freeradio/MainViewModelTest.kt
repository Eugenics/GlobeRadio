package com.eugenics.freeradio

import android.content.ComponentName
import android.content.Context
import androidx.datastore.core.DataStoreFactory
import com.eugenics.data.testing.FakeStationsRepositoryFactory
import com.eugenics.ui_core.data.enums.UIState
import com.eugenics.freeradio.ui.viewmodels.MainViewModel
import com.eugenics.freeradio.util.SettingsSerializer
import com.eugenics.media_service.media.FreeRadioMediaService
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    private var context = mockk<Context>()

    private val serviceConnection = FreeRadioMediaServiceConnection(
        context = context,
        serviceComponent = ComponentName(context, FreeRadioMediaService::class.java)
    )
    private val stationsRepository = FakeStationsRepositoryFactory.create()
    private val dataStore = DataStoreFactory.create(
        serializer = SettingsSerializer,
        produceFile = {
            File(
                context.applicationInfo.dataDir,
                "datastore/settings_data_store.pb"
            )
        }
    )

    private val mainViewModel = MainViewModel(
        mediaServiceConnection = serviceConnection,
        stationsRepository = stationsRepository,
        dataStore = dataStore
    )

    @Before
    fun setup() {
    }

    @Test
    fun testUIState() {
        assertEquals(UIState.UI_STATE_SPLASH, mainViewModel.uiState.value)
    }

    @Test
    fun testStationsInitList() {
        assertEquals(0, mainViewModel.stations.value.size)
    }

    @Test
    fun testStationsList() {

    }
}