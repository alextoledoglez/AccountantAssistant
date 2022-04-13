package com.personal.accountantAssistant.domain.models

import com.personal.accountantAssistant.extensions.ZERO
import com.personal.accountantAssistant.extensions.isMoreThanZero
import com.personal.accountantAssistant.extensions.orZero
import java.math.BigDecimal

data class SummaryModel(
    val activeCount: Int = Int.ZERO,
    val total: BigDecimal = BigDecimal.ZERO
) {
    fun isActiveCountEqualTo(itemsCount: Int) = isAnyActive() && activeCount.orZero() == itemsCount
    fun isAnyActive() = activeCount.isMoreThanZero()
}