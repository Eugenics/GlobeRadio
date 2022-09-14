package com.eugenics.media_service.data.database.dao

import androidx.room.*
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

    @Query("DELETE FROM stations WHERE length(tags) = 0")
    fun deleteStationsWithoutTags()

    @Transaction
    fun refreshStations(stationsDao: List<StationDaoObject>) {
        insertStations(stationsDao = stationsDao)
        deleteStationsWithoutTags()
    }

    @Query("INSERT INTO favorites values(:uuid,:stationUuid)")
    fun addFavorite(uuid: String, stationUuid: String)

    @Query("DELETE FROM favorites WHERE station_uuid = :stationUuid")
    fun deleteFavorite(stationUuid: String)

    @Query("SELECT * FROM stations WHERE stationuuid IN (SELECT station_uuid FROM favorites)")
    fun fetchStationsByFavorites(): List<StationDaoObject>
}