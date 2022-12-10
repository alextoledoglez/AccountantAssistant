package com.personal.accountantAssistant.domain.useCases.buys

import com.personal.accountantAssistant.domain.models.ExpenseModel
import kotlinx.coroutines.flow.Flow

interface GetBuysUseCase {
    operator fun invoke(): Flow<MutableList<ExpenseModel>?>
}