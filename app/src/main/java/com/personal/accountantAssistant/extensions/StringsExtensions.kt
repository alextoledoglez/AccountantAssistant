package com.personal.accountantAssistant.extensions

import java.text.DecimalFormatSymbols
import java.text.NumberFormat

import java.util.*

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

val String.Companion.STR_DECIMAL_SEPARATOR: String
    get() = DecimalFormatSymbols.getInstance().decimalSeparator.toString()

fun String.toRoundedBigDecimal() = this.trim().toBigDecimal().rounded()

fun String.isNotEmptyAndLengthEqualTo(value: Int) = (isNotEmpty() && length == value)

fun String.toCurrencyBigDecimal() = if (String.STR_DECIMAL_SEPARATOR == String.COMMA) {
    this.replace(String.DOT, String.EMPTY).replace(String.COMMA, String.DOT)
} else {
    this.replace(String.COMMA, String.EMPTY)
}.toRoundedBigDecimal()

fun String.toCurrencyMaskedStr(): String {
    val isDecimals = true
    val currency = String.EMPTY
    val unmaskedText = this
        .replace("[$,.]".toRegex(), String.EMPTY)
        .replace("\\s+".toRegex(), String.EMPTY)
    val result = if (unmaskedText.isNotEmpty()) {
        val format = try {
            if (isDecimals) {
                val number: Double = (unmaskedText.toDouble() / 100)
                NumberFormat.getCurrencyInstance().let {
                    it.format(number).replace(it.currency?.symbol.orEmpty(), currency)
                }
            } else {
                val locale = Locale.getDefault()
                val parsed = unmaskedText.toInt()
                "$currency${NumberFormat.getNumberInstance(locale).format(parsed.toLong())}"
            }
        } catch (e: NumberFormatException) {
            null
        }
        if (String.STR_DECIMAL_SEPARATOR != String.COMMA && !isDecimals)
            format?.replace(String.COMMA.toRegex(), String.STR_DECIMAL_SEPARATOR)
        else
            format
    } else this
    return result ?: this
}