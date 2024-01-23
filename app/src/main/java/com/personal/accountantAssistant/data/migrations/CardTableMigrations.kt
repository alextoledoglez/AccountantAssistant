package com.personal.accountantAssistant.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.personal.accountantAssistant.data.AppDatabase.Companion.CARDS_TABLE_NAME
import com.personal.accountantAssistant.data.entities.CardEntity.Companion.ACTIVE
import com.personal.accountantAssistant.data.entities.CardEntity.Companion.AVAILABLE_VALUE
import com.personal.accountantAssistant.data.entities.CardEntity.Companion.COMPANY
import com.personal.accountantAssistant.data.entities.CardEntity.Companion.DATE
import com.personal.accountantAssistant.data.entities.CardEntity.Companion.ID
import com.personal.accountantAssistant.data.entities.CardEntity.Companion.LIMIT_VALUE
import com.personal.accountantAssistant.data.entities.CardEntity.Companion.NAME
import com.personal.accountantAssistant.data.entities.CardEntity.Companion.PASSWORD
import com.personal.accountantAssistant.data.entities.CardEntity.Companion.USED_VALUE
import com.personal.accountantAssistant.data.entities.CardEntity.Companion.VALUE

object CardTableMigrations : BaseTableMigration() {

    private const val TABLE_BACKUP = "${CARDS_TABLE_NAME}_BACKUP"

    private const val ACTIVE_FIELD_TYPE_SCHEMA_CHANGES = "$ID $INTEGER_TYPE PRIMARY KEY, " +
            "$COMPANY $TEXT_TYPE, " +
            "$NAME $TEXT_TYPE, " +
            "$PASSWORD $TEXT_TYPE, " +
            "$VALUE $TEXT_TYPE, " +
            "$ACTIVE $INTEGER_TYPE"

    private const val VALUE_FIELD_TYPE_SCHEMA_CHANGES = "$ID $INTEGER_TYPE PRIMARY KEY, " +
            "$COMPANY $TEXT_TYPE, " +
            "$NAME $TEXT_TYPE, " +
            "$PASSWORD $TEXT_TYPE, " +
            "$VALUE $REAL_TYPE, " +
            "$ACTIVE $INTEGER_TYPE"

    private const val TABLE_FIELDS = "$ID, $COMPANY, $NAME, $PASSWORD, $VALUE, $ACTIVE"

    private val CREATE_TABLE_BACKUP_WITH_ACTIVE_FIELD_CHANGES = createTable(
        table = TABLE_BACKUP, fieldsDefinitions = ACTIVE_FIELD_TYPE_SCHEMA_CHANGES
    )
    private val CREATE_TABLE_BACKUP_WITH_VALUE_FIELD_CHANGES = createTable(
        table = TABLE_BACKUP, fieldsDefinitions = VALUE_FIELD_TYPE_SCHEMA_CHANGES
    )
    private val COPY_TABLE_DATA = copyFieldsFromTableToTable(
        fields = TABLE_FIELDS,
        originTable = CARDS_TABLE_NAME,
        destinyTable = TABLE_BACKUP
    )
    private val DROP_TABLE = dropTable(CARDS_TABLE_NAME)
    private val RENAME_TABLE_BACKUP_TO_TABLE = renameTableTo(
        oldTable = TABLE_BACKUP, newTable = CARDS_TABLE_NAME
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

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(renameColumnTo(CARDS_TABLE_NAME, VALUE, AVAILABLE_VALUE))
            db.execSQL(addColumn(CARDS_TABLE_NAME, DATE, TEXT_TYPE))
            db.execSQL(addColumn(CARDS_TABLE_NAME, USED_VALUE, REAL_TYPE))
            db.execSQL(addColumn(CARDS_TABLE_NAME, LIMIT_VALUE, REAL_TYPE))
        }
    }
}