package com.personal.accountantAssistant.core.extensions

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.widget.NumberPicker
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.core.AlertDialogBuilder
import com.personal.accountantAssistant.core.AlertDialogBuilder.Companion.MAX_VALUE
import com.personal.accountantAssistant.core.AlertDialogBuilder.Companion.MIN_VALUE
import com.personal.accountantAssistant.core.AlertDialogBuilder.Companion.toCurrentOrMinValue
import com.personal.accountantAssistant.utils.ActionUtils
import io.reactivex.functions.Action
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

fun AlertDialogBuilder.showConfirmationFrom(
    titleId: Int,
    messageId: Int,
    confirmAction: Action,
    cancelAction: Action = ActionUtils.NONE_ACTION_TO_DO
) = apply {
    setTitle(titleId)
    setMessage(messageId)
    setOkButtonAction(confirmAction)
    setCancelButtonAction(cancelAction)
    show()
}

fun AlertDialogBuilder.showImportOrExportFrom(
    titleId: Int, importAction: Action, exportAction: Action
) = apply {
    val importOption = 0
    val exportOption = 1
    val checkedItem = AtomicInteger(0)
    val options =
        arrayOf(context.getString(R.string.import_all), context.getString(R.string.export_all))
    setTitle(context.getString(titleId))
    setSingleChoiceItems(options, checkedItem.get())
    { _: DialogInterface?, which: Int -> checkedItem.set(which) }
    setOkButtonAction(
        when (checkedItem.get()) {
            importOption -> importAction
            exportOption -> exportAction
            else -> ActionUtils.NONE_ACTION_TO_DO
        }
    )
    setCancelButtonAction {}
    show()
}

fun AlertDialogBuilder.setupNumberPickerFrom(
    defaultValue: Int, listener: NumberPicker.OnValueChangeListener
) = apply {
    setView(
        NumberPicker(context).apply {
            minValue = MIN_VALUE
            maxValue = MAX_VALUE
            value = toCurrentOrMinValue(defaultValue)
            setOnValueChangedListener(listener)
        }
    )
    setTitle(R.string.select_quantity)
    setOkButtonAction {}
    setCancelButtonAction {}
}

fun AlertDialogBuilder.showDatePickerFrom(
    date: Date?, listener: DatePickerDialog.OnDateSetListener
) = apply {
    val calendar = Calendar.getInstance().also { it.time = date ?: Date() }
    DatePickerDialog(
        context, listener,
        calendar[Calendar.YEAR],
        calendar[Calendar.MONTH],
        calendar[Calendar.DAY_OF_MONTH]
    ).show()
}
