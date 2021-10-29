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
import com.personal.accountantAssistant.ui.payments.entities.Payments
import com.personal.accountantAssistant.ui.payments.enums.PaymentsEnum
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
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
        sqLiteDatabase.execSQL(CREATE_PAYMENTS_TABLE_QUERY)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $PAYMENTS_TABLE")
        onCreate(sqLiteDatabase)
    }

    private fun getSelectAllQueryStrFrom(tableName: String): String {
        return "$SELECT_FROM_COMMAND$SPACE_SEPARATOR$tableName$DOT_COMMA_SEPARATOR"
    }

    private fun cursorToPayments(cursor: Cursor): Payments = Payments().apply {
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

    private fun toContentValues(payments: Payments?): ContentValues = ContentValues().apply {
        put(PaymentsEnum.NAME.name, payments?.name)
        put(PaymentsEnum.QUANTITY.name, payments?.quantity)
        put(PaymentsEnum.DATE.name, DateUtils.toString(payments?.date))
        put(PaymentsEnum.UNITARY_VALUE.name, payments?.unitaryValue)
        put(PaymentsEnum.TOTAL_VALUE.name, payments?.totalValue)
        put(PaymentsEnum.TYPE.name, payments?.type?.name)
        put(PaymentsEnum.ACTIVE.name, payments?.isActive)
    }

    @SuppressLint("Recycle")
    private fun getOrCreateTable(
        sqLiteDatabase: SQLiteDatabase,
        selectQuery: String
    ): Cursor? = try {
        sqLiteDatabase.rawQuery(selectQuery, null)
    } catch (e: Exception) {
        null
    }

    fun getPaymentsRecords(): ArrayList<Payments> {
        var cursor: Cursor?
        val payments = ArrayList<Payments>()
        val sqLiteDatabase = this.writableDatabase
        val SELECT_ALL_PAYMENTS_RECORDS_QUERY = getSelectAllQueryStrFrom(PAYMENTS_TABLE)
        cursor = getOrCreateTable(sqLiteDatabase, SELECT_ALL_PAYMENTS_RECORDS_QUERY)
        if (ParserUtils.isNullObject(cursor)) {
            onCreate(sqLiteDatabase)
            cursor = getOrCreateTable(sqLiteDatabase, SELECT_ALL_PAYMENTS_RECORDS_QUERY)
        }
        cursor?.let {
            if (it.moveToFirst()) {
                payments.add(cursorToPayments(it))
            }
            while (it.moveToNext()) {
                payments.add(cursorToPayments(it))
            }
        }
        sqLiteDatabase.close()
        return payments
    }

    private fun getPaymentsRecordsBy(type: PaymentsType): List<Payments>? =
        getPaymentsRecords().stream().filter { type == it.type }.collect(Collectors.toList())

    fun getSortedPaymentsRecordsBy(type: PaymentsType): List<Payments>? =
        getPaymentsRecords().stream().filter { it.type == type }
            .sorted(Comparator.comparing(Payments::date))
            .collect(Collectors.toList())

    fun anyActivePaymentsRecordsBy(type: PaymentsType): Boolean? = getPaymentsRecordsBy(type)
        ?.stream()?.anyMatch { obj: Payments -> obj.isActive }

    fun allActivePaymentsRecordsBy(paymentsType: PaymentsType): Boolean? =
        getPaymentsRecordsBy(paymentsType)?.stream()
            ?.allMatch { obj: Payments -> obj.isActive }

    fun insertDefaultBillsRecords() {
        for (defaultBillsEnum in BillsEnum.values()) {
            insertBillRecordFrom(Bills(defaultBillsEnum.value))
        }
    }

    fun insertDefaultBuysRecords() {
        for (products in BuysEnum.values()) {
            insertBuyRecordFrom(Buys(products.value))
        }
    }

    private fun insertPaymentRecordFrom(payments: Payments?): Long {
        val sqLiteDatabase = this.writableDatabase
        val contentValues = toContentValues(payments)
        val id = sqLiteDatabase.insert(PAYMENTS_TABLE, null, contentValues)
        sqLiteDatabase.close()
        return id
    }

    private fun insertBillRecordFrom(bill: Bills): Long? = ParserUtils.toPayments(bill)?.let {
        insertPaymentRecordFrom(it)
    }

    private fun insertBuyRecordFrom(buy: Buys): Long? = ParserUtils.toPayments(buy)?.let {
        insertPaymentRecordFrom(it)
    }

    private fun updateQuery(
        table: String,
        contentValues: ContentValues,
        whereClause: String,
        whereArgs: Array<String?>?
    ): Long {
        val sqLiteDatabase = this.writableDatabase
        val id = sqLiteDatabase.update(
            table,
            contentValues,
            whereClause,
            whereArgs
        ).toLong()
        sqLiteDatabase.close()
        return id
    }

    private fun paymentsUpdateQuery(
        contentValues: ContentValues,
        whereClause: String,
        whereArgs: Array<String?>?
    ): Long = updateQuery(
        PAYMENTS_TABLE,
        contentValues,
        whereClause,
        whereArgs
    )

    private fun toWhereArgs(payment: Payments?): Array<String?>? = payment?.let {
        arrayOf(it.id.toString(), it.type?.name)
    }

    fun updatePaymentsRecordFrom(payment: Payments?): Long = paymentsUpdateQuery(
        toContentValues(payment),
        UID_AND_TYPE_WHERE_CLAUSE,
        toWhereArgs(payment)
    )

    private fun deleteQuery(
        table: String,
        whereClause: String?,
        whereArgs: Array<String?>?
    ): Long {
        val sqLiteDatabase = this.writableDatabase
        val id = sqLiteDatabase.delete(
            table,
            whereClause,
            whereArgs
        ).toLong()
        sqLiteDatabase.close()
        return id
    }

    private fun paymentDeleteQuery(
        whereClause: String?, whereArgs: Array<String?>?
    ): Long = deleteQuery(
        PAYMENTS_TABLE,
        whereClause,
        whereArgs
    )

    fun deletePaymentsRecordFrom(payment: Payments?): Long = paymentDeleteQuery(
        UID_AND_TYPE_WHERE_CLAUSE,
        toWhereArgs(payment)
    )

    private fun deleteAllPaymentsRecordsBy(paymentsType: PaymentsType): Long = paymentDeleteQuery(
        TYPE_WHERE_CLAUSE, arrayOf(paymentsType.name)
    )

    fun deleteAllBuysRecord(): Long = deleteAllPaymentsRecordsBy(PaymentsType.BUY)

    fun deleteAllBillsRecord(): Long = deleteAllPaymentsRecordsBy(PaymentsType.BILL)

    fun insertOrUpdatePayment(payment: Payments?): Long =
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