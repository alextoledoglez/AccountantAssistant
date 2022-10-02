package com.personal.accountantAssistant.domain.models

import com.personal.accountantAssistant.extensions.isLessThan
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.rounded
import java.math.BigDecimal

data class ExpensesValuesModel(
    val buys: BigDecimal?,
    val bills: BigDecimal?,
    val total: BigDecimal?,
    val available: BigDecimal?,
    val balance: BigDecimal? = available.orZero().minus(total.orZero().rounded()).rounded(),
    val isTotalLessThanAvailable: Boolean = total.orZero().rounded().isLessThan(available.orZero())
)
