package com.personal.accountantAssistant.core.extensions

import android.content.DialogInterface
import android.widget.EditText
import android.widget.NumberPicker
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.core.AlertDialogBuilder
import com.personal.accountantAssistant.core.AlertDialogBuilder.Companion.MAX_VALUE
import com.personal.accountantAssistant.core.AlertDialogBuilder.Companion.MIN_VALUE
import com.personal.accountantAssistant.core.AlertDialogBuilder.Companion.toCurrentOrMinTextValue
import com.personal.accountantAssistant.utils.ActionUtils
import io.reactivex.functions.Action
import java.util.concurrent.atomic.AtomicInteger

fun AlertDialogBuilder.confirmationDialog(
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

fun AlertDialogBuilder.showImportExportDialog(
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

fun AlertDialogBuilder.showNumberPickerDialogFrom(
    editText: EditText, defaultValue: Int
): AlertDialogBuilder = apply {
    setView(
        NumberPicker(context).apply {
            minValue = MIN_VALUE
            maxValue = MAX_VALUE
            value = if (defaultValue == 0) MIN_VALUE else defaultValue
            setOnValueChangedListener { _: NumberPicker?, _: Int, newValue: Int ->
                editText.setText(toCurrentOrMinTextValue(newValue))
            }
        }
    )
    setTitle(R.string.select_quantity)
    setOkButtonAction() {}
    setCancelButtonAction() {}
    show()
}
