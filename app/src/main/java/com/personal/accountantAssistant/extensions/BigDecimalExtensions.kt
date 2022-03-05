package com.personal.accountantAssistant.extensions

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.rounded(): BigDecimal = setScale(Int.DECIMAL_PLACES, RoundingMode.HALF_UP)

fun BigDecimal.orValue(value: BigDecimal) = this ?: value

fun BigDecimal?.orValue(value: Int): BigDecimal = this ?: value.toBigDecimal()

fun BigDecimal?.orValue(value: Float): BigDecimal = this ?: value.toBigDecimal()

fun BigDecimal?.orZero(): BigDecimal = orValue(0)

fun BigDecimal?.toCurrencyMaskedStr() = toString().toCurrencyMaskedStr()

fun BigDecimal?.toCurrencyBigDecimal() = toString().toCurrencyBigDecimal()