package com.eugenics.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.room.Room
import com.eugenics.core.model.CurrentPrefs
import com.eugenics.data.data.database.dao.StationDao
import com.eugenics.data.data.database.database.DataBase
import com.eugenics.data.data.datasources.LocalDataSourceImpl
import com.eugenics.data.data.datastore.PrefsSerializer
import com.eugenics.data.interfaces.repository.ILocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideDao(dataBase: DataBase): StationDao = dataBase.dao

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

    @Provides
    @Singleton
    fun provideLocalDataSource(dao: StationDao): ILocalDataSource =
        LocalDataSourceImpl.newInstance(dao = dao)

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<CurrentPrefs> =
        DataStoreFactory.create(
            serializer = PrefsSerializer,
            produceFile = {
                File(
                    context.applicationInfo.dataDir,
                    "service_datastore/prefs.pb"
                )
            }
        )
}