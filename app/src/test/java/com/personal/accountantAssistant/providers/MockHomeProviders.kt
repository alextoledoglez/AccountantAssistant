package com.personal.accountantAssistant.providers

import com.personal.accountantAssistant.domain.models.ExpensesValuesModel
import com.personal.accountantAssistant.extensions.flowEmit
import java.math.BigDecimal
import java.util.*

object MockHomeProviders {

    fun mockedLastDate() = Date()

    fun mockedAvailableMoney(): BigDecimal = BigDecimal.ZERO

    fun mockedPeriodDates() = flowEmit { Pair(Date(), mockedLastDate()) }

    fun mockedFlowAvailableMoney() = flowEmit { mockedAvailableMoney() }

    fun mockedFlowExpensesValues() = flowEmit {
        ExpensesValuesModel(
            buys = BigDecimal.ZERO,
            bills = BigDecimal.ZERO,
            total = BigDecimal.ZERO,
            available = BigDecimal.ZERO
        )
    }
}