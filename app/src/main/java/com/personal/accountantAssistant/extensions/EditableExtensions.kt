package com.personal.accountantAssistant.extensions

import android.text.Editable

fun Editable?.toCurrencyBigDecimal() = toString().toCurrencyBigDecimal()