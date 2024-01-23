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

    fun renameColumnTo(
        table: String, oldColumn: String, newColumn: String
    ) = "ALTER TABLE $table RENAME COLUMN $oldColumn TO $newColumn"

    fun addColumn(
        table: String, column: String, columnType: String
    ) = "ALTER TABLE $table ADD COLUMN $column $columnType"

    fun dropTable(table: String) = "DROP TABLE $table"

    fun setTableSchemaMigration(
        startVersion: Int,
        endVersion: Int,
        createTable: String,
        insertTable: String,
        dropTable: String,
        renameTable: String
    ) = object : Migration(startVersion, endVersion) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(createTable)
            db.execSQL(insertTable)
            db.execSQL(dropTable)
            db.execSQL(renameTable)
        }
    }

    fun renameTableMigration(
        startVersion: Int, endVersion: Int, oldTable: String, newTable: String
    ) = object : Migration(startVersion, endVersion) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(renameTableTo(oldTable, newTable))
        }
    }

    companion object {
        const val INTEGER_TYPE = "INTEGER"
        const val TEXT_TYPE = "TEXT"
        const val REAL_TYPE = "REAL"
    }
}