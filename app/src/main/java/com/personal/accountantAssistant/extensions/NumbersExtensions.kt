package com.personal.accountantAssistant.extensions

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

val Int.Companion.DEFAULT_QUANTITY_VALUE: Int get() = String.STR_DEFAULT_QUANTITY_VALUE.toInt()

val Int.Companion.STORAGE_PERMISSION_CODE: Int get() = 2

val Int.Companion.DECIMAL_PLACES: Int get() = 2

val Int.Companion.ZERO: Int get() = 0

val Int.Companion.ONE: Int get() = 1

val Int.Companion.DEFAULT_UID: Int get() = Int.ZERO

val Int.Companion.DEFAULT_CALENDAR_ID: Int get() = Int.ONE

val Int.Companion.FIRST_SHEET: Int get() = Int.ZERO

val Int.Companion.HEADER_ROW: Int get() = Int.ZERO

val Int.Companion.BODY_ROW: Int get() = HEADER_ROW + Int.ONE

val Int.Companion.TITLE_POINT_SIZE: Int get() = 16

fun Int?.orValue(value: Int) = this ?: value

fun Int?.orZero(): Int = orValue(Int.ZERO)

fun Int?.isMoreThanZero() = orZero() > Int.ZERO

fun Int?.isLessThanZero() = orZero() < Int.ZERO

fun Int?.isEqualToZero() = orZero() == Int.ZERO

val Long.Companion.ZERO: Long get() = 0

fun Long?.orValue(value: Long) = this ?: value

fun Long?.orZero(): Long = orValue(Long.ZERO)

fun Long.toEntityId() = if (this > Int.DEFAULT_UID) this.toInt() else null

fun Long?.toCalendar(): Calendar? {
    val calendar = Calendar.getInstance()
    this?.let { calendar.timeInMillis = it }
    return calendar
}

fun Long?.toUtcTime() = this?.plus(kotlin.math.abs(TimeZone.getDefault().getOffset(this)))

fun Long?.toUtcDate() = toUtcTime().toCalendar()?.time

fun Float?.orValue(value: Int): Float = this ?: value.toFloat()

fun Float?.orZero(): Float = orValue(Int.ZERO)

val Double.Companion.DEFAULT_VALUE: Double get() = String.STR_DEFAULT_MONETARY_VALUE.toDouble()

fun Double?.orValue(value: Int): Double = this ?: value.toDouble()

fun Double?.orZero(): Double = orValue(Int.ZERO)

fun BigDecimal.rounded(): BigDecimal = setScale(Int.DECIMAL_PLACES, RoundingMode.HALF_UP)

fun BigDecimal?.orValue(value: Int): BigDecimal = this ?: value.toBigDecimal()

fun BigDecimal?.orZero(): BigDecimal = orValue(Int.ZERO)

fun BigDecimal?.toCurrencyMaskedStr() = toString().toCurrencyMaskedStr()

fun BigDecimal?.isMoreThanZero() = this.orZero() > BigDecimal.ZERO

fun BigDecimal?.isMoreThan(value: BigDecimal?) = (value.orZero() <= this.orZero())

fun BigDecimal?.isLessThan(value: BigDecimal?) = !isMoreThan(value)