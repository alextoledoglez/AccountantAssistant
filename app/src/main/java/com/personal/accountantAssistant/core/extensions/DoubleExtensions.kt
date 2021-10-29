package com.personal.accountantAssistant.core.extensions

fun Double?.orValue(value: Double) = this ?: value

fun Double?.orValue(value: Int): Double = this ?: value.toDouble()

fun Double?.orValue(value: Float): Double = this ?: value.toDouble()

fun Double?.orZero(): Double = orValue(0)