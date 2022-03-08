package com.personal.accountantAssistant.extensions

import java.math.BigDecimal
import java.math.RoundingMode

val Double.Companion.DEFAULT_VALUE: Double get() = String.STR_DEFAULT_MONETARY_VALUE.toDouble()

fun Double.rounded(): Double =
    BigDecimal(this).setScale(Int.DECIMAL_PLACES, RoundingMode.HALF_UP).toDouble()

fun Double.orValue(value: BigDecimal) = this ?: value

fun Double?.orValue(value: Int): Double = this ?: value.toDouble()

fun Double?.orValue(value: Float): Double = this ?: value.toDouble()

fun Double?.orZero(): Double = orValue(0)

fun Double?.toCurrencyMaskedStr() = toString().toCurrencyMaskedStr()

fun Double?.toCurrencyBigDecimal() = toString().toCurrencyBigDecimal()