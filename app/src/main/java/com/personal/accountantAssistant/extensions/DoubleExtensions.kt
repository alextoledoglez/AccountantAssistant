package com.personal.accountantAssistant.extensions

val Double.Companion.DEFAULT_VALUE: Double get() = String.STR_DEFAULT_MONETARY_VALUE.toDouble()

fun Double?.orValue(value: Double) = this ?: value

fun Double?.orValue(value: Int): Double = this ?: value.toDouble()

fun Double?.orValue(value: Float): Double = this ?: value.toDouble()

fun Double?.orZero(): Double = orValue(0)