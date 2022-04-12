package com.personal.accountantAssistant.data.migrations

import com.personal.accountantAssistant.data.AppDatabase
import com.personal.accountantAssistant.data.entities.ExpenseEntity

object ExpensesTableMigrations : BaseTableMigration() {

    private const val TABLE_BACKUP = "${AppDatabase.EXPENSES_TABLE}_BACKUP"

    private const val FIELDS_SCHEME_CHANGES = "${ExpenseEntity.ID} INTEGER PRIMARY KEY, " +
            "${ExpenseEntity.NAME} TEXT, " +
            "${ExpenseEntity.QUANTITY} INTEGER, " +
            "${ExpenseEntity.DATE} TEXT, " +
            "${ExpenseEntity.UNITARY_VALUE} REAL, " +
            "${ExpenseEntity.TOTAL_VALUE} REAL, " +
            "${ExpenseEntity.TYPE} TEXT, " +
            "${ExpenseEntity.ACTIVE} INTEGER"

    private const val FIELDS_DEFINITIONS = "${ExpenseEntity.ID}, " +
            "${ExpenseEntity.NAME}, " +
            "${ExpenseEntity.QUANTITY}, " +
            "${ExpenseEntity.DATE}, " +
            "${ExpenseEntity.UNITARY_VALUE}, " +
            "${ExpenseEntity.TOTAL_VALUE}, " +
            "${ExpenseEntity.TYPE}, " +
            ExpenseEntity.ACTIVE

    private val CREATE_TABLE_BACKUP_WITH_SCHEME_CHANGES = createTable(
        table = TABLE_BACKUP, fieldsDefinitions = FIELDS_SCHEME_CHANGES
    )
    private val COPY_TABLE_DATA = copyFieldsFromTableToTable(
        fields = FIELDS_DEFINITIONS,
        originTable = AppDatabase.EXPENSES_TABLE,
        destinyTable = TABLE_BACKUP
    )
    private val DROP_TABLE = dropTable(AppDatabase.EXPENSES_TABLE)
    private val RENAME_TABLE_BACKUP_TO_TABLE = renameTableTo(
        oldTable = TABLE_BACKUP, newTable = AppDatabase.EXPENSES_TABLE
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
        oldTable = "PAYMENTS_TABLE",
        newTable = AppDatabase.EXPENSES_TABLE
    )
}