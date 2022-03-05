package com.personal.accountantAssistant.extensions

import android.text.Editable
import com.personal.accountantAssistant.utils.DateUtils

fun Editable?.toCurrencyBigDecimal() = toString().toCurrencyBigDecimal()

fun Editable?.toDate() = DateUtils.toDate(toString())

fun Editable?.toInt() = toString().toInt()