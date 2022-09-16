package com.eugenics.media_service.data.database.dao

import androidx.room.*
import com.eugenics.media_service.data.database.enteties.StationDaoObject

@Dao
interface StationDao {

    @Query("SELECT * FROM stations ORDER BY name")
    fun fetchAllStationData(): List<StationDaoObject>

    @Query("SELECT * FROM stations WHERE name LIKE :name OR tags LIKE :name ORDER BY name")
    fun fetchStationByName(name: String): List<StationDaoObject>

    @Query("SELECT * FROM stations WHERE tags LIKE :tag ORDER BY name")
    fun fetchStationByTag(tag: String): List<StationDaoObject>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStation(stationDao: StationDaoObject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStations(stationsDao: List<StationDaoObject>)

    @Query("DELETE FROM stations WHERE length(tags) = 0")
    fun deleteStationsWithoutTags()

    @Transaction
    fun refreshStations(stationsDao: List<StationDaoObject>) {
        insertStations(stationsDao = stationsDao)
        deleteStationsWithoutTags()
    }

    @Query("UPDATE stations SET is_favorite = 1 WHERE stationuuid = :stationUuid")
    fun addFavorite(stationUuid: String)

    @Query("UPDATE stations SET is_favorite = 0 WHERE stationuuid = :stationUuid")
    fun deleteFavorite(stationUuid: String)

    @Query("SELECT * FROM stations WHERE is_favorite = 1")
    fun fetchStationsByFavorites(): List<StationDaoObject>
}