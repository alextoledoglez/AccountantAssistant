package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.domain.models.ExpenseModel
import kotlinx.coroutines.flow.Flow

interface GetBillsDueTodayUseCase {
    operator fun invoke(): Flow<List<ExpenseModel>?>
}