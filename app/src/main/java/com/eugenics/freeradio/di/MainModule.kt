package com.eugenics.freeradio.di

import android.content.ComponentName
import android.content.Context
import com.eugenics.media_service.media.FreeRadioMediaService
import com.eugenics.media_service.media.FreeRadioMediaServiceConnection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MainModule {
    @Singleton
    @Provides
    fun provideMediaServiceConnection(@ApplicationContext context: Context): FreeRadioMediaServiceConnection =
        FreeRadioMediaServiceConnection.getInstance(
            context = context,
            ComponentName(context, FreeRadioMediaService::class.java)
        )
}