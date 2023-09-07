package com.eugenics.core_database.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val sqlString = "ALTER TABLE stations ADD COLUMN votes INTEGER NOTNULL"
        database.execSQL(sqlString)
    }

}

//val MIGRATION_2_3 = object : Migration(2, 3) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        val sqlString = "ALTER TABLE stations DROP COLUMN votes"
//        database.execSQL(sqlString)
//    }
//
//}
//
//val MIGRATION_3_4 = object : Migration(3, 4) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        val sqlString = "ALTER TABLE stations ADD COLUMN votes INTEGER DEFAULT 0"
//        database.execSQL(sqlString)
//    }
//
//}