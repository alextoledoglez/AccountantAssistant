package com.personal.accountantAssistant.domain.useCases.buys

import com.personal.accountantAssistant.domain.models.ExpenseModel
import kotlinx.coroutines.flow.Flow

interface SaveBuyUseCase {
    operator fun invoke(model: ExpenseModel): Flow<MutableList<ExpenseModel>?>
}