package com.personal.accountantAssistant.utils

import com.personal.accountantAssistant.extensions.orZero
import java.math.BigDecimal
import java.math.RoundingMode
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
        return if (compare >= 0) value2.add(
            value1.subtract(value2).divide(TWO, HALF_UP)
        ) else value1.add(value2.subtract(value1).divide(TWO, HALF_UP))
    }

    fun getDifferenceBetween(value1: BigDecimal, value2: BigDecimal): BigDecimal {
        val compare = value1.compareTo(value2)
        return if (compare >= 0) value1.subtract(value2) else value2.subtract(value1)
    }

    fun getNotZeroValue(value: BigDecimal): BigDecimal {
        return if (value == BigDecimal.ZERO) BigDecimal.ONE else value
    }

    private fun sum(a: Int?, b: Int?): Int {
        return a.orZero() + b.orZero()
    }

    private fun sum(a: Float?, b: Float?): Float {
        return a.orZero() + b.orZero()
    }

    private fun sum(a: Double?, b: Double?): Double {
        return a.orZero() + b.orZero()
    }

    var accumulatedSum =
        BinaryOperator { a: Int?, b: Int? -> sum(a.orZero(), b.orZero()) }

    var accumulatedDecimalSum =
        BinaryOperator { a: BigDecimal?, b: BigDecimal? -> a?.add(b) }

    @JvmField
    var accumulatedDoubleSum = BinaryOperator { a: Double?, b: Double? ->
        sum(a.orZero(), b.orZero())
    }
}