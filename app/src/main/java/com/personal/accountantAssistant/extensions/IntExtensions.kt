package com.personal.accountantAssistant.extensions

fun Int?.orValue(value: Int) = this ?: value

fun Int?.orZero(): Int = orValue(0)

fun Int?.orOne(): Int = orValue(1)