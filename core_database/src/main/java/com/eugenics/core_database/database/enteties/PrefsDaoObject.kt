package com.eugenics.core_database.database.enteties

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.eugenics.core.enums.Commands
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
    companion object {
        fun newInstance(
            uuid: String = UUID.randomUUID().toString(),
            tag: String = "",
            stationUUID: String = "",
            command: String = Commands.STATIONS_COMMAND.name,
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

fun PrefsDaoObject.asModel(): CurrentPrefs =
    CurrentPrefs(
        tag = tag,
        stationUuid = stationUUID,
        command = command,
        query = query,
        uuid = uuid
    )
