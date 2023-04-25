package com.eugenics.data.data.database.enteties

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.eugenics.core.enums.TagsCommands
import com.eugenics.core.model.CurrentPrefs
import java.util.UUID

@Entity(tableName = "prefs")
data class PrefsDaoObject(
    @PrimaryKey
    val uuid: String,
    @ColumnInfo(name = "tag")
    val tag: String,
    @ColumnInfo(name = "station_uuid")
    val stationUUID: String,
    @ColumnInfo(name = "command")
    val command: String,
    @ColumnInfo(name = "query")
    val query: String
) {
    fun convertToModel(): CurrentPrefs =
        CurrentPrefs(
            tag = tag,
            stationUuid = stationUUID,
            command = command,
            query = query
        )

    companion object {
        fun newInstance(
            uuid: String = UUID.randomUUID().toString(),
            tag: String = "",
            stationUUID: String = "",
            command: String = TagsCommands.STATIONS_COMMAND.name,
            query: String = ""
        ): PrefsDaoObject =
            PrefsDaoObject(
                uuid = uuid,
                tag = tag,
                stationUUID = stationUUID,
                command = command,
                query = query
            )
    }
}
