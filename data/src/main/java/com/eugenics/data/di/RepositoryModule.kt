package com.eugenics.data.di

import com.eugenics.data.data.repository.Repository
import com.eugenics.data.interfaces.repository.ILocalDataSource
import com.eugenics.data.interfaces.repository.INetworkDataSource
import com.eugenics.media_service.domain.interfaces.repository.IRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideRepository(
        @Named(NetworkModule.NETWORK_DATA_SOURCE_NAME)
        remoteDataSource: INetworkDataSource,
        localDataSource: ILocalDataSource,
        @Named(NetworkModule.FAKE_DATA_SOURCE_NAME)
        fakeNetworkDataSource: INetworkDataSource
    ): IRepository =
        Repository(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource,
            fakeINetworkDataSource = fakeNetworkDataSource
        )
}