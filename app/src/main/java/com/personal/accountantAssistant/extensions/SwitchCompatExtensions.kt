package com.personal.accountantAssistant.extensions

import androidx.annotation.StringRes
import androidx.appcompat.widget.SwitchCompat

fun SwitchCompat.setCheckedText(
    checkedText: String = textOn.toString(), uncheckedText: String = textOff.toString()
) {
    text = if (isChecked) checkedText else uncheckedText
}

fun SwitchCompat.setCheckedText(@StringRes checkedTextRes: Int, @StringRes uncheckedTextRes: Int) {
    setCheckedText(this.context.getString(checkedTextRes), this.context.getString(uncheckedTextRes))
}