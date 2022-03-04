package com.personal.accountantAssistant.extensions

val String.Companion.EMPTY: String get() = ""

val String.Companion.SPACE: String get() = " "

val String.Companion.DOT: String get() = "."

val String.Companion.COMMA: String get() = ","

val String.Companion.BAR: String get() = "/"

val String.Companion.HASHTAG: String get() = "#"

val String.Companion.UID: String get() = "ID"

val String.Companion.TIMES: String get() = "x"

val String.Companion.UNITY: String get() = "u"

val String.Companion.ENTITY: String get() = "ENTITY"

val String.Companion.DASH_SEPARATOR: String get() = "-"

val String.Companion.EQUAL_OPERATOR: String get() = " = "

val String.Companion.BAR_CODE_KEY: String get() = "barcode"

val String.Companion.MULTIPLY_OPERATOR: String get() = " * "

val String.Companion.STR_DEFAULT_QUANTITY_VALUE: String get() = "00"

val String.Companion.STR_DEFAULT_MONETARY_VALUE: String get() = "00"

fun String.toValue(value: String) = this.ifEmpty { value }

fun String.isNotEmptyAndLengthEqualTo(value: Int) = (isNotEmpty() && length == value)