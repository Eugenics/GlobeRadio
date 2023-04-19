package com.eugenics.data.di

import android.content.Context
import com.eugenics.data.data.datasources.FileDataSourceImpl
import com.eugenics.data.interfaces.repository.IFileDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FileModule {

    @Provides
    @Singleton
    fun provideFileDataSource(@ApplicationContext context: Context): IFileDataSource =
        FileDataSourceImpl(applicationContext = context)
}