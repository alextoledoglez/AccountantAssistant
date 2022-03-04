package com.personal.accountantAssistant.extensions

val Boolean.Companion.DEFAULT_ACTIVE_STATUS: Boolean get() = false

fun Boolean?.orFalse() = this ?: false