package com.eugenics.core_database.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val sqlString = "ALTER TABLE stations ADD COLUMN votes INTEGER"
        database.execSQL(sqlString)
    }

}