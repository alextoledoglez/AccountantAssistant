package com.personal.accountantAssistant.domain.useCases.buys

import com.personal.accountantAssistant.domain.models.ExpenseModel
import kotlinx.coroutines.flow.Flow

interface ActiveBuysUseCase {
    fun switchActiveBuy(model: ExpenseModel): Flow<MutableList<ExpenseModel>?>
    fun setAllBuysActive(isActive: Boolean): Flow<MutableList<ExpenseModel>?>
}