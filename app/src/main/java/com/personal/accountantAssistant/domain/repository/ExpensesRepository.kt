package com.personal.accountantAssistant.domain.repository

import com.personal.accountantAssistant.domain.models.ExpensesValuesModel
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.*

interface ExpensesRepository {
    fun getExpenses(period: Pair<Date?, Date?>, availableMoney: BigDecimal?): Flow<ExpensesValuesModel>
}