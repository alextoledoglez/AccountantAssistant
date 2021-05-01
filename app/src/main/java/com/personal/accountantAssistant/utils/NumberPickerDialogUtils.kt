package com.personal.accountantAssistant.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import android.widget.NumberPicker
import com.personal.accountantAssistant.R

object NumberPickerDialogUtils {
    private const val MIN_VALUE = 1
    private const val MAX_VALUE = 100

    @JvmStatic
    fun initializeFrom(context: Context?,
                       editText: EditText,
                       defaultValue: Int) {
        setText(editText, defaultValue)
        editText.setOnClickListener { showNumberPickerDialogFrom(context, editText, defaultValue) }
        editText.onFocusChangeListener = OnFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (hasFocus) {
                showNumberPickerDialogFrom(context, editText, defaultValue)
            }
        }
    }

    private fun setText(editText: EditText, texValue: Int) {
        editText.setText(if (texValue == 0) MIN_VALUE.toString() else texValue.toString())
    }

    private fun showNumberPickerDialogFrom(context: Context?,
                                           editText: EditText,
                                           defaultValue: Int) {
        val numberPicker = NumberPicker(context)
        numberPicker.minValue = MIN_VALUE
        numberPicker.maxValue = MAX_VALUE
        numberPicker.value = if (defaultValue == 0) MIN_VALUE else defaultValue
        numberPicker.setOnValueChangedListener { _: NumberPicker?, _: Int, newValue: Int -> setText(editText, newValue) }
        AlertDialog.Builder(context)
                .setView(numberPicker)
                .setTitle(R.string.select_quantity)
                .setPositiveButton(R.string.ok) { _: DialogInterface?, _: Int -> }
                .setNegativeButton(R.string.cancel) { _: DialogInterface?, _: Int -> }
                .show()
    }
}