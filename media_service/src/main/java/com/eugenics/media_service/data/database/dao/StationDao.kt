package com.eugenics.media_service.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eugenics.media_service.data.database.enteties.StationDaoObject

@Dao
interface StationDao {

    @Query("SELECT * FROM stations ORDER BY name")
    fun fetchAllStationData(): List<StationDaoObject>

    @Query("SELECT * FROM stations WHERE name LIKE :name ORDER BY name")
    fun fetchStationByName(name: String): List<StationDaoObject>

    @Query("SELECT * FROM stations WHERE tags LIKE :tag ORDER BY name")
    fun fetchStationByTag(tag: String): List<StationDaoObject>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStation(stationDao: StationDaoObject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStations(stationsDao: List<StationDaoObject>)
}