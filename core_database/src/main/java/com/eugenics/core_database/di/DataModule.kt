package com.eugenics.core_database.di

import android.content.Context
import androidx.room.Room
import com.eugenics.core_database.database.DataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideDao(dataBase: DataBase): com.eugenics.core_database.database.dao.StationDao =
        dataBase.stationDao

    @Provides
    @Singleton
    fun providePrefsDao(dataBase: DataBase): com.eugenics.core_database.database.dao.PrefsDao =
        dataBase.prefsDao

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): DataBase =
        Room.databaseBuilder(
            context,
            DataBase::class.java,
            "local_db.db"
        )
            .createFromAsset("preLoadDb.db")
            .build()
}