package com.personal.accountantAssistant.domain.useCases.bills

import com.personal.accountantAssistant.domain.models.ExpenseModel
import kotlinx.coroutines.flow.Flow

interface ActiveBillsUseCase {
    fun switchActiveBill(model: ExpenseModel): Flow<MutableList<ExpenseModel>?>
    fun setAllBillsActive(isActive: Boolean): Flow<MutableList<ExpenseModel>?>
}