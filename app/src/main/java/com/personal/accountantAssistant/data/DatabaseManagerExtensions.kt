package com.personal.accountantAssistant.data

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity
import com.personal.accountantAssistant.data.entities.wallet.CardEntity
import com.personal.accountantAssistant.extensions.DEFAULT_UID
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.showConfirmationFrom
import com.personal.accountantAssistant.utils.ActionUtils
import com.personal.accountantAssistant.utils.CalendarsUtils
import com.personal.accountantAssistant.utils.ToastUtils
import io.reactivex.functions.Action


fun DatabaseManager.isNotDefaultRecord(idOrRecord: Long?): Boolean = !isDefaultRecord(idOrRecord)


fun DatabaseManager.isDefaultRecord(idOrRecord: Long?): Boolean {
    return idOrRecord?.let { it.toInt() == Int.DEFAULT_UID }.orFalse()
}

@RequiresApi(Build.VERSION_CODES.P)
fun DatabaseManager.saveDataFrom(
    context: Context?, expenseEntity: ExpenseEntity?, onSuccess: Action?
) = context?.let { ctx ->
    val recordSaved: Long = insertOrUpdateExpense(expenseEntity)
    if (isNotDefaultRecord(recordSaved)) {
        CalendarsUtils.createCalendarEventFrom(ctx, expenseEntity)
        onSuccess?.let { ActionUtils.runAction(it) }
    } else {
        CalendarsUtils.deleteCalendarEventsFrom(ctx, expenseEntity)
        ToastUtils.showLongText(ctx, R.string.error_saving_your_data)
    }
}

@RequiresApi(Build.VERSION_CODES.P)
fun DatabaseManager.saveDataFrom(
    context: Context?, card: CardEntity?, onSuccess: Action?
) = context?.let { ctx ->
    val recordSaved: Long = insertOrUpdateCard(card)
    if (isNotDefaultRecord(recordSaved)) {
        onSuccess?.let { ActionUtils.runAction(it) }
    } else {
        ToastUtils.showLongText(ctx, R.string.error_saving_your_data)
    }
}

@RequiresApi(Build.VERSION_CODES.P)
fun DatabaseManager.deleteDataFrom(
    context: Context?, expenseEntity: ExpenseEntity?, onSuccess: Action?
) = context?.let { ctx ->
    val recordDeleted = deleteExpenseRecordFrom(expenseEntity)
    if (isNotDefaultRecord(recordDeleted)) {
        CalendarsUtils.deleteCalendarEventsFrom(ctx, expenseEntity)
        ToastUtils.showLongText(ctx, R.string.successfully_deleted_record)
        onSuccess?.let { ActionUtils.runAction(it) }
    } else {
        ToastUtils.showLongText(ctx, R.string.error_deleting_records)
    }
}

@RequiresApi(Build.VERSION_CODES.P)
fun DatabaseManager.deleteDataFrom(
    context: Context?, card: CardEntity?, onSuccess: Action?
) = context?.let { ctx ->
    val recordDeleted = deleteCardsRecordFrom(card)
    if (isNotDefaultRecord(recordDeleted)) {
        ToastUtils.showLongText(ctx, R.string.successfully_deleted_record)
        onSuccess?.let { ActionUtils.runAction(it) }
    } else {
        ToastUtils.showLongText(ctx, R.string.error_deleting_records)
    }
}

@RequiresApi(Build.VERSION_CODES.P)
fun DatabaseManager.deleteRecord(
    context: Context?, expenseEntity: ExpenseEntity?, onSuccess: Action?
) = context?.let {
    AlertDialogBuilder(it).showConfirmationFrom(
        R.string.delete_record_title,
        R.string.delete_record_message,
        { deleteDataFrom(context, expenseEntity, onSuccess) }
    )
}

@RequiresApi(Build.VERSION_CODES.P)
fun DatabaseManager.deleteRecord(
    context: Context?, card: CardEntity?, onSuccess: Action?
) = context?.let {
    AlertDialogBuilder(it).showConfirmationFrom(
        R.string.delete_record_title,
        R.string.delete_record_message,
        { deleteDataFrom(context, card, onSuccess) }
    )
}
