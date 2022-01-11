package com.personal.accountantAssistant.extensions

fun Int?.orValue(value: Int) = this ?: value

fun Int?.orZero(): Int = orValue(0)