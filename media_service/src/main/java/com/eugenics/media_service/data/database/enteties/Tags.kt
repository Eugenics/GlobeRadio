package com.eugenics.media_service.data.database.enteties

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "APP_TAGS")
data class Tags(
    @PrimaryKey
    @ColumnInfo(name = "uid")
    val uid: Int = 0,
    @ColumnInfo(name = "app_tag_name")
    val appTagName: String = ""
)
