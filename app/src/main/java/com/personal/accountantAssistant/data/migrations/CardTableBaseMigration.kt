package com.personal.accountantAssistant.data.migrations

import com.personal.accountantAssistant.data.AppDatabase
import com.personal.accountantAssistant.data.entities.CardEntity

abstract class CardTableBaseMigration {

    companion object {

        private const val CARD_TABLE_BACKUP = "CARD_TABLE_BACKUP"

        const val CREATE_CARD_TABLE_BACKUP = "CREATE TABLE $CARD_TABLE_BACKUP"

        const val INSERT_CARD_TABLE_BACKUP_DATA =
            "INSERT INTO $CARD_TABLE_BACKUP " +
                    "(${CardEntity.ID}, " +
                    "${CardEntity.COMPANY}, " +
                    "${CardEntity.NAME}, " +
                    "${CardEntity.PASSWORD}, " +
                    "${CardEntity.VALUE}, " +
                    "${CardEntity.ACTIVE}) " +
                    "SELECT " +
                    "${CardEntity.ID}, " +
                    "${CardEntity.COMPANY}, " +
                    "${CardEntity.NAME}, " +
                    "${CardEntity.PASSWORD}, " +
                    "${CardEntity.VALUE}, " +
                    "${CardEntity.ACTIVE} FROM ${AppDatabase.CARD_TABLE}"

        const val RENAME_CARD_TABLE_BACKUP = "ALTER TABLE $CARD_TABLE_BACKUP " +
                "RENAME TO ${AppDatabase.CARD_TABLE}"

        const val DROP_CARD_TABLE = "DROP TABLE ${AppDatabase.CARD_TABLE}"
    }
}