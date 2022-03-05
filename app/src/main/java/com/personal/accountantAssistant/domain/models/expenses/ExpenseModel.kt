package com.personal.accountantAssistant.domain.models.expenses

import com.personal.accountantAssistant.data.enums.expenses.ExpensesType
import java.math.BigDecimal
import java.util.*

data class ExpenseModel(
    var id: Int,
    var name: String? = null,
    var quantity: Int,
    var date: Date? = null,
    var unitaryValue: BigDecimal,
    var totalValue: BigDecimal,
    var type: ExpensesType? = null,
    var isActive: Boolean
)
