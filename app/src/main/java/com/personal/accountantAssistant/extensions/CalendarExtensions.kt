package com.personal.accountantAssistant.extensions

import java.util.*

fun Calendar.updateWith(calendar: Calendar) = this.apply {
    set(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.HOUR),
        calendar.get(Calendar.MINUTE),
        calendar.get(Calendar.SECOND)
    )
}

fun Calendar.addFieldValue(field: Int, value: Int) = this.apply { add(field, value) }