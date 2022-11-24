package com.personal.accountantAssistant.extensions

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.widget.NumberPicker
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.bases.AlertDialogBuilder.Companion.MAX_VALUE
import com.personal.accountantAssistant.bases.AlertDialogBuilder.Companion.MIN_VALUE
import com.personal.accountantAssistant.bases.AlertDialogBuilder.Companion.toCurrentOrMinValue
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

fun AlertDialogBuilder.showConfirmationFrom(
    titleId: Int, messageId: Int, confirmAction: () -> Unit, cancelAction: () -> Unit
) = apply {
    setTitle(titleId)
    setMessage(messageId)
    setOkButtonAction(confirmAction)
    setCancelButtonAction(cancelAction)
    show()
}

fun AlertDialogBuilder.showImportOrExportFrom(
    titleId: Int, importAction: () -> Unit, exportAction: () -> Unit
) = apply {
    val importOption = 0
    val checkedItem = AtomicInteger(0)
    val options =
        arrayOf(context.getString(R.string.import_all), context.getString(R.string.export_all))
    setTitle(context.getString(titleId))
    setSingleChoiceItems(options, checkedItem.get())
    { _: DialogInterface?, which: Int -> checkedItem.set(which) }
    setOkButtonAction(if (checkedItem.get() == importOption) importAction else exportAction)
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
    date: Date?,
    listener: DatePickerDialog.OnDateSetListener
) = apply {
    date.toCalendar().let {
        DatePickerDialog(context, listener, it.getYear(), it.getMonth(), it.getDayOfMonth())
            .updateCalendarDate(it)
            .show()
    }
}
