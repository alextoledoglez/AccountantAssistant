package com.personal.accountantAssistant.utils

import java.util.*

object Constants {
    const val UID = "ID"
    const val TIMES = "x"
    const val UNITY = "u"
    const val EMPTY_STR = ""
    const val ENTITY = "ENTITY"
    const val DASH_SEPARATOR = "-"
    const val EQUAL_OPERATOR = " = "
    const val BAR_CODE_KEY = "barcode"
    const val MULTIPLY_OPERATOR = " * "
    private const val STR_DEFAULT_QUANTITY_VALUE = "00"
    const val STR_DEFAULT_MONETARY_VALUE = "00.00"
    private const val STR_DEFAULT_VALUE = STR_DEFAULT_MONETARY_VALUE
    const val DEFAULT_UID = 0
    const val DETAIL_REQUEST_CODE = 123

    @JvmField
    val DEFAULT_DATE_VALUE = Date()

    const val DEFAULT_ACTIVE_STATUS: Boolean = false

    @JvmField
    val DEFAULT_VALUE = STR_DEFAULT_VALUE.toDouble()

    @JvmField
    val DEFAULT_QUANTITY_VALUE = STR_DEFAULT_QUANTITY_VALUE.toInt()
}