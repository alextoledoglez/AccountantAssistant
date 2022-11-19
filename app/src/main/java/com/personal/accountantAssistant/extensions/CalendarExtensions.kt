package com.personal.accountantAssistant.extensions

import java.util.*
import java.util.Calendar.DAY_OF_MONTH

const val DAY_OF_MONTH_UNTIL_DATE = -3

fun Calendar.isDateBetween(after: Date?, before: Date?, inclusive: Boolean = false): Boolean {
    val date = this.time
    return if (inclusive)
        date.isMoreOrEqualToZero(after) && date.isLessOrEqualToZero(before)
    else
        date.compareTo(after).isMoreThanZero() && date.compareTo(before).isLessThanZero()
}

fun Calendar.toThreeDaysBefore(): Date = apply { add(DAY_OF_MONTH, DAY_OF_MONTH_UNTIL_DATE) }.time