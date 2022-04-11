package com.personal.accountantAssistant.extensions

val Long.Companion.ZERO: Long get() = 0

val Long.Companion.ONE: Long get() = 1

val Long.Companion.DEFAULT_UID: Long get() = Long.ZERO

fun Long?.orValue(value: Long) = this ?: value

fun Long?.orZero(): Long = orValue(Long.ZERO)

fun Long?.orOne(): Long = orValue(Long.ONE)

fun Long.toEntityId() = if (this > Int.DEFAULT_UID) this.toInt() else null