package com.personal.accountantAssistant.extensions

val Int.Companion.DEFAULT_QUANTITY_VALUE: Int get() = String.STR_DEFAULT_QUANTITY_VALUE.toInt()

val Int.Companion.ZERO: Int get() = 0

val Int.Companion.ONE: Int get() = 1

val Int.Companion.DEFAULT_UID: Int get() = Int.ZERO

val Int.Companion.DECIMAL_PLACES: Int get() = 2

fun Int?.orValue(value: Int) = this ?: value

fun Int?.orZero(): Int = orValue(Int.ZERO)

fun Int?.orOne(): Int = orValue(Int.ONE)

fun Int?.isMoreThanZero() = orZero() > Int.ZERO