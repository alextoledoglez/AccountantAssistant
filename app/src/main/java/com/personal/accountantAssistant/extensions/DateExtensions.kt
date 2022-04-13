package com.personal.accountantAssistant.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date?.orCurrent() = this ?: Date()

fun Date?.toDateStr(): String {
    var strDate = orCurrent().toString()
    val dateFormat = SimpleDateFormat(String.DD_MM_YYYY, Locale.getDefault())
    try {
        strDate = dateFormat.format(orCurrent())
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return strDate
}

fun Date?.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    this?.let { calendar.time = it }
    return calendar
}

fun Date?.toCalendarMillis(): Long {
    return this.toCalendar().timeInMillis
}