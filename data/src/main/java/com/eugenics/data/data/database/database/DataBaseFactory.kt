package com.eugenics.data.data.database.database

import android.content.Context
import androidx.room.Room

object DataBaseFactory {
    fun create(context: Context): DataBase =
        Room.databaseBuilder(
            context,
            DataBase::class.java,
            "local_db.db"
        )
            .createFromAsset("preLoadDb.db")
            .build()
}