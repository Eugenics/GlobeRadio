package com.eugenics.freeradio.di

import com.eugenics.freeradio.data.repository.ILocalDataSourceImpl
import com.eugenics.freeradio.domain.repository.ILocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface LocalDataModule {
    @Binds
    @Singleton
    fun bindLocalDataSource(localDataSource: ILocalDataSourceImpl): ILocalDataSource
}