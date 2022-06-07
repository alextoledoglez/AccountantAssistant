package com.personal.accountantAssistant.data.migrations

import com.personal.accountantAssistant.data.AppDatabase.Companion.EXPENSES_TABLE_NAME
import com.personal.accountantAssistant.data.AppDatabase.Companion.PAYMENTS_TABLE_NAME
import com.personal.accountantAssistant.data.entities.ExpenseEntity.Companion.ACTIVE
import com.personal.accountantAssistant.data.entities.ExpenseEntity.Companion.DATE
import com.personal.accountantAssistant.data.entities.ExpenseEntity.Companion.ID
import com.personal.accountantAssistant.data.entities.ExpenseEntity.Companion.NAME
import com.personal.accountantAssistant.data.entities.ExpenseEntity.Companion.QUANTITY
import com.personal.accountantAssistant.data.entities.ExpenseEntity.Companion.TOTAL_VALUE
import com.personal.accountantAssistant.data.entities.ExpenseEntity.Companion.TYPE
import com.personal.accountantAssistant.data.entities.ExpenseEntity.Companion.UNITARY_VALUE

object ExpensesTableMigrations : BaseTableMigration() {

    private const val TABLE_BACKUP = "${EXPENSES_TABLE_NAME}_BACKUP"

    private const val FIELDS_SCHEME_CHANGES = "$ID $INTEGER_TYPE PRIMARY KEY, " +
            "$NAME $TEXT_TYPE, " +
            "$QUANTITY $INTEGER_TYPE, " +
            "$DATE $TEXT_TYPE, " +
            "$UNITARY_VALUE $REAL_TYPE, " +
            "$TOTAL_VALUE $REAL_TYPE, " +
            "$TYPE $TEXT_TYPE, " +
            "$ACTIVE $INTEGER_TYPE"

    private const val FIELDS_DEFINITIONS = "$ID, " +
            "$NAME, " +
            "$QUANTITY, " +
            "$DATE, " +
            "$UNITARY_VALUE, " +
            "$TOTAL_VALUE, " +
            "$TYPE, " +
            ACTIVE

    private val CREATE_TABLE_BACKUP_WITH_SCHEME_CHANGES = createTable(
        table = TABLE_BACKUP, fieldsDefinitions = FIELDS_SCHEME_CHANGES
    )
    private val COPY_TABLE_DATA = copyFieldsFromTableToTable(
        fields = FIELDS_DEFINITIONS,
        originTable = EXPENSES_TABLE_NAME,
        destinyTable = TABLE_BACKUP
    )
    private val DROP_TABLE = dropTable(EXPENSES_TABLE_NAME)
    private val RENAME_TABLE_BACKUP_TO_TABLE = renameTableTo(
        oldTable = TABLE_BACKUP, newTable = EXPENSES_TABLE_NAME
    )

    val MIGRATION_3_4 = setTableSchemaMigration(
        startVersion = 3, endVersion = 4,
        createTable = CREATE_TABLE_BACKUP_WITH_SCHEME_CHANGES,
        insertTable = COPY_TABLE_DATA,
        dropTable = DROP_TABLE,
        renameTable = RENAME_TABLE_BACKUP_TO_TABLE
    )

    val MIGRATION_4_5 = renameTableMigration(
        startVersion = 4,
        endVersion = 5,
        oldTable = PAYMENTS_TABLE_NAME,
        newTable = EXPENSES_TABLE_NAME
    )
}