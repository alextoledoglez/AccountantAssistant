package com.personal.accountantAssistant.domain.useCases.home

import com.personal.accountantAssistant.domain.models.ExpensesValuesModel
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.*

interface GetExpensesUseCase {
    operator fun invoke(
        period: Pair<Date?, Date?>, available: BigDecimal?
    ): Flow<ExpensesValuesModel>
}