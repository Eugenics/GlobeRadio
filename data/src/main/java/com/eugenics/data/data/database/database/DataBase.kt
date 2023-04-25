package com.eugenics.data.data.database.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.eugenics.data.data.database.dao.PrefsDao
import com.eugenics.data.data.database.enteties.StationDaoObject
import com.eugenics.data.data.database.dao.StationDao
import com.eugenics.data.data.database.enteties.FavoritesTmpDaoObject
import com.eugenics.data.data.database.enteties.PrefsDaoObject
import com.eugenics.data.data.database.enteties.Tags

@Database(
    entities = [
        StationDaoObject::class,
        Tags::class,
        FavoritesTmpDaoObject::class,
        PrefsDaoObject::class
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
abstract class DataBase : RoomDatabase() {
    abstract val dao: StationDao
    abstract val prefsDao: PrefsDao
}