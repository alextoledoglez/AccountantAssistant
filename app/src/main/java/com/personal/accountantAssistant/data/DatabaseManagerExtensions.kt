package com.personal.accountantAssistant.data

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.utils.*
import io.reactivex.functions.Action


fun DatabaseManager.isNotDefaultRecord(idOrRecord: Long?): Boolean = !isDefaultRecord(idOrRecord)


fun DatabaseManager.isDefaultRecord(idOrRecord: Long?): Boolean {
    return idOrRecord?.let { it.toInt() == Constants.DEFAULT_UID } ?: false
}

@RequiresApi(Build.VERSION_CODES.P)
fun DatabaseManager.saveDataFrom(activity: Activity?, entity: Any?, onSuccess: Action?) {
    val payment = ParserUtils.toPayments(entity)
    ActivityUtils.parse(activity)?.let { context ->
        val databaseManager = DatabaseManager(context)
        val recordSaved: Long = databaseManager.insertOrUpdatePayment(payment)
        if (isNotDefaultRecord(recordSaved)) {
            CalendarsUtils.createCalendarEventFrom(context, payment)
            onSuccess?.let { ActionUtils.runAction(it) }
        } else {
            CalendarsUtils.deleteCalendarEventsFrom(context, payment)
            ToastUtils.showLongText(context, R.string.error_saving_your_data)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
fun DatabaseManager.deleteDataFrom(context: Context?, entity: Any?, onSuccess: Action?) {
    val payment = ParserUtils.toPayments(entity)
    context?.let { ctx ->
        val databaseManager = DatabaseManager(ctx)
        val recordDeleted = databaseManager.deletePaymentsRecordFrom(payment)
        if (isNotDefaultRecord(recordDeleted)) {
            CalendarsUtils.deleteCalendarEventsFrom(ctx, payment)
            ToastUtils.showLongText(ctx, R.string.successfully_deleted_record)
            onSuccess?.let { ActionUtils.runAction(it) }
        } else {
            ToastUtils.showLongText(ctx, R.string.error_deleting_records)
        }
    }
}

//@JvmStatic
@RequiresApi(Build.VERSION_CODES.P)
fun DatabaseManager.deleteRecord(context: Context?, entity: Any?, onSuccess: Action?) {
    DialogUtils.confirmationDialog(
        context,
        R.string.delete_record_title,
        R.string.delete_record_message
    ) { deleteDataFrom(context, entity, onSuccess) }
}
