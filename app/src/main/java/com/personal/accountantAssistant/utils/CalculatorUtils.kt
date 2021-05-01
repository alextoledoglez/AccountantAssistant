package com.personal.accountantAssistant.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import java.util.function.BinaryOperator

object CalculatorUtils {
    fun getPercentRespectiveTo(xValue: BigDecimal, totalValue: BigDecimal?): BigDecimal {
        return xValue
                .multiply(BigDecimal(100))
                .divide(totalValue, RoundingMode.HALF_UP)
    }

    fun getMitDifferenceFromHigherOf(value1: BigDecimal, value2: BigDecimal): BigDecimal {
        val TWO = BigDecimal(2)
        val HALF_UP = BigDecimal.ROUND_HALF_UP
        val compare = value1.compareTo(value2)
        return if (compare >= 0) value2.add(value1.subtract(value2).divide(TWO, HALF_UP)) else value1.add(value2.subtract(value1).divide(TWO, HALF_UP))
    }

    fun getDifferenceBetween(value1: BigDecimal, value2: BigDecimal): BigDecimal {
        val compare = value1.compareTo(value2)
        return if (compare >= 0) value1.subtract(value2) else value2.subtract(value1)
    }

    fun getNotZeroValue(value: BigDecimal): BigDecimal {
        return if (value == BigDecimal.ZERO) BigDecimal.ONE else value
    }

    private fun withoutNullValue(n: Int): Int {
        return Optional.ofNullable(n).orElse(0)
    }

    private fun withoutNullValue(n: Float): Float {
        return Optional.ofNullable(n).orElse(00.00f)
    }

    private fun withoutNullValue(n: Double): Double {
        return Optional.ofNullable(n).orElse(00.00)
    }

    fun withoutNullValue(n: BigDecimal?): BigDecimal {
        return Optional.ofNullable(n).orElse(BigDecimal.ZERO)
    }

    private fun sum(a: Int,
                    b: Int): Int {
        return a + b
    }

    private fun sum(a: Float,
                    b: Float): Float {
        return a + b
    }

    private fun sum(a: Double,
                    b: Double): Double {
        return a + b
    }

    var accumulatedSum = BinaryOperator { a: Int, b: Int -> sum(withoutNullValue(a), withoutNullValue(b)) }
    var accumulatedDecimalSum = BinaryOperator { obj: BigDecimal, augend: BigDecimal? -> obj.add(augend) }
    @JvmField
    var accumulatedDoubleSum = BinaryOperator { a: Double, b: Double -> sum(withoutNullValue(a), withoutNullValue(b)) }
}