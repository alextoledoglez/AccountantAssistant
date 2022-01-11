package com.personal.accountantAssistant.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity
import com.personal.accountantAssistant.data.entities.wallet.CardEntity
import com.personal.accountantAssistant.data.enums.bills.BillsEnum
import com.personal.accountantAssistant.data.enums.buys.BuysEnum
import com.personal.accountantAssistant.data.enums.expenses.ExpensesFieldsEnum
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType
import com.personal.accountantAssistant.data.enums.wallet.CardFieldsEnum
import com.personal.accountantAssistant.data.enums.wallet.DefaultCardsEnum
import com.personal.accountantAssistant.data.mappers.toExpense
import com.personal.accountantAssistant.domain.models.bills.BillModel
import com.personal.accountantAssistant.domain.models.buys.BuyModel
import com.personal.accountantAssistant.extensions.getDoubleColumn
import com.personal.accountantAssistant.extensions.getIntColumn
import com.personal.accountantAssistant.extensions.getStringColumn
import com.personal.accountantAssistant.utils.*
import java.util.*
import java.util.stream.Collectors

class DatabaseManager @RequiresApi(Build.VERSION_CODES.P) constructor(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {

        private const val DB_VERSION = 1

        const val DB_NAME = "ACCOUNTANT_ASSISTANT"

        private const val WHERE_CLAUSE_PARAMETER = " = ?"
        private const val WHERE_CLAUSE_JOIN = " and "
        private const val UID_WHERE_CLAUSE = Constants.UID + WHERE_CLAUSE_PARAMETER
        private val TYPE_WHERE_CLAUSE = ExpensesFieldsEnum.TYPE.toString() + WHERE_CLAUSE_PARAMETER
        private val UID_AND_TYPE_WHERE_CLAUSE = UID_WHERE_CLAUSE +
                WHERE_CLAUSE_JOIN +
                ExpensesFieldsEnum.TYPE + WHERE_CLAUSE_PARAMETER

        private const val CREATE_TABLE_COMMAND = "CREATE TABLE"
        private const val SELECT_FROM_COMMAND = "SELECT * FROM"
        private const val PAYMENTS_TABLE = "PAYMENTS_TABLE"
        private const val CARD_TABLE = "CARD_TABLE"

        private const val TEXT = "TEXT"
        private const val COMMA_SEPARATOR = ","
        private const val SPACE_SEPARATOR = " "
        private const val DOT_COMMA_SEPARATOR = ";"
        private const val AUTOINCREMENT = "AUTOINCREMENT"
        private const val INTEGER_PRIMARY_KEY = "INTEGER PRIMARY KEY"
        private const val SPACE_TEXT = SPACE_SEPARATOR + TEXT
        private const val SPACE_TEXT_COMMA = SPACE_SEPARATOR + TEXT + COMMA_SEPARATOR
        private const val SPACE_AUTOINCREMENT_COMMA =
            SPACE_SEPARATOR + AUTOINCREMENT + COMMA_SEPARATOR

        private val CREATE_CARD_TABLE_QUERY = CREATE_TABLE_COMMAND +
                SPACE_SEPARATOR +
                CARD_TABLE +
                "(" + Constants.UID +
                SPACE_SEPARATOR +
                INTEGER_PRIMARY_KEY +
                SPACE_AUTOINCREMENT_COMMA +
                CardFieldsEnum.TITLE.name + SPACE_TEXT_COMMA +
                CardFieldsEnum.PASSWORD.name + SPACE_TEXT_COMMA +
                CardFieldsEnum.VALUE.name + SPACE_TEXT_COMMA +
                CardFieldsEnum.ACTIVE.name + SPACE_TEXT + ")"

        private val CREATE_PAYMENTS_TABLE_QUERY = CREATE_TABLE_COMMAND +
                SPACE_SEPARATOR +
                PAYMENTS_TABLE +
                "(" + Constants.UID +
                SPACE_SEPARATOR +
                INTEGER_PRIMARY_KEY +
                SPACE_AUTOINCREMENT_COMMA +
                ExpensesFieldsEnum.NAME.name + SPACE_TEXT_COMMA +
                ExpensesFieldsEnum.QUANTITY.name + SPACE_TEXT_COMMA +
                ExpensesFieldsEnum.DATE.name + SPACE_TEXT_COMMA +
                ExpensesFieldsEnum.UNITARY_VALUE.name + SPACE_TEXT_COMMA +
                ExpensesFieldsEnum.TOTAL_VALUE.name + SPACE_TEXT_COMMA +
                ExpensesFieldsEnum.TYPE.name + SPACE_TEXT_COMMA +
                ExpensesFieldsEnum.ACTIVE.name + SPACE_TEXT + ")"
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_CARD_TABLE_QUERY)
        sqLiteDatabase.execSQL(CREATE_PAYMENTS_TABLE_QUERY)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $CARD_TABLE")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $PAYMENTS_TABLE")
        onCreate(sqLiteDatabase)
    }

    private fun getSelectAllQueryStrFrom(tableName: String): String {
        return "$SELECT_FROM_COMMAND$SPACE_SEPARATOR$tableName$DOT_COMMA_SEPARATOR"
    }

    private fun cursorToCardEntity(cursor: Cursor): CardEntity = CardEntity().apply {
        id = cursor.getIntColumn(Constants.UID)
        title = cursor.getStringColumn(CardFieldsEnum.TITLE.name)
        password = cursor.getIntColumn(CardFieldsEnum.PASSWORD.name)
        value = cursor.getDoubleColumn(CardFieldsEnum.VALUE.name)
        isActive = NumberUtils.toBoolean(cursor.getStringColumn(CardFieldsEnum.ACTIVE.name))
    }

    private fun toCardContentValues(cardEntity: CardEntity?): ContentValues =
        ContentValues().apply {
            put(CardFieldsEnum.TITLE.name, cardEntity?.title)
            put(CardFieldsEnum.PASSWORD.name, cardEntity?.password)
            put(CardFieldsEnum.VALUE.name, cardEntity?.value)
            put(CardFieldsEnum.ACTIVE.name, cardEntity?.isActive)
        }

    private fun cursorToExpenseEntity(cursor: Cursor): ExpenseEntity = ExpenseEntity().apply {
        id = cursor.getIntColumn(Constants.UID)
        name = cursor.getStringColumn(ExpensesFieldsEnum.NAME.name)
        quantity = cursor.getIntColumn(ExpensesFieldsEnum.QUANTITY.name)
        date = DateUtils.toDate(cursor.getStringColumn(ExpensesFieldsEnum.DATE.name))
        unitaryValue = cursor.getDoubleColumn(ExpensesFieldsEnum.UNITARY_VALUE.name)
        totalValue = cursor.getDoubleColumn(ExpensesFieldsEnum.TOTAL_VALUE.name)
        type = cursor.getStringColumn(ExpensesFieldsEnum.TYPE.name)?.let {
            ExpensesType.valueOf(it)
        } ?: ExpensesType.NONE
        isActive = NumberUtils.toBoolean(cursor.getStringColumn(ExpensesFieldsEnum.ACTIVE.name))
    }

    private fun toExpenseContentValues(expenseEntity: ExpenseEntity?): ContentValues =
        ContentValues().apply {
            put(ExpensesFieldsEnum.NAME.name, expenseEntity?.name)
            put(ExpensesFieldsEnum.QUANTITY.name, expenseEntity?.quantity)
            put(ExpensesFieldsEnum.DATE.name, DateUtils.toString(expenseEntity?.date))
            put(ExpensesFieldsEnum.UNITARY_VALUE.name, expenseEntity?.unitaryValue)
            put(ExpensesFieldsEnum.TOTAL_VALUE.name, expenseEntity?.totalValue)
            put(ExpensesFieldsEnum.TYPE.name, expenseEntity?.type?.name)
            put(ExpensesFieldsEnum.ACTIVE.name, expenseEntity?.isActive)
        }

    @SuppressLint("Recycle")
    private fun getOrCreateTable(
        sqLiteDatabase: SQLiteDatabase, selectQuery: String
    ): Cursor? = try {
        sqLiteDatabase.rawQuery(selectQuery, null)
    } catch (e: Exception) {
        null
    }

    private fun getCardRecords(): ArrayList<CardEntity> {
        var cursor: Cursor?
        val cards = ArrayList<CardEntity>()
        val selectAllCardRecordsQuery = getSelectAllQueryStrFrom(CARD_TABLE)
        cursor = getOrCreateTable(writableDatabase, selectAllCardRecordsQuery)
        if (ParserUtils.isNullObject(cursor)) {
            writableDatabase.execSQL(CREATE_CARD_TABLE_QUERY)
            cursor = getOrCreateTable(writableDatabase, selectAllCardRecordsQuery)
        }
        cursor?.let {
            if (it.moveToFirst()) {
                cards.add(cursorToCardEntity(it))
            }
            while (it.moveToNext()) {
                cards.add(cursorToCardEntity(it))
            }
        }
        writableDatabase.close()
        return cards
    }

    fun getSortedCardRecords(): List<CardEntity> = getCardRecords()
        .sortedWith(compareBy(CardEntity::value, CardEntity::isActive))
        .reversed()

    fun isAnyCardRecordActive(): Boolean = getCardRecords().stream().anyMatch { obj: CardEntity ->
        obj.isActive == true
    }

    fun isAllCardRecordsActive(): Boolean = getCardRecords().stream().allMatch { obj: CardEntity ->
        obj.isActive == true
    }

    fun getExpensesRecords(): ArrayList<ExpenseEntity> {
        var cursor: Cursor?
        val expenses = ArrayList<ExpenseEntity>()
        val selectAllExpensesRecordsQuery = getSelectAllQueryStrFrom(PAYMENTS_TABLE)
        cursor = getOrCreateTable(writableDatabase, selectAllExpensesRecordsQuery)
        if (ParserUtils.isNullObject(cursor)) {
            writableDatabase.execSQL(CREATE_PAYMENTS_TABLE_QUERY)
            cursor = getOrCreateTable(writableDatabase, selectAllExpensesRecordsQuery)
        }
        cursor?.let {
            if (it.moveToFirst()) {
                expenses.add(cursorToExpenseEntity(it))
            }
            while (it.moveToNext()) {
                expenses.add(cursorToExpenseEntity(it))
            }
        }
        writableDatabase.close()
        return expenses
    }

    private fun getExpensesRecordsBy(type: ExpensesType): List<ExpenseEntity>? =
        getExpensesRecords().stream().filter { type == it.type }.collect(Collectors.toList())

    fun getSortedExpensesRecordsBy(type: ExpensesType): List<ExpenseEntity>? =
        getExpensesRecords().stream().filter { it.type == type }
            .sorted(Comparator.comparing(ExpenseEntity::date))
            .collect(Collectors.toList())

    fun anyActiveExpensesRecordsBy(type: ExpensesType): Boolean? = getExpensesRecordsBy(type)
        ?.stream()?.anyMatch { obj: ExpenseEntity -> obj.isActive }

    fun allActiveExpensesRecordsBy(expensesType: ExpensesType): Boolean? =
        getExpensesRecordsBy(expensesType)?.stream()
            ?.allMatch { obj: ExpenseEntity -> obj.isActive }

    fun insertDefaultBillsRecords() {
        for (defaultBillsEnum in BillsEnum.values()) {
            insertBillRecordFrom(BillModel(defaultBillsEnum.value))
        }
    }

    fun insertDefaultCardsRecords() {
        for (card in DefaultCardsEnum.values()) {
            insertCardRecordFrom(CardEntity(card.title))
        }
    }

    fun insertDefaultBuysRecords() {
        for (products in BuysEnum.values()) {
            insertBuyRecordFrom(BuyModel(products.value))
        }
    }

    private fun insertCardRecordFrom(card: CardEntity?): Long {
        val contentValues = toCardContentValues(card)
        val id = writableDatabase.insert(CARD_TABLE, null, contentValues)
        writableDatabase.close()
        return id
    }

    private fun insertExpenseRecordFrom(expenseEntity: ExpenseEntity?): Long {
        val contentValues = toExpenseContentValues(expenseEntity)
        val id = writableDatabase.insert(PAYMENTS_TABLE, null, contentValues)
        writableDatabase.close()
        return id
    }

    private fun insertBuyRecordFrom(buy: BuyModel): Long = insertExpenseRecordFrom(buy.toExpense())

    private fun insertBillRecordFrom(bill: BillModel): Long =
        insertExpenseRecordFrom(bill.toExpense())

    private fun updateQuery(
        table: String, contentValues: ContentValues, whereClause: String, whereArgs: Array<String?>?
    ): Long {
        val id = writableDatabase.update(table, contentValues, whereClause, whereArgs).toLong()
        writableDatabase.close()
        return id
    }

    private fun cardsUpdateQuery(
        contentValues: ContentValues, whereClause: String, whereArgs: Array<String?>?
    ): Long = updateQuery(CARD_TABLE, contentValues, whereClause, whereArgs)

    fun updateCardRecordFrom(card: CardEntity?): Long = cardsUpdateQuery(
        toCardContentValues(card), UID_WHERE_CLAUSE, arrayOf(card?.id.toString())
    )

    private fun expensesUpdateQuery(
        contentValues: ContentValues, whereClause: String, whereArgs: Array<String?>?
    ): Long = updateQuery(PAYMENTS_TABLE, contentValues, whereClause, whereArgs)

    private fun toWhereArgs(expenseEntity: ExpenseEntity?): Array<String?>? = expenseEntity?.let {
        arrayOf(it.id.toString(), it.type?.name)
    }

    fun updateExpenseRecordFrom(expenseEntity: ExpenseEntity?): Long = expensesUpdateQuery(
        toExpenseContentValues(expenseEntity), UID_AND_TYPE_WHERE_CLAUSE, toWhereArgs(expenseEntity)
    )

    private fun deleteQuery(table: String, whereClause: String?, whereArgs: Array<String?>?): Long {
        val id = writableDatabase.delete(table, whereClause, whereArgs).toLong()
        writableDatabase.close()
        return id
    }

    private fun cardDeleteQuery(
        whereClause: String?, whereArgs: Array<String?>?
    ): Long = deleteQuery(CARD_TABLE, whereClause, whereArgs)

    fun deleteCardsRecordFrom(card: CardEntity?): Long = cardDeleteQuery(
        UID_WHERE_CLAUSE, arrayOf(card?.id.toString())
    )

    private fun expensesDeleteQuery(
        whereClause: String?, whereArgs: Array<String?>?
    ): Long = deleteQuery(PAYMENTS_TABLE, whereClause, whereArgs)

    fun deleteExpenseRecordFrom(expenseEntity: ExpenseEntity?): Long = expensesDeleteQuery(
        UID_AND_TYPE_WHERE_CLAUSE, toWhereArgs(expenseEntity)
    )

    private fun deleteAllExpensesRecordsBy(expensesType: ExpensesType): Long = expensesDeleteQuery(
        TYPE_WHERE_CLAUSE, arrayOf(expensesType.name)
    )

    fun deleteAllCardsRecords(): Long = TODO()

    fun deleteAllBuysRecord(): Long = deleteAllExpensesRecordsBy(ExpensesType.BUY)

    fun deleteAllBillsRecord(): Long = deleteAllExpensesRecordsBy(ExpensesType.BILL)

    fun insertOrUpdateCard(card: CardEntity?): Long = if (isNotDefaultRecord(card?.id?.toLong())) {
        updateCardRecordFrom(card)
    } else {
        insertCardRecordFrom(card)
    }

    fun insertOrUpdateExpense(expenseEntity: ExpenseEntity?): Long =
        if (isNotDefaultRecord(expenseEntity?.id?.toLong())) {
            updateExpenseRecordFrom(expenseEntity)
        } else {
            insertExpenseRecordFrom(expenseEntity)
        }

    fun getExpensesTotalPriceUntil(type: ExpensesType, lastPeriodDate: Date?) = NumberUtils.roundTo(
        getSortedExpensesRecordsBy(type)?.stream()?.filter {
            it.isActive && DateUtils.isInRange(it.date, lastPeriodDate)
        }?.map { it.getTotalValue() }
            ?.reduce(Constants.DEFAULT_VALUE, CalculatorUtils.accumulatedDoubleSum)
    )
}