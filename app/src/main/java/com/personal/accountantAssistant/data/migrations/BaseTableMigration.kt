package com.personal.accountantAssistant.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

abstract class BaseTableMigration {

    fun createTable(
        table: String, fieldsDefinitions: String
    ) = "CREATE TABLE $table ($fieldsDefinitions)"

    fun copyFieldsFromTableToTable(
        fields: String, originTable: String, destinyTable: String
    ) = "INSERT INTO $destinyTable ($fields) SELECT $fields FROM $originTable"

    fun renameTableTo(
        oldTable: String, newTable: String
    ) = "ALTER TABLE $oldTable RENAME TO $newTable"

    fun dropTable(table: String) = "DROP TABLE $table"

    fun setTableSchemaMigration(
        startVersion: Int,
        endVersion: Int,
        createTable: String,
        insertTable: String,
        dropTable: String,
        renameTable: String
    ) = object : Migration(startVersion, endVersion) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(createTable)
            database.execSQL(insertTable)
            database.execSQL(dropTable)
            database.execSQL(renameTable)
        }
    }

    fun renameTableMigration(
        startVersion: Int, endVersion: Int, oldTable: String, newTable: String
    ) = object : Migration(startVersion, endVersion) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(renameTableTo(oldTable, newTable))
        }
    }
}