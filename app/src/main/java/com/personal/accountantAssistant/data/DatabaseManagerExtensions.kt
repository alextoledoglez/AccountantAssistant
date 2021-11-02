package com.personal.accountantAssistant.data

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.core.AlertDialogBuilder
import com.personal.accountantAssistant.core.extensions.confirmationDialog
import com.personal.accountantAssistant.ui.payments.entities.Payments
import com.personal.accountantAssistant.utils.ActionUtils
import com.personal.accountantAssistant.utils.CalendarsUtils
import com.personal.accountantAssistant.utils.Constants
import com.personal.accountantAssistant.utils.ToastUtils
import io.reactivex.functions.Action


fun DatabaseManager.isNotDefaultRecord(idOrRecord: Long?): Boolean = !isDefaultRecord(idOrRecord)


fun DatabaseManager.isDefaultRecord(idOrRecord: Long?): Boolean {
    return idOrRecord?.let { it.toInt() == Constants.DEFAULT_UID } ?: false
}

@RequiresApi(Build.VERSION_CODES.P)
fun DatabaseManager.saveDataFrom(
    context: Context?, payment: Payments?, onSuccess: Action?
) = context?.let { ctx ->
    val recordSaved: Long = insertOrUpdatePayment(payment)
    if (isNotDefaultRecord(recordSaved)) {
        CalendarsUtils.createCalendarEventFrom(ctx, payment)
        onSuccess?.let { ActionUtils.runAction(it) }
    } else {
        CalendarsUtils.deleteCalendarEventsFrom(ctx, payment)
        ToastUtils.showLongText(ctx, R.string.error_saving_your_data)
    }
}

@RequiresApi(Build.VERSION_CODES.P)
fun DatabaseManager.deleteDataFrom(
    context: Context?, payment: Payments?, onSuccess: Action?
) = context?.let { ctx ->
    val recordDeleted = deletePaymentsRecordFrom(payment)
    if (isNotDefaultRecord(recordDeleted)) {
        CalendarsUtils.deleteCalendarEventsFrom(ctx, payment)
        ToastUtils.showLongText(ctx, R.string.successfully_deleted_record)
        onSuccess?.let { ActionUtils.runAction(it) }
    } else {
        ToastUtils.showLongText(ctx, R.string.error_deleting_records)
    }
}

@RequiresApi(Build.VERSION_CODES.P)
fun DatabaseManager.deleteRecord(
    context: Context?, payment: Payments?, onSuccess: Action?
) = context?.let {
    AlertDialogBuilder(it).confirmationDialog(
        R.string.delete_record_title,
        R.string.delete_record_message,
        { deleteDataFrom(context, payment, onSuccess) }
    )
}
