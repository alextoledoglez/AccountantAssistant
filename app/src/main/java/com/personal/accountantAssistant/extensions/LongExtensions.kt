package com.personal.accountantAssistant.extensions

val Long.Companion.DEFAULT_UID: Long get() = 0

fun Long?.orValue(value: Long) = this ?: value

fun Long?.orZero(): Long = orValue(0)

fun Long?.orOne(): Long = orValue(1)