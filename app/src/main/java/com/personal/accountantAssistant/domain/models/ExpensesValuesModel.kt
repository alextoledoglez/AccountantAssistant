package com.personal.accountantAssistant.domain.models

import java.math.BigDecimal

data class ExpensesValuesModel(
    val buys: BigDecimal?,
    val bills: BigDecimal?,
    val total: BigDecimal?
)
