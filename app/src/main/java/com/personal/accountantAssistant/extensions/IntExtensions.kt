package com.personal.accountantAssistant.extensions

val Int.Companion.DEFAULT_QUANTITY_VALUE: Int get() = String.STR_DEFAULT_QUANTITY_VALUE.toInt()

val Int.Companion.DEFAULT_UID: Int get() = 0

val Int.Companion.DETAIL_REQUEST_CODE: Int get() = 123

fun Int?.orValue(value: Int) = this ?: value

fun Int?.orZero(): Int = orValue(0)

fun Int?.orOne(): Int = orValue(1)