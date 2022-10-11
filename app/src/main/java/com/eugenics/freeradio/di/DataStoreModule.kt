package com.eugenics.freeradio.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.eugenics.freeradio.data.local.ref.SettingsDataSource
import com.eugenics.freeradio.data.local.ref.SettingsSerializer
import com.eugenics.freeradio.data.repository.RepositoryImpl
import com.eugenics.freeradio.domain.interfaces.Repository
import com.eugenics.freeradio.domain.model.CurrentState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    @Provides
    @Singleton
    fun providesDataStore(
        application: Application
    ): DataStore<CurrentState> =
        DataStoreFactory.create(serializer = SettingsSerializer, produceFile = {
            File(application.filesDir, "datastore/settings_data_store.pb")
        })

    @Provides
    @Singleton
    fun providesDataStoreDataSource(dataStore: DataStore<CurrentState>): SettingsDataSource =
        SettingsDataSource(dataStore = dataStore)

    @Provides
    @Singleton
    fun provideRepository(application: Application): Repository =
        RepositoryImpl(application = application)
}