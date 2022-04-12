package com.personal.accountantAssistant.data.migrations

import com.personal.accountantAssistant.data.AppDatabase
import com.personal.accountantAssistant.data.entities.CardEntity

object CardTableMigrations : BaseTableMigration() {

    private const val TABLE_BACKUP = "${AppDatabase.CARDS_TABLE_NAME}_BACKUP"

    private const val ACTIVE_FIELD_TYPE_SCHEMA_CHANGES = "${CardEntity.ID} INTEGER PRIMARY KEY, " +
            "${CardEntity.COMPANY} TEXT, " +
            "${CardEntity.NAME} TEXT, " +
            "${CardEntity.PASSWORD} TEXT, " +
            "${CardEntity.VALUE} TEXT, " +
            "${CardEntity.ACTIVE} INTEGER"

    private const val VALUE_FIELD_TYPE_SCHEMA_CHANGES = "${CardEntity.ID} INTEGER PRIMARY KEY, " +
            "${CardEntity.COMPANY} TEXT, " +
            "${CardEntity.NAME} TEXT, " +
            "${CardEntity.PASSWORD} TEXT, " +
            "${CardEntity.VALUE} REAL, " +
            "${CardEntity.ACTIVE} INTEGER"

    private const val TABLE_FIELDS = "${CardEntity.ID}, " +
            "${CardEntity.COMPANY}, " +
            "${CardEntity.NAME}, " +
            "${CardEntity.PASSWORD}, " +
            "${CardEntity.VALUE}, " +
            CardEntity.ACTIVE

    private val CREATE_TABLE_BACKUP_WITH_ACTIVE_FIELD_CHANGES = createTable(
        table = TABLE_BACKUP, fieldsDefinitions = ACTIVE_FIELD_TYPE_SCHEMA_CHANGES
    )
    private val CREATE_TABLE_BACKUP_WITH_VALUE_FIELD_CHANGES = createTable(
        table = TABLE_BACKUP, fieldsDefinitions = VALUE_FIELD_TYPE_SCHEMA_CHANGES
    )
    private val COPY_TABLE_DATA = copyFieldsFromTableToTable(
        fields = TABLE_FIELDS,
        originTable = AppDatabase.CARDS_TABLE_NAME,
        destinyTable = TABLE_BACKUP
    )
    private val DROP_TABLE = dropTable(AppDatabase.CARDS_TABLE_NAME)
    private val RENAME_TABLE_BACKUP_TO_TABLE = renameTableTo(
        oldTable = TABLE_BACKUP, newTable = AppDatabase.CARDS_TABLE_NAME
    )

    val MIGRATION_1_2 = setTableSchemaMigration(
        startVersion = 1, endVersion = 2,
        CREATE_TABLE_BACKUP_WITH_ACTIVE_FIELD_CHANGES,
        COPY_TABLE_DATA,
        DROP_TABLE,
        RENAME_TABLE_BACKUP_TO_TABLE
    )

    val MIGRATION_2_3 = setTableSchemaMigration(
        startVersion = 2, endVersion = 3,
        CREATE_TABLE_BACKUP_WITH_VALUE_FIELD_CHANGES,
        COPY_TABLE_DATA,
        DROP_TABLE,
        RENAME_TABLE_BACKUP_TO_TABLE
    )
}