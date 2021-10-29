package com.personal.accountantAssistant.utils

import com.personal.accountantAssistant.core.extensions.orZero
import java.math.BigDecimal
import java.math.RoundingMode

object NumberUtils {
    private const val ZERO_STR = "0"
    private const val ONE_STR = "1"
    private const val DECIMAL_PLACES = 2
    fun toBoolean(numericString: String?): Boolean {
        var result = false
        if (numericString == ZERO_STR) {
            result = false
        } else if (numericString == ONE_STR) {
            result = true
        }
        return result
    }

    @JvmStatic
    fun roundTo(value: Double?): Double {
        return roundTo(BigDecimal(value.orZero().toString())).toDouble()
    }

    fun roundTo(value: Float?): Float {
        return roundTo(BigDecimal(value.orZero().toString())).toFloat()
    }

    private fun roundTo(value: BigDecimal): BigDecimal {
        return value.setScale(DECIMAL_PLACES, RoundingMode.HALF_UP)
    }
}