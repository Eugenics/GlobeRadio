package com.eugenics.data.data.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.eugenics.data.data.database.enteties.StationDaoObject
import com.eugenics.data.data.database.dao.StationDao
import com.eugenics.data.data.database.enteties.FavoritesTmpDaoObject
import com.eugenics.data.data.database.enteties.Tags

@Database(
    entities = [
        StationDaoObject::class,
        Tags::class,
        FavoritesTmpDaoObject::class
    ],
    version = 1,
    exportSchema = true
)
abstract class DataBase : RoomDatabase() {
    abstract val dao: StationDao
}