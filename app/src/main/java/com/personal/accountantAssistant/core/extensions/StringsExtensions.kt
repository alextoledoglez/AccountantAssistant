package com.personal.accountantAssistant.core.extensions


val String.Companion.EMPTY: String get() = ""

fun String.toValue(value: String) = if (this.isNotEmpty())
    this
else
    value