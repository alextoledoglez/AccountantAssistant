package com.personal.accountantAssistant.domain.models

import java.math.BigDecimal

data class BuysModel(
    var uid: Long = 0,
    val isAllChecked: Boolean = false,
    val isAnyChecked: Boolean = false,
    val total: BigDecimal = BigDecimal.ZERO,
    val expenses: ArrayList<ExpenseModel>
)