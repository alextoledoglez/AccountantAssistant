package com.personal.accountantAssistant.domain.useCases.bills

import com.personal.accountantAssistant.domain.models.ExpenseModel
import kotlinx.coroutines.flow.Flow

interface SaveBillUseCase {
    operator fun invoke(model: ExpenseModel): Flow<MutableList<ExpenseModel>?>
}