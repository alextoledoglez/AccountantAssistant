package com.personal.accountantAssistant.providers

import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.ZERO
import com.personal.accountantAssistant.extensions.flowEmit
import java.math.BigDecimal
import java.util.*

object MockBuysProviders {

    fun mockedBuy() = ExpenseModel(
        id = Long.ZERO,
        name = "Apple",
        quantity = Int.ZERO,
        date = Date(),
        unitaryValue = BigDecimal(10),
        totalValue = BigDecimal(10),
        type = ExpensesType.BUY,
        isActive = false
    )

    fun mockedBuysFlow() = flowEmit {
        mutableListOf(
            mockedBuy().copy(id = 1),
            mockedBuy().copy(id = 2)
        )
    }

    fun mockedAllBuysActiveFlow() = flowEmit {
        mutableListOf(
            mockedBuy().copy(id = 1, isActive = true),
            mockedBuy().copy(id = 2, isActive = true)
        )
    }

    fun mockedAllBuysInactiveFlow() = flowEmit {
        mutableListOf(
            mockedBuy().copy(id = 1, isActive = false),
            mockedBuy().copy(id = 2, isActive = false)
        )
    }

    fun mockedBuysSummaryFlow() = flowEmit {
        SummaryModel(activeCount = Int.ZERO, total = BigDecimal.ZERO)
    }
}