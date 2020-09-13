package com.personal.accountantAssistant.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.personal.accountantAssistant.ui.bills.entities.Bills;
import com.personal.accountantAssistant.ui.bills.enums.BillsEnum;
import com.personal.accountantAssistant.ui.buys.entities.Buys;
import com.personal.accountantAssistant.ui.buys.enums.BuysEnum;
import com.personal.accountantAssistant.ui.payments.entities.Payments;
import com.personal.accountantAssistant.ui.payments.enums.PaymentsEnum;
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType;
import com.personal.accountantAssistant.utils.Constants;
import com.personal.accountantAssistant.utils.DataBaseUtils;
import com.personal.accountantAssistant.utils.DateUtils;
import com.personal.accountantAssistant.utils.NumberUtils;
import com.personal.accountantAssistant.utils.ParserUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

import static java.util.stream.Collectors.toList;

public class DatabaseManager extends SQLiteOpenHelper {

    private final static int DB_VERSION = 1;
    private final static String DB_NAME = "ACCOUNTANT_ASSISTANT";

    private final String WHERE_CLAUSE_PARAMETER = " = ?";
    private final String WHERE_CLAUSE_JOIN = " and ";
    private final String UID_WHERE_CLAUSE = Constants.UID + WHERE_CLAUSE_PARAMETER;
    private final String TYPE_WHERE_CLAUSE = PaymentsEnum.TYPE + WHERE_CLAUSE_PARAMETER;
    private final String UID_AND_TYPE_WHERE_CLAUSE = UID_WHERE_CLAUSE +
            WHERE_CLAUSE_JOIN +
            PaymentsEnum.TYPE + WHERE_CLAUSE_PARAMETER;

    private final String CREATE_TABLE_COMMAND = "CREATE TABLE";
    private final String SELECT_FROM_COMMAND = "SELECT * FROM";
    private final String PAYMENTS_TABLE = "PAYMENTS_TABLE";
    final String BUY_TABLE = "PRODUCTS_TABLE";
    final String BILL_TABLE = "BILLS_TABLE";

    private final String TEXT = "TEXT";
    private final String COMMA_SEPARATOR = ",";
    private final String SPACE_SEPARATOR = " ";
    private final String DOT_COMMA_SEPARATOR = ";";
    private final String AUTOINCREMENT = "AUTOINCREMENT";
    private final String INTEGER_PRIMARY_KEY = "INTEGER PRIMARY KEY";
    private final String SPACE_TEXT = SPACE_SEPARATOR + TEXT;
    private final String SPACE_TEXT_COMMA = SPACE_SEPARATOR + TEXT + COMMA_SEPARATOR;
    private final String SPACE_AUTOINCREMENT_COMMA = SPACE_SEPARATOR + AUTOINCREMENT + COMMA_SEPARATOR;

    private final String CREATE_PAYMENTS_TABLE_QUERY = CREATE_TABLE_COMMAND +
            SPACE_SEPARATOR +
            PAYMENTS_TABLE +
            "(" + Constants.UID +
            SPACE_SEPARATOR +
            INTEGER_PRIMARY_KEY +
            SPACE_AUTOINCREMENT_COMMA +
            PaymentsEnum.NAME.name() + SPACE_TEXT_COMMA +
            PaymentsEnum.QUANTITY.name() + SPACE_TEXT_COMMA +
            PaymentsEnum.DATE.name() + SPACE_TEXT_COMMA +
            PaymentsEnum.UNITARY_VALUE.name() + SPACE_TEXT_COMMA +
            PaymentsEnum.TOTAL_VALUE.name() + SPACE_TEXT_COMMA +
            PaymentsEnum.TYPE.name() + SPACE_TEXT_COMMA +
            PaymentsEnum.ACTIVE.name() + SPACE_TEXT + ")";

    public DatabaseManager(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PAYMENTS_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PAYMENTS_TABLE);
        onCreate(sqLiteDatabase);
    }

    private String getSelectAllQueryStrFrom(final String tableName) {
        return SELECT_FROM_COMMAND + SPACE_SEPARATOR + tableName + DOT_COMMA_SEPARATOR;
    }

    private Payments cursorToPayments(final Cursor cursor) {
        final Payments payments = new Payments();
        payments.setId(cursor.getInt(cursor.getColumnIndex(Constants.UID)));
        payments.setName(cursor.getString(cursor.getColumnIndex(PaymentsEnum.NAME.name())));
        payments.setQuantity(cursor.getInt(cursor.getColumnIndex(PaymentsEnum.QUANTITY.name())));
        payments.setDate(DateUtils.toDate(cursor.getString(cursor.getColumnIndex(PaymentsEnum.DATE.name()))));
        payments.setUnitaryValue(cursor.getDouble(cursor.getColumnIndex(PaymentsEnum.UNITARY_VALUE.name())));
        payments.setTotalValue(cursor.getDouble(cursor.getColumnIndex(PaymentsEnum.TOTAL_VALUE.name())));
        payments.setType(PaymentsType.valueOf(cursor.getString(cursor.getColumnIndex(PaymentsEnum.TYPE.name()))));
        payments.setActive(NumberUtils.toBoolean(cursor.getString(cursor.getColumnIndex(PaymentsEnum.ACTIVE.name()))));
        return payments;
    }

    private ContentValues toContentValues(final Payments payments) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(PaymentsEnum.NAME.name(), payments.getName());
        contentValues.put(PaymentsEnum.QUANTITY.name(), payments.getQuantity());
        contentValues.put(PaymentsEnum.DATE.name(), DateUtils.toString(payments.getDate()));
        contentValues.put(PaymentsEnum.UNITARY_VALUE.name(), payments.getUnitaryValue());
        contentValues.put(PaymentsEnum.TOTAL_VALUE.name(), payments.getTotalValue());
        contentValues.put(PaymentsEnum.TYPE.name(), payments.getType().name());
        contentValues.put(PaymentsEnum.ACTIVE.name(), payments.isActive());
        return contentValues;
    }

    private Cursor getOrCreateTable(final SQLiteDatabase sqLiteDatabase,
                                    final String selectQuery) {
        try {
            return sqLiteDatabase.rawQuery(selectQuery, null);
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<Payments> getPaymentsRecords() {

        Cursor cursor;
        final ArrayList<Payments> payments = new ArrayList<>();
        final SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        final String SELECT_ALL_PAYMENTS_RECORDS_QUERY = getSelectAllQueryStrFrom(PAYMENTS_TABLE);

        cursor = getOrCreateTable(sqLiteDatabase, SELECT_ALL_PAYMENTS_RECORDS_QUERY);

        if (ParserUtils.isNullObject(cursor)) {
            onCreate(sqLiteDatabase);
            cursor = getOrCreateTable(sqLiteDatabase, SELECT_ALL_PAYMENTS_RECORDS_QUERY);
        }

        if (!ParserUtils.isNullObject(cursor) && cursor.moveToFirst()) {
            payments.add(cursorToPayments(cursor));
            while (cursor.moveToNext()) {
                payments.add(cursorToPayments(cursor));
            }
        }

        sqLiteDatabase.close();
        return payments;
    }

    private List<Payments> getPaymentsRecordsBy(final PaymentsType paymentsType) {
        return getPaymentsRecords()
                .stream()
                .filter(it -> paymentsType.equals(it.getType()))
                .collect(toList());
    }

    public boolean anyActivePaymentsRecordsBy(final PaymentsType paymentsType) {
        return getPaymentsRecordsBy(paymentsType)
                .stream()
                .anyMatch(Payments::isActive);
    }

    public boolean allActivePaymentsRecordsBy(final PaymentsType paymentsType) {
        return getPaymentsRecordsBy(paymentsType)
                .stream()
                .allMatch(Payments::isActive);
    }

    public List<Bills> getBillsRecords() {
        return getPaymentsRecordsBy(PaymentsType.BILL)
                .stream()
                .map(Bills::new)
                .collect(toList());
    }

    public List<Buys> getBuysRecords() {
        return getPaymentsRecordsBy(PaymentsType.BUY)
                .stream()
                .map(Buys::new)
                .collect(toList());
    }

    public void insertDefaultBillsRecords() {
        for (BillsEnum defaultBillsEnum : BillsEnum.values()) {
            insertBillRecordFrom(new Bills(defaultBillsEnum.getValue()));
        }
    }

    public void insertDefaultBuysRecords() {
        for (BuysEnum products : BuysEnum.values()) {
            insertBuyRecordFrom(new Buys(products.getValue()));
        }
    }

    public void insertDefaultPaymentsRecords() {
        insertDefaultBuysRecords();
        insertDefaultBillsRecords();
    }

    private long insertPaymentRecordFrom(final Payments payments) {
        final SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        final ContentValues contentValues = toContentValues(payments);
        long id = sqLiteDatabase.insert(PAYMENTS_TABLE, null, contentValues);
        sqLiteDatabase.close();
        return id;
    }

    private long insertBillRecordFrom(final Bills bill) {
        return insertPaymentRecordFrom(ParserUtils.toPayments(bill));
    }

    private long insertBuyRecordFrom(final Buys buy) {
        return insertPaymentRecordFrom(ParserUtils.toPayments(buy));
    }

    private long updateQuery(final String table,
                             final ContentValues contentValues,
                             final String whereClause,
                             final String[] whereArgs) {
        final SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long id = sqLiteDatabase.update(table,
                contentValues,
                whereClause,
                whereArgs);
        sqLiteDatabase.close();
        return id;
    }

    private long paymentsUpdateQuery(final ContentValues contentValues,
                                     final String whereClause,
                                     final String[] whereArgs) {
        return updateQuery(PAYMENTS_TABLE,
                contentValues,
                whereClause,
                whereArgs);
    }

    private String[] toWhereArgs(final Payments payment) {
        return new String[]{String.valueOf(payment.getId()), payment.getType().name()};
    }

    public long updatePaymentsRecordFrom(final Payments payment) {
        return paymentsUpdateQuery(toContentValues(payment),
                UID_AND_TYPE_WHERE_CLAUSE,
                toWhereArgs(payment));
    }

    public long updateBuyRecordFrom(final Buys buy) {
        return updatePaymentsRecordFrom(new Payments(buy));
    }

    public long updateBillRecordFrom(final Bills bill) {
        return updatePaymentsRecordFrom(new Payments(bill));
    }

    private long deleteQuery(final String table,
                             final String whereClause,
                             final String[] whereArgs) {
        final SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long id = sqLiteDatabase.delete(table,
                whereClause,
                whereArgs);
        sqLiteDatabase.close();
        return id;
    }

    private long paymentDeleteQuery(final String whereClause,
                                    final String[] whereArgs) {
        return deleteQuery(PAYMENTS_TABLE,
                whereClause,
                whereArgs);
    }

    public long deletePaymentsRecordFrom(final Payments payment) {
        return paymentDeleteQuery(UID_AND_TYPE_WHERE_CLAUSE,
                toWhereArgs(payment));
    }

    long deleteBillRecordFrom(final Bills bill) {
        return deletePaymentsRecordFrom(ParserUtils.toPayments(bill));
    }

    long deleteBuyRecordFrom(final Buys buy) {
        return deletePaymentsRecordFrom(ParserUtils.toPayments(buy));
    }

    public long deleteAllPaymentsRecords() {
        return paymentDeleteQuery(null, null);
    }

    private long deleteAllPaymentsRecordsBy(final PaymentsType paymentsType) {
        return paymentDeleteQuery(TYPE_WHERE_CLAUSE,
                new String[]{paymentsType.name()});
    }

    public long deleteAllBuysRecord() {
        return deleteAllPaymentsRecordsBy(PaymentsType.BUY);
    }

    public long deleteAllBillsRecord() {
        return deleteAllPaymentsRecordsBy(PaymentsType.BILL);
    }

    public long insertOrUpdatePayment(final Payments payment) {
        if (DataBaseUtils.isNotDefault(payment.getId())) {
            return updatePaymentsRecordFrom(payment);
        } else {
            return insertPaymentRecordFrom(payment);
        }
    }
}
