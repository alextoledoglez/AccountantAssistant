package com.personal.accountantAssistant.providers

import com.personal.accountantAssistant.domain.models.ExpensesValuesModel
import com.personal.accountantAssistant.extensions.flowEmit
import java.math.BigDecimal
import java.util.*

object MockHomeProviders {

    fun mockedPeriodDates() = Pair(Date(), Date())

    fun mockedAvailableMoney(): BigDecimal = BigDecimal.ZERO

    fun mockedPeriodDatesFlow() = flowEmit { mockedPeriodDates() }

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