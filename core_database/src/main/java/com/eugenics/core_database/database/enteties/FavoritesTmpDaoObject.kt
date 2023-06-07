package com.eugenics.core_database.database.enteties

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "favorites_tmp")
data class FavoritesTmpDaoObject(
    @PrimaryKey
    @ColumnInfo(name = "uuid")
    val uuid: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "stationuuid")
    val stationuuid: String
)
