package com.eugenics.data.di

import com.eugenics.data.data.repository.IRepositoryImpl
import com.eugenics.data.interfaces.repository.IRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindRepository(repository: IRepositoryImpl): IRepository
}