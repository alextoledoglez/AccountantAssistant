package com.personal.accountantAssistant.domain.useCases.bills

import com.personal.accountantAssistant.domain.models.ExpenseModel
import kotlinx.coroutines.flow.Flow

interface GetBillsUseCase {
    operator fun invoke(): Flow<MutableList<ExpenseModel>?>
}