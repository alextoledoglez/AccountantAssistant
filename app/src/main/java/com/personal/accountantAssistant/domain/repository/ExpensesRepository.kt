package com.personal.accountantAssistant.domain.repository

import com.personal.accountantAssistant.domain.models.BillModel
import com.personal.accountantAssistant.domain.models.BuyModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import kotlinx.coroutines.flow.Flow

interface ExpensesRepository {
    fun getBuys(): Flow<BuyModel?>
    fun getBills(): Flow<BillModel?>
    fun setDefaultBuys(): Flow<Unit>
    fun setDefaultBills(): Flow<Unit>
    fun updateExpense(model: ExpenseModel): Flow<Long>
    fun deleteExpense(model: ExpenseModel): Flow<Long>
    fun deleteAllBuys(): Flow<Unit>
    fun deleteAllBills(): Flow<Unit>
}