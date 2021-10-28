package com.personal.accountantAssistant.core.extensions

fun Float?.orValue(value: Float) = this ?: value

fun Float?.orZero() = orValue(0F)