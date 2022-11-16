package com.personal.accountantAssistant.providers

import com.personal.accountantAssistant.domain.models.ExpensesValuesModel
import com.personal.accountantAssistant.extensions.flowEmit
import java.math.BigDecimal
import java.util.*

object MockHomeProviders {

    fun mockedFirstDate() = Date()

    fun mockedLastDate() = Date()

    fun mockedPeriodDates() = flowEmit { Pair(mockedFirstDate(), mockedLastDate()) }

    fun mockedAvailableMoney() = flowEmit { BigDecimal.ZERO }

    fun mockedFlowExpensesValues() = flowEmit {
        ExpensesValuesModel(
            buys = BigDecimal.ZERO,
            bills = BigDecimal.ZERO,
            total = BigDecimal.ZERO,
            available = BigDecimal.ZERO
        )
    }
}