package com.personal.accountantAssistant.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
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

class DatabaseManager : SQLiteOpenHelper {

    companion object {

        private const val DB_VERSION = 1

        const val DB_NAME = "ACCOUNTANT_ASSISTANT"
        const val BUY_TABLE = "PRODUCTS_TABLE"
        const val BILL_TABLE = "BILLS_TABLE"

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
        private const val SPACE_AUTOINCREMENT_COMMA = SPACE_SEPARATOR + AUTOINCREMENT + COMMA_SEPARATOR

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

    @RequiresApi(Build.VERSION_CODES.P)
    constructor(context: Context?) : super(context, DB_NAME, null, DB_VERSION)

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

    private fun cursorToPayments(cursor: Cursor): Payments {
        val payments = Payments()
        payments.id = cursor.getInt(cursor.getColumnIndex(Constants.UID))
        payments.name = cursor.getString(cursor.getColumnIndex(PaymentsEnum.NAME.name))
        payments.quantity = cursor.getInt(cursor.getColumnIndex(PaymentsEnum.QUANTITY.name))
        payments.date = DateUtils.toDate(cursor.getString(cursor.getColumnIndex(PaymentsEnum.DATE.name)))
        payments.unitaryValue = cursor.getDouble(cursor.getColumnIndex(PaymentsEnum.UNITARY_VALUE.name))
        payments.totalValue = cursor.getDouble(cursor.getColumnIndex(PaymentsEnum.TOTAL_VALUE.name))
        payments.type = PaymentsType.valueOf(cursor.getString(cursor.getColumnIndex(PaymentsEnum.TYPE.name)))
        payments.isActive = NumberUtils.toBoolean(cursor.getString(cursor.getColumnIndex(PaymentsEnum.ACTIVE.name)))
        return payments
    }

    private fun toContentValues(payments: Payments?): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(PaymentsEnum.NAME.name, payments?.name)
        contentValues.put(PaymentsEnum.QUANTITY.name, payments?.quantity)
        contentValues.put(PaymentsEnum.DATE.name, DateUtils.toString(payments?.date))
        contentValues.put(PaymentsEnum.UNITARY_VALUE.name, payments?.unitaryValue)
        contentValues.put(PaymentsEnum.TOTAL_VALUE.name, payments?.totalValue)
        contentValues.put(PaymentsEnum.TYPE.name, payments?.type?.name)
        contentValues.put(PaymentsEnum.ACTIVE.name, payments?.isActive)
        return contentValues
    }

    @SuppressLint("Recycle")
    private fun getOrCreateTable(sqLiteDatabase: SQLiteDatabase,
                                 selectQuery: String): Cursor? {
        return try {
            sqLiteDatabase.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            null
        }
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
        cursor?.moveToFirst()?.apply {
            payments.add(cursorToPayments(cursor))
            while (cursor.moveToNext()) {
                payments.add(cursorToPayments(cursor))
            }
        }
        sqLiteDatabase.close()
        return payments
    }

    private fun getPaymentsRecordsBy(paymentsType: PaymentsType): List<Payments> {
        return getPaymentsRecords()
                .stream()
                .filter { paymentsType == it.type }
                .collect(Collectors.toList())
    }

    fun anyActivePaymentsRecordsBy(paymentsType: PaymentsType): Boolean {
        return getPaymentsRecordsBy(paymentsType)
                .stream()
                .anyMatch { obj: Payments -> obj.isActive }
    }

    fun allActivePaymentsRecordsBy(paymentsType: PaymentsType): Boolean {
        return getPaymentsRecordsBy(paymentsType)
                .stream()
                .allMatch { obj: Payments -> obj.isActive }
    }

    fun getBillsRecords(): List<Bills?>? {
        return getPaymentsRecordsBy(PaymentsType.BILL)
                .stream()
                .map { payments: Payments? -> payments?.let { Bills(it) } }
                .collect(Collectors.toList())
    }

    fun getBuysRecords(): List<Buys?>? {
        return getPaymentsRecordsBy(PaymentsType.BUY)
                .stream()
                .map { payments: Payments? -> payments?.let { Buys(it) } }
                .collect(Collectors.toList())
    }

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

    fun insertDefaultPaymentsRecords() {
        insertDefaultBuysRecords()
        insertDefaultBillsRecords()
    }

    private fun insertPaymentRecordFrom(payments: Payments?): Long {
        val sqLiteDatabase = this.writableDatabase
        val contentValues = toContentValues(payments)
        val id = sqLiteDatabase.insert(PAYMENTS_TABLE, null, contentValues)
        sqLiteDatabase.close()
        return id
    }

    private fun insertBillRecordFrom(bill: Bills): Long? {
        return ParserUtils.toPayments(bill)?.let { insertPaymentRecordFrom(it) }
    }

    private fun insertBuyRecordFrom(buy: Buys): Long? {
        return ParserUtils.toPayments(buy)?.let { insertPaymentRecordFrom(it) }
    }

    private fun updateQuery(table: String,
                            contentValues: ContentValues,
                            whereClause: String,
                            whereArgs: Array<String?>?): Long {
        val sqLiteDatabase = this.writableDatabase
        val id = sqLiteDatabase.update(table,
                contentValues,
                whereClause,
                whereArgs).toLong()
        sqLiteDatabase.close()
        return id
    }

    private fun paymentsUpdateQuery(contentValues: ContentValues,
                                    whereClause: String,
                                    whereArgs: Array<String?>?): Long {
        return updateQuery(PAYMENTS_TABLE,
                contentValues,
                whereClause,
                whereArgs)
    }

    private fun toWhereArgs(payment: Payments?): Array<String?>? {
        return payment?.let { arrayOf(it.id.toString(), it.type?.name) }
    }

    fun updatePaymentsRecordFrom(payment: Payments?): Long {
        return paymentsUpdateQuery(toContentValues(payment), UID_AND_TYPE_WHERE_CLAUSE, toWhereArgs(payment))
    }

    fun updateBuyRecordFrom(buy: Buys?): Long {
        return updatePaymentsRecordFrom(buy?.let { Payments(it) })
    }

    fun updateBillRecordFrom(bill: Bills?): Long {
        return updatePaymentsRecordFrom(bill?.let { Payments(it) })
    }

    private fun deleteQuery(table: String,
                            whereClause: String?,
                            whereArgs: Array<String?>?): Long {
        val sqLiteDatabase = this.writableDatabase
        val id = sqLiteDatabase.delete(table,
                whereClause,
                whereArgs).toLong()
        sqLiteDatabase.close()
        return id
    }

    private fun paymentDeleteQuery(whereClause: String?,
                                   whereArgs: Array<String?>?): Long {
        return deleteQuery(PAYMENTS_TABLE,
                whereClause,
                whereArgs)
    }

    fun deletePaymentsRecordFrom(payment: Payments?): Long {
        return paymentDeleteQuery(UID_AND_TYPE_WHERE_CLAUSE,
                toWhereArgs(payment))
    }

    fun deleteBillRecordFrom(bill: Bills?): Long {
        return deletePaymentsRecordFrom(ParserUtils.toPayments(bill))
    }

    fun deleteBuyRecordFrom(buy: Buys?): Long {
        return deletePaymentsRecordFrom(ParserUtils.toPayments(buy))
    }

    fun deleteAllPaymentsRecords(): Long {
        return paymentDeleteQuery(null, null)
    }

    private fun deleteAllPaymentsRecordsBy(paymentsType: PaymentsType): Long {
        return paymentDeleteQuery(TYPE_WHERE_CLAUSE, arrayOf(paymentsType.name))
    }

    fun deleteAllBuysRecord(): Long {
        return deleteAllPaymentsRecordsBy(PaymentsType.BUY)
    }

    fun deleteAllBillsRecord(): Long {
        return deleteAllPaymentsRecordsBy(PaymentsType.BILL)
    }

    fun insertOrUpdatePayment(payment: Payments?): Long {
        return if (DataBaseUtils.isNotDefaultRecord(payment?.id?.toLong())) {
            updatePaymentsRecordFrom(payment)
        } else {
            insertPaymentRecordFrom(payment)
        }
    }
}