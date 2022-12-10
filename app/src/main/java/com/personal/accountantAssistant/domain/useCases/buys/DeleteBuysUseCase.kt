package com.personal.accountantAssistant.domain.useCases.buys

import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.UserModel
import kotlinx.coroutines.flow.Flow

interface DeleteBuysUseCase {
    fun deleteBuy(model: ExpenseModel): Flow<MutableList<ExpenseModel>?>
    fun deleteAllBuys(): Flow<MutableList<ExpenseModel>?>
}