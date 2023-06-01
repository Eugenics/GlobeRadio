package com.eugenics.data.di

import com.eugenics.core_database.database.dao.PrefsDao
import com.eugenics.core_database.database.dao.StationDao
import com.eugenics.data.data.datasources.StationsRemoteDataSource
import com.eugenics.data.data.datasources.StationsLocalDataSource
import com.eugenics.data.data.repository.StationsRepository
import com.eugenics.core.interfaces.IStationsApi
import com.eugenics.data.data.datasources.PrefsLocalDataSource
import com.eugenics.data.data.repository.PrefsRepository
import com.eugenics.data.interfaces.IPrefsLocalDataSource
import com.eugenics.data.interfaces.IPrefsRepository
import com.eugenics.data.interfaces.IStationsLocalDataSource
import com.eugenics.data.interfaces.IStationsRemoteDataSource
import com.eugenics.data.interfaces.IStationsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideStationsLocalDataSource(stationDao: StationDao): IStationsLocalDataSource =
        StationsLocalDataSource(stationDao = stationDao)

    @Provides
    @Singleton
    fun provideStationsRemoteDataSource(stationsApi: IStationsApi): IStationsRemoteDataSource =
        StationsRemoteDataSource(IStationsApi = stationsApi)

    @Provides
    @Singleton
    fun providePrefsDataSource(prefsDao: PrefsDao): IPrefsLocalDataSource =
        PrefsLocalDataSource(prefsDao = prefsDao)

    @Provides
    @Singleton
    fun provideStationsRepository(
        stationsRemoteDataSource: IStationsRemoteDataSource,
        stationsLocalDataSource: IStationsLocalDataSource
    ): IStationsRepository =
        StationsRepository(
            stationsRemoteDataSource = stationsRemoteDataSource,
            stationsLocalDataSource = stationsLocalDataSource
        )

    @Provides
    @Singleton
    fun providePrefsRepository(prefsLocalDataSource: IPrefsLocalDataSource): IPrefsRepository =
        PrefsRepository(prefsDataSource = prefsLocalDataSource)
}