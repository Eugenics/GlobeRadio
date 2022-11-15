package com.eugenics.media_service.data.database.dao

import androidx.room.*
import com.eugenics.media_service.data.database.enteties.FavoritesTmpDaoObject
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

    @Query("DELETE FROM stations WHERE length(name) = 0")
    fun deleteStationsWithoutName()

    @Query("DELETE FROM stations")
    fun deleteStations()

    @Transaction
    fun refreshStations(stationsDao: List<StationDaoObject>) {
        insertStations(stationsDao = stationsDao)
        deleteStationsWithoutTags()
        deleteStationsWithoutName()
    }

    @Query("UPDATE stations SET is_favorite = 1 WHERE stationuuid = :stationUuid")
    fun addFavorite(stationUuid: String)

    @Query("UPDATE stations SET is_favorite = 0 WHERE stationuuid = :stationUuid")
    fun deleteFavorite(stationUuid: String)

    @Query("SELECT * FROM stations WHERE is_favorite = 1")
    fun fetchStationsByFavorites(): List<StationDaoObject>

    @Query("DELETE FROM favorites_tmp")
    fun deleteFavoritesTmp()

    @Query("SELECT stationuuid FROM stations WHERE is_favorite = 1")
    fun fetchFavoriteStations(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveFavoritesTmp(favoritesStations: List<FavoritesTmpDaoObject>)

    @Query(
        "UPDATE stations set is_favorite = 1 WHERE is_favorite = 0 AND stationuuid iN (SELECT stationuuid FROM favorites_tmp)"
    )
    fun restoreFavoritesStationInfo()

    @Transaction
    fun reloadAllStations(stationsDao: List<StationDaoObject>) {
        deleteFavoritesTmp()
        saveFavoritesTmp(fetchFavoriteStations().map { stationUuid ->
            FavoritesTmpDaoObject(stationuuid = stationUuid)
        })

        deleteStations()
        refreshStations(stationsDao = stationsDao)

        restoreFavoritesStationInfo()
    }
}