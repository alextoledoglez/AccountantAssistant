package com.personal.accountantAssistant.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.db.DatabaseManager
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import com.personal.accountantAssistant.utils.ActionUtils.runAction
import com.personal.accountantAssistant.utils.ActivityUtils.parse
import com.personal.accountantAssistant.utils.CalendarsUtils.createCalendarEventFrom
import com.personal.accountantAssistant.utils.CalendarsUtils.deleteCalendarEventsFrom
import io.reactivex.functions.Action

object DataBaseUtils {
    @JvmStatic
    fun isNotDefaultRecord(idOrRecord: Long?): Boolean {
        return !isDefaultRecord(idOrRecord)
    }

    @JvmStatic
    fun isDefaultRecord(idOrRecord: Long?): Boolean {
        return idOrRecord?.let { it.toInt() == Constants.DEFAULT_UID } ?: false
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun newDatabaseManagerFrom(activity: Activity?): DatabaseManager? {
        return parse(activity)?.let { context ->
            DatabaseManager(context)
        }
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.P)
    fun saveDataFrom(activity: Activity?, entity: Any?, onSuccess: Action?) {
        val payment = ParserUtils.toPayments(entity)
        parse(activity)?.let { context ->
            val databaseManager = DatabaseManager(context)
            val recordSaved: Long = databaseManager.insertOrUpdatePayment(payment)
            if (isNotDefaultRecord(recordSaved)) {
                createCalendarEventFrom(context, payment)
                onSuccess?.let { runAction(it) }
            } else {
                deleteCalendarEventsFrom(context, payment)
                ToastUtils.showLongText(context, R.string.error_saving_your_data)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun deleteDataFrom(context: Context?, entity: Any?, onSuccess: Action?) {
        val payment = ParserUtils.toPayments(entity)
        context?.let { ctx ->
            val databaseManager = DatabaseManager(ctx)
            val recordDeleted = databaseManager.deletePaymentsRecordFrom(payment)
            if (isNotDefaultRecord(recordDeleted)) {
                deleteCalendarEventsFrom(ctx, payment)
                ToastUtils.showLongText(ctx, R.string.successfully_deleted_record)
                onSuccess?.let { runAction(it) }
            } else {
                ToastUtils.showLongText(ctx, R.string.error_deleting_records)
            }
        }
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.P)
    fun deleteRecord(context: Context, entity: Any?, onSuccess: Action?) {
        DialogUtils.confirmationDialog(context,
                R.string.delete_record_title,
                R.string.delete_record_message,
                ParserUtils.toAction(deleteDataFrom(context, entity, onSuccess))
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun saveDataFromWithoutFinish(activity: Activity?, entity: Any?, onSuccess: Action?) {
        saveDataFrom(activity, entity, onSuccess)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @JvmStatic
    fun anyActivePaymentsRecordsBy(activity: Activity?, paymentsType: PaymentsType?): Boolean? {
        return paymentsType?.let { type -> activity?.let { newDatabaseManagerFrom(it) }?.anyActivePaymentsRecordsBy(type) }
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.P)
    fun allActivePaymentsRecordsBy(activity: Activity?, paymentsType: PaymentsType?): Boolean? {
        return paymentsType?.let { type -> activity?.let { newDatabaseManagerFrom(it) }?.allActivePaymentsRecordsBy(type) }
    }
}