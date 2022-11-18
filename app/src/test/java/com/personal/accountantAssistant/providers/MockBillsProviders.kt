package com.personal.accountantAssistant.providers

import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.ZERO
import com.personal.accountantAssistant.extensions.flowEmit
import java.math.BigDecimal
import java.util.*

object MockBillsProviders {

    fun mockedBill() = ExpenseModel(
        id = Long.ZERO,
        name = "Electricity",
        quantity = Int.ZERO,
        date = Date(),
        unitaryValue = BigDecimal(500),
        totalValue = BigDecimal(500),
        type = ExpensesType.BILL,
        isActive = false
    )

    fun mockedBillsFlow() = flowEmit {
        mutableListOf(
            mockedBill().copy(id = 1),
            mockedBill().copy(id = 2)
        )
    }


    fun mockedAllBillsActiveFlow() = flowEmit {
        mutableListOf(
            mockedBill().copy(id = 1, isActive = true),
            mockedBill().copy(id = 2, isActive = true)
        )
    }

    fun mockedAllBillsInactiveFlow() = flowEmit {
        mutableListOf(
            mockedBill().copy(id = 1, isActive = false),
            mockedBill().copy(id = 2, isActive = false)
        )
    }

    fun mockedBillsSummaryFlow() = flowEmit {
        SummaryModel(activeCount = Int.ZERO, total = BigDecimal.ZERO)
    }
}