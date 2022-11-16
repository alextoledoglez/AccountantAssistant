package com.personal.accountantAssistant.providers

import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.ZERO
import com.personal.accountantAssistant.extensions.flowEmit
import java.math.BigDecimal
import java.util.*

object MockBuysProviders {

    fun mockedFlowBuys() = flowEmit {
        mutableListOf(
            ExpenseModel(
                id = Long.ZERO,
                name = "Apple",
                quantity = Int.ZERO,
                date = Date(),
                unitaryValue = BigDecimal(10),
                totalValue = BigDecimal(10),
                type = ExpensesType.BUY,
                isActive = false
            )
        )
    }
}