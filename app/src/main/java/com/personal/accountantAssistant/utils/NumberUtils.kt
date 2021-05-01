package com.personal.accountantAssistant.utils

import java.math.BigDecimal
import java.math.RoundingMode

object NumberUtils {
    private const val ZERO_STR = "0"
    private const val ONE_STR = "1"
    private const val DECIMAL_PLACES = 2
    fun toBoolean(numericString: String): Boolean {
        var result = java.lang.Boolean.FALSE
        if (numericString == ZERO_STR) {
            result = java.lang.Boolean.FALSE
        } else if (numericString == ONE_STR) {
            result = java.lang.Boolean.TRUE
        }
        return result
    }

    @JvmStatic
    fun roundTo(value: Double?): Double {
        return roundTo(BigDecimal(value.toString())).toDouble()
    }

    fun roundTo(value: Float): Float {
        return roundTo(BigDecimal(value.toString())).toFloat()
    }

    private fun roundTo(value: BigDecimal): BigDecimal {
        return value.setScale(DECIMAL_PLACES, RoundingMode.HALF_UP)
    }
}