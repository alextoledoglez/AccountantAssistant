package com.personal.accountantAssistant.extensions

fun Float?.orValue(value: Float) = this ?: value

fun Float?.orValue(value: Int): Float = this ?: value.toFloat()

fun Float?.orZero(): Float = orValue(0)