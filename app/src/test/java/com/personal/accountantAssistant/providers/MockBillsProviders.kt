package com.personal.accountantAssistant.providers

import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.ZERO
import com.personal.accountantAssistant.extensions.flowEmit
import java.math.BigDecimal
import java.util.*

object MockBillsProviders {

    fun mockedFlowBills() = flowEmit {
        mutableListOf(
            ExpenseModel(
                id = Long.ZERO,
                name = "Electricity",
                quantity = Int.ZERO,
                date = Date(),
                unitaryValue = BigDecimal(500),
                totalValue = BigDecimal(500),
                type = ExpensesType.BILL,
                isActive = false
            )
        )
    }
}