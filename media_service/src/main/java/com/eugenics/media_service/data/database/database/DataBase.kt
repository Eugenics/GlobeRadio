package com.eugenics.media_service.data.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.eugenics.media_service.data.database.enteties.StationDaoObject
import com.eugenics.media_service.data.database.dao.StationDao
import com.eugenics.media_service.data.database.enteties.FavoritesDaoObject
import com.eugenics.media_service.data.database.enteties.Tags

@Database(
    entities = [
        StationDaoObject::class,
        Tags::class,
        FavoritesDaoObject::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DataBase : RoomDatabase() {
    abstract val dao: StationDao
}