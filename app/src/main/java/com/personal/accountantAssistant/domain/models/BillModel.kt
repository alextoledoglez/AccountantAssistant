package com.personal.accountantAssistant.domain.models

import java.math.BigDecimal

data class BillModel(
    var uid: Long = 0,
    val isAllChecked: Boolean = false,
    val isAnyChecked: Boolean = false,
    val total: BigDecimal = BigDecimal.ZERO,
    val expenses: List<ExpenseModel>
)