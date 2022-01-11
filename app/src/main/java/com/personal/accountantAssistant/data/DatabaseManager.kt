package com.personal.accountantAssistant.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import com.personal.accountantAssistant.core.extensions.getDoubleColumn
import com.personal.accountantAssistant.core.extensions.getIntColumn
import com.personal.accountantAssistant.core.extensions.getStringColumn
import com.personal.accountantAssistant.ui.bills.entities.Bills
import com.personal.accountantAssistant.ui.bills.enums.BillsEnum
import com.personal.accountantAssistant.ui.buys.entities.Buys
import com.personal.accountantAssistant.ui.buys.enums.BuysEnum
import com.personal.accountantAssistant.ui.payments.entities.PaymentsEntity
import com.personal.accountantAssistant.ui.payments.enums.PaymentsEnum
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import com.personal.accountantAssistant.ui.wallet.entities.CardEntity
import com.personal.accountantAssistant.ui.wallet.enums.CardFieldsEnum
import com.personal.accountantAssistant.ui.wallet.enums.DefaultCardsEnum
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
        private val TYPE_WHERE_CLAUSE = PaymentsEnum.TYPE.toString() + WHERE_CLAUSE_PARAMETER
        private val UID_AND_TYPE_WHERE_CLAUSE = UID_WHERE_CLAUSE +
                WHERE_CLAUSE_JOIN +
                PaymentsEnum.TYPE + WHERE_CLAUSE_PARAMETER

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
                PaymentsEnum.NAME.name + SPACE_TEXT_COMMA +
                PaymentsEnum.QUANTITY.name + SPACE_TEXT_COMMA +
                PaymentsEnum.DATE.name + SPACE_TEXT_COMMA +
                PaymentsEnum.UNITARY_VALUE.name + SPACE_TEXT_COMMA +
                PaymentsEnum.TOTAL_VALUE.name + SPACE_TEXT_COMMA +
                PaymentsEnum.TYPE.name + SPACE_TEXT_COMMA +
                PaymentsEnum.ACTIVE.name + SPACE_TEXT + ")"
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

    private fun cursorToPaymentsEntity(cursor: Cursor): PaymentsEntity = PaymentsEntity().apply {
        id = cursor.getIntColumn(Constants.UID)
        name = cursor.getStringColumn(PaymentsEnum.NAME.name)
        quantity = cursor.getIntColumn(PaymentsEnum.QUANTITY.name)
        date = DateUtils.toDate(cursor.getStringColumn(PaymentsEnum.DATE.name))
        unitaryValue = cursor.getDoubleColumn(PaymentsEnum.UNITARY_VALUE.name)
        totalValue = cursor.getDoubleColumn(PaymentsEnum.TOTAL_VALUE.name)
        type = cursor.getStringColumn(PaymentsEnum.TYPE.name)?.let {
            PaymentsType.valueOf(it)
        } ?: PaymentsType.NONE
        isActive = NumberUtils.toBoolean(cursor.getStringColumn(PaymentsEnum.ACTIVE.name))
    }

    private fun toPaymentContentValues(paymentsEntity: PaymentsEntity?): ContentValues =
        ContentValues().apply {
            put(PaymentsEnum.NAME.name, paymentsEntity?.name)
            put(PaymentsEnum.QUANTITY.name, paymentsEntity?.quantity)
            put(PaymentsEnum.DATE.name, DateUtils.toString(paymentsEntity?.date))
            put(PaymentsEnum.UNITARY_VALUE.name, paymentsEntity?.unitaryValue)
            put(PaymentsEnum.TOTAL_VALUE.name, paymentsEntity?.totalValue)
            put(PaymentsEnum.TYPE.name, paymentsEntity?.type?.name)
            put(PaymentsEnum.ACTIVE.name, paymentsEntity?.isActive)
        }

    private fun toCardContentValues(cardEntity: CardEntity?): ContentValues =
        ContentValues().apply {
            put(CardFieldsEnum.TITLE.name, cardEntity?.title)
            put(CardFieldsEnum.PASSWORD.name, cardEntity?.password)
            put(CardFieldsEnum.VALUE.name, cardEntity?.value)
            put(CardFieldsEnum.ACTIVE.name, cardEntity?.isActive)
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

    fun getPaymentsRecords(): ArrayList<PaymentsEntity> {
        var cursor: Cursor?
        val payments = ArrayList<PaymentsEntity>()
        val selectAllPaymentsRecordsQuery = getSelectAllQueryStrFrom(PAYMENTS_TABLE)
        cursor = getOrCreateTable(writableDatabase, selectAllPaymentsRecordsQuery)
        if (ParserUtils.isNullObject(cursor)) {
            writableDatabase.execSQL(CREATE_PAYMENTS_TABLE_QUERY)
            cursor = getOrCreateTable(writableDatabase, selectAllPaymentsRecordsQuery)
        }
        cursor?.let {
            if (it.moveToFirst()) {
                payments.add(cursorToPaymentsEntity(it))
            }
            while (it.moveToNext()) {
                payments.add(cursorToPaymentsEntity(it))
            }
        }
        writableDatabase.close()
        return payments
    }

    private fun getPaymentsRecordsBy(type: PaymentsType): List<PaymentsEntity>? =
        getPaymentsRecords().stream().filter { type == it.type }.collect(Collectors.toList())

    fun getSortedPaymentsRecordsBy(type: PaymentsType): List<PaymentsEntity>? =
        getPaymentsRecords().stream().filter { it.type == type }
            .sorted(Comparator.comparing(PaymentsEntity::date))
            .collect(Collectors.toList())

    fun anyActivePaymentsRecordsBy(type: PaymentsType): Boolean? = getPaymentsRecordsBy(type)
        ?.stream()?.anyMatch { obj: PaymentsEntity -> obj.isActive }

    fun allActivePaymentsRecordsBy(paymentsType: PaymentsType): Boolean? =
        getPaymentsRecordsBy(paymentsType)?.stream()
            ?.allMatch { obj: PaymentsEntity -> obj.isActive }

    fun insertDefaultBillsRecords() {
        for (defaultBillsEnum in BillsEnum.values()) {
            insertBillRecordFrom(Bills(defaultBillsEnum.value))
        }
    }

    fun insertDefaultCardsRecords() {
        for (card in DefaultCardsEnum.values()) {
            insertCardRecordFrom(CardEntity(card.title))
        }
    }

    fun insertDefaultBuysRecords() {
        for (products in BuysEnum.values()) {
            insertBuyRecordFrom(Buys(products.value))
        }
    }

    private fun insertCardRecordFrom(card: CardEntity?): Long {
        val contentValues = toCardContentValues(card)
        val id = writableDatabase.insert(CARD_TABLE, null, contentValues)
        writableDatabase.close()
        return id
    }

    private fun insertPaymentRecordFrom(paymentsEntity: PaymentsEntity?): Long {
        val contentValues = toPaymentContentValues(paymentsEntity)
        val id = writableDatabase.insert(PAYMENTS_TABLE, null, contentValues)
        writableDatabase.close()
        return id
    }

    private fun insertBuyRecordFrom(buy: Buys): Long? = ParserUtils.toPayments(buy)?.let {
        insertPaymentRecordFrom(it)
    }

    private fun insertBillRecordFrom(bill: Bills): Long? = ParserUtils.toPayments(bill)?.let {
        insertPaymentRecordFrom(it)
    }

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

    private fun paymentsUpdateQuery(
        contentValues: ContentValues, whereClause: String, whereArgs: Array<String?>?
    ): Long = updateQuery(PAYMENTS_TABLE, contentValues, whereClause, whereArgs)

    private fun toWhereArgs(payment: PaymentsEntity?): Array<String?>? = payment?.let {
        arrayOf(it.id.toString(), it.type?.name)
    }

    fun updatePaymentsRecordFrom(payment: PaymentsEntity?): Long = paymentsUpdateQuery(
        toPaymentContentValues(payment),
        UID_AND_TYPE_WHERE_CLAUSE,
        toWhereArgs(payment)
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

    private fun paymentDeleteQuery(
        whereClause: String?, whereArgs: Array<String?>?
    ): Long = deleteQuery(PAYMENTS_TABLE, whereClause, whereArgs)

    fun deletePaymentsRecordFrom(payment: PaymentsEntity?): Long = paymentDeleteQuery(
        UID_AND_TYPE_WHERE_CLAUSE, toWhereArgs(payment)
    )

    private fun deleteAllPaymentsRecordsBy(paymentsType: PaymentsType): Long = paymentDeleteQuery(
        TYPE_WHERE_CLAUSE, arrayOf(paymentsType.name)
    )

    fun deleteAllCardsRecords(): Long = TODO()

    fun deleteAllBuysRecord(): Long = deleteAllPaymentsRecordsBy(PaymentsType.BUY)

    fun deleteAllBillsRecord(): Long = deleteAllPaymentsRecordsBy(PaymentsType.BILL)

    fun insertOrUpdateCard(card: CardEntity?): Long = if (isNotDefaultRecord(card?.id?.toLong())) {
        updateCardRecordFrom(card)
    } else {
        insertCardRecordFrom(card)
    }

    fun insertOrUpdatePayment(payment: PaymentsEntity?): Long =
        if (isNotDefaultRecord(payment?.id?.toLong())) {
            updatePaymentsRecordFrom(payment)
        } else {
            insertPaymentRecordFrom(payment)
        }

    fun getPaymentsTotalPriceUntil(type: PaymentsType, lastPeriodDate: Date?) = NumberUtils.roundTo(
        getSortedPaymentsRecordsBy(type)?.stream()?.filter {
            it.isActive && DateUtils.isInRange(it.date, lastPeriodDate)
        }?.map { it.getTotalValue() }
            ?.reduce(Constants.DEFAULT_VALUE, CalculatorUtils.accumulatedDoubleSum)
    )
}