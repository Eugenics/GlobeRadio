package com.eugenics.freeradio.di

import android.content.ComponentName
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.eugenics.core.model.CurrentState
import com.eugenics.freeradio.util.SettingsSerializer
import com.eugenics.media_service.media.FreeRadioMediaService
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MainModule {

    @Singleton
    @Provides
    fun provideMediaServiceConnection(
        @ApplicationContext context: Context
    ): FreeRadioMediaServiceConnection =
        FreeRadioMediaServiceConnection.getInstance(
            context = context,
            ComponentName(context, FreeRadioMediaService::class.java)
        )

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<CurrentState> =
        DataStoreFactory.create(
            serializer = SettingsSerializer,
            produceFile = {
                File(
                    context.applicationInfo.dataDir,
                    "datastore/settings_data_store.pb"
                )
            }
        )
}