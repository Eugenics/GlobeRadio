package com.eugenics.media_service.di

import android.content.Context
import com.eugenics.data.interfaces.IPrefsRepository
import com.eugenics.data.interfaces.IStationsRepository
import com.eugenics.media_service.media.MediaSource
import com.eugenics.media_service.media3.data.ContentDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
    @Provides
    @Singleton
    fun provideMediaDataSource(
        stationRepository: IStationsRepository,
        prefsRepository: IPrefsRepository
    ): MediaSource = MediaSource(stationRepository, prefsRepository)

    @Provides
    @Singleton
    fun providesContentDataSource(
        @ApplicationContext context: Context,
        stationRepository: IStationsRepository
    ): ContentDataSource = ContentDataSource(context, stationRepository)
}