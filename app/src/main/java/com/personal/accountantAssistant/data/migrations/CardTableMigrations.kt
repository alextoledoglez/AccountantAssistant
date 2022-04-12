package com.personal.accountantAssistant.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.personal.accountantAssistant.data.entities.CardEntity

object CardTableMigrations : CardTableBaseMigration() {

    private const val CREATE_CARD_TABLE_BACKUP_WITH_ACTIVE_FIELD_CHANGES =
        "$CREATE_CARD_TABLE_BACKUP " +
                "(${CardEntity.ID} INTEGER PRIMARY KEY, " +
                "${CardEntity.COMPANY} TEXT, " +
                "${CardEntity.NAME} TEXT, " +
                "${CardEntity.PASSWORD} TEXT, " +
                "${CardEntity.VALUE} TEXT, " +
                "${CardEntity.ACTIVE} INTEGER" +
                ")"

    private const val CREATE_CARD_TABLE_BACKUP_WITH_VALUE_FIELD_CHANGES =
        "$CREATE_CARD_TABLE_BACKUP " +
                "(${CardEntity.ID} INTEGER PRIMARY KEY, " +
                "${CardEntity.COMPANY} TEXT, " +
                "${CardEntity.NAME} TEXT, " +
                "${CardEntity.PASSWORD} TEXT, " +
                "${CardEntity.VALUE} REAL, " +
                "${CardEntity.ACTIVE} INTEGER" +
                ")"

    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(CREATE_CARD_TABLE_BACKUP_WITH_ACTIVE_FIELD_CHANGES)
            database.execSQL(INSERT_CARD_TABLE_BACKUP_DATA)
            database.execSQL(DROP_CARD_TABLE)
            database.execSQL(RENAME_CARD_TABLE_BACKUP)
        }
    }

    val MIGRATION_2_3: Migration = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(CREATE_CARD_TABLE_BACKUP_WITH_VALUE_FIELD_CHANGES)
            database.execSQL(INSERT_CARD_TABLE_BACKUP_DATA)
            database.execSQL(DROP_CARD_TABLE)
            database.execSQL(RENAME_CARD_TABLE_BACKUP)
        }
    }
}