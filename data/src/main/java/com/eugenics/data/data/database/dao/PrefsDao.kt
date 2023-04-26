package com.eugenics.data.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.eugenics.data.data.database.enteties.PrefsDaoObject

@Dao
interface PrefsDao {

    @Query("SELECT * FROM prefs LIMIT 1")
    fun fetchPrefs(): List<PrefsDaoObject>

    @Insert
    fun insertPrefs(prefsObject: PrefsDaoObject)

    @Update
    fun updatePrefs(prefsObject: PrefsDaoObject)

    @Query("DELETE FROM prefs")
    fun deletePrefs()
}