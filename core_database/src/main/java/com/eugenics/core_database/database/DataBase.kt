package com.eugenics.core_database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.eugenics.core_database.database.dao.PrefsDao
import com.eugenics.core_database.database.dao.StationDao
import com.eugenics.core_database.database.enteties.FavoritesTmpDaoObject
import com.eugenics.core_database.database.enteties.PrefsDaoObject
import com.eugenics.core_database.database.enteties.StationDaoObject
import com.eugenics.core_database.database.enteties.Tags

@Database(
    entities = [
        StationDaoObject::class,
        Tags::class,
        FavoritesTmpDaoObject::class,
        PrefsDaoObject::class
    ],
    version = 2,
    exportSchema = true
)
abstract class DataBase : RoomDatabase() {
    abstract val stationDao: StationDao
    abstract val prefsDao: PrefsDao
}