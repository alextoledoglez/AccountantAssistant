package com.personal.accountantAssistant.extensions


val String.Companion.EMPTY: String get() = ""

fun String.toValue(value: String) = if (this.isNotEmpty())
    this
else
    value