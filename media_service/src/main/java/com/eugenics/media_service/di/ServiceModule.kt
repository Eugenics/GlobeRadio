package com.eugenics.media_service.di

import com.eugenics.media_service.media.FreeRadioMediaService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {

//    @Provides
//    @Singleton
//    fun provideFreeRadioMediaService(): FreeRadioMediaService =
//        FreeRadioMediaService()
}