package com.eugenics.core_database.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.eugenics.core_database.database.enteties.PrefsDaoObject

@Dao
interface PrefsDao {

    @Query("SELECT * FROM prefs LIMIT 1")
    suspend fun fetchPrefs(): PrefsDaoObject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrefs(prefsObject: PrefsDaoObject)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePrefs(prefsObject: PrefsDaoObject)

    @Query("DELETE FROM prefs")
    suspend fun deletePrefs()
}