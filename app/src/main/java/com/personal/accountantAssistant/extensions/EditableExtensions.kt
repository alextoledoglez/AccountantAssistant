package com.personal.accountantAssistant.extensions

import android.text.Editable

fun Editable?.toCurrencyBigDecimal() = toString().toCurrencyBigDecimal()

fun Editable?.toDate() = toString().toDate()

fun Editable?.toInt() = toString().toInt()