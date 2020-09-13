package com.personal.accountantAssistant.utils;

import android.app.Activity;
import android.content.Context;

import com.personal.accountantAssistant.R;
import com.personal.accountantAssistant.db.DatabaseManager;
import com.personal.accountantAssistant.ui.payments.entities.Payments;
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType;

import io.reactivex.functions.Action;

public class DataBaseUtils {

    public static boolean isNotDefault(final long idOrRecord) {
        return idOrRecord > Constants.DEFAULT_UID;
    }

    private static DatabaseManager newDatabaseManagerFrom(final Activity activity) {
        final Context context = ActivityUtils.parse(activity);
        return new DatabaseManager(context);
    }

    public static void saveDataFrom(final Activity activity,
                                    final Object entity,
                                    final Action onSuccess) {

        long recordSaved = Constants.DEFAULT_UID;
        final Payments payment = ParserUtils.toPayments(entity);
        final Context context = ActivityUtils.parse(activity);
        final DatabaseManager databaseManager = new DatabaseManager(context);

        if (!ParserUtils.isNullObject(payment)) {
            recordSaved = databaseManager.insertOrUpdatePayment(payment);
        }

        if (isNotDefault(recordSaved)) {
            CalendarsUtils.createCalendarEventFrom(context, payment);
            ActionUtils.runAction(onSuccess);
        } else {
            CalendarsUtils.deleteCalendarEventsFrom(context, payment);
            ToastUtils.showLongText(context, R.string.error_saving_your_data);
        }
    }

    static void deleteDataFrom(final Context context,
                               final Object entity) {

        long recordDeleted = Constants.DEFAULT_UID;
        final Payments payment = ParserUtils.toPayments(entity);
        final DatabaseManager databaseManager = new DatabaseManager(context);

        if (!ParserUtils.isNullObject(payment)) {
            recordDeleted = databaseManager.deletePaymentsRecordFrom(payment);
        }

        if (isNotDefault(recordDeleted)) {
            CalendarsUtils.deleteCalendarEventsFrom(context, payment);
            ToastUtils.showLongText(context, R.string.successfully_deleted_record);
            ActivityUtils.refreshBy(context);
        } else {
            ToastUtils.showLongText(context, R.string.error_deleting_records);
        }
    }

    public static void deleteRecord(final Context context,
                                    final Object entity) {
        DialogUtils.confirmationDialog(context,
                R.string.delete_record_title,
                R.string.delete_record_message,
                () -> deleteDataFrom(context, entity));
    }

    public static void saveDataFrom(final Activity activity,
                                    final Object entity) {
        saveDataFrom(activity, entity, () -> {
            final Context context = ActivityUtils.parse(activity);
            ToastUtils.showLongText(context, R.string.record_successfully_save);
            activity.finish();
        });
    }

    public static void saveDataFromWithoutFinish(final Activity activity,
                                                 final Object entity) {
        saveDataFrom(activity, entity, ActionUtils.NONE_ACTION_TO_DO);
    }

    public static boolean anyActivePaymentsRecordsBy(final Activity activity,
                                                     final PaymentsType paymentsType) {
        return newDatabaseManagerFrom(activity)
                .anyActivePaymentsRecordsBy(paymentsType);
    }

    public static boolean allActivePaymentsRecordsBy(final Activity activity,
                                                     final PaymentsType paymentsType) {
        return newDatabaseManagerFrom(activity)
                .allActivePaymentsRecordsBy(paymentsType);
    }
}
