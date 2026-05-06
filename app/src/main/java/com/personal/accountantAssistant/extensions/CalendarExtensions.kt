package com.personal.accountantAssistant.extensions

import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR
import java.util.Date

const val DAY_OF_MONTH_UNTIL_DATE = -3

fun Calendar.getYear(): Int = this[YEAR]

fun Calendar.getMonth(): Int = this[MONTH]

fun Calendar.getDayOfMonth(): Int = this[DAY_OF_MONTH]

fun Calendar.toCurrentDateStr(): String =
    "${getDayOfMonth()}${String.DASH_SEPARATOR}${getMonth()}${String.DASH_SEPARATOR}${getYear()}"

fun Calendar.isDateBetween(after: Date?, before: Date?, inclusive: Boolean = false) = if (inclusive)
    time.isMoreOrEqualToZero(after) && time.isLessOrEqualToZero(before)
else
    time.compareTo(after).isMoreThanZero() && time.compareTo(before).isLessThanZero()

fun Calendar.toThreeDaysBefore(): Date = apply { add(DAY_OF_MONTH, DAY_OF_MONTH_UNTIL_DATE) }.time

fun Calendar.toNextMonthCalendar() = apply { add(MONTH, Int.ONE) }

fun Calendar.toPrevMonthCalendar() = apply { add(MONTH, -Int.ONE) }

fun Calendar.dateOf(year: Int, month: Int, day: Int) = apply { set(year, month - 1, day) }.time

fun Calendar.getNextMonthDate() = run {
    add(MONTH, Int.ONE)
    time
}