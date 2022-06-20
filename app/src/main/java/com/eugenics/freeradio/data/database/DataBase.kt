package com.eugenics.freeradio.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.eugenics.freeradio.data.database.dao.StationDaoObject
import com.eugenics.freeradio.data.database.dao.StationDao

@Database(
    entities = [StationDaoObject::class],
    version = 1,
    exportSchema = true
)
abstract class DataBase : RoomDatabase() {
    abstract val dao: StationDao
}