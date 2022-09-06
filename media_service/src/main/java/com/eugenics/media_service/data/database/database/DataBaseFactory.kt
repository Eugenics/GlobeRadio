package com.eugenics.media_service.data.database.database

import android.content.Context
import androidx.room.Room
import com.eugenics.media_service.application.MediaServiceApplication

object DataBaseFactory {
    fun create(context: Context = MediaServiceApplication().getContext()): DataBase =
        Room.databaseBuilder(
            context,
            DataBase::class.java,
            "local_db.db"
        )
            .createFromAsset("preLoadDb.db")
            .build()
}