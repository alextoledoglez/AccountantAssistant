package com.personal.accountantAssistant.domain.useCases.bills

import com.personal.accountantAssistant.domain.models.ExpenseModel
import kotlinx.coroutines.flow.Flow

interface DeleteBillsUseCase {
    fun deleteBill(model: ExpenseModel): Flow<MutableList<ExpenseModel>?>
    fun deleteAllBills(): Flow<MutableList<ExpenseModel>?>
}