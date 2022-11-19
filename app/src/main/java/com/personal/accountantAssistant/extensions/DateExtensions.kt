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

fun Date?.toCalendarMillis(): Long = toCalendar().timeInMillis

fun Date?.toThreeDaysBefore(): Date = toCalendar().toThreeDaysBefore()

fun Date?.isDateBetween(firstDate: Date?, lastDate: Date?) =
    toCalendar().isDateBetween(after = firstDate, before = lastDate)

fun Date?.isDueSoon() =
    Calendar.getInstance().isDateBetween(after = toThreeDaysBefore(), before = this)

fun Date?.isDueToday(): Boolean = this?.toDateStr().equals(Calendar.getInstance().time.toDateStr())

fun Pair<Date?, Date?>.toPeriodDateStr(): String =
    "${first.toDateStr()}${String.DASH_SEPARATOR}${second.toDateStr()}"