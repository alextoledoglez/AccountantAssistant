package com.personal.accountantAssistant.extensions

import java.util.*
import java.util.Calendar.DAY_OF_MONTH

const val DAY_OF_MONTH_UNTIL_DATE = -3

fun Calendar.isDateBetween(after: Date?, before: Date?): Boolean =
    this.time.compareTo(after).isMoreThanZero() && this.time.compareTo(before).isLessThanZero()

fun Calendar.toThreeDaysBefore(): Date = apply { add(DAY_OF_MONTH, DAY_OF_MONTH_UNTIL_DATE) }.time