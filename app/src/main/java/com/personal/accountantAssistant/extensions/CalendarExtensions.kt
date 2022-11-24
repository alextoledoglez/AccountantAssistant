package com.personal.accountantAssistant.extensions

import java.util.*
import java.util.Calendar.*

const val DAY_OF_MONTH_UNTIL_DATE = -3

fun Calendar.getDate() = time

fun Calendar.getStrDate(): String = time.toDateStr()

fun Calendar.getYear(): Int = this[YEAR]

fun Calendar.getMonth(): Int = this[MONTH]

fun Calendar.getDayOfMonth(): Int = this[DAY_OF_MONTH]

fun Calendar.toCurrentDateStr(): String =
    "${getDayOfMonth()}${String.DASH_SEPARATOR}${getMonth()}${String.DASH_SEPARATOR}${getYear()}"

fun Calendar.isDateBetween(after: Date?, before: Date?, inclusive: Boolean = false): Boolean {
    val date = this.time
    return if (inclusive)
        date.isMoreOrEqualToZero(after) && date.isLessOrEqualToZero(before)
    else
        date.compareTo(after).isMoreThanZero() && date.compareTo(before).isLessThanZero()
}

fun Calendar.toThreeDaysBefore(): Date = apply { add(DAY_OF_MONTH, DAY_OF_MONTH_UNTIL_DATE) }.time