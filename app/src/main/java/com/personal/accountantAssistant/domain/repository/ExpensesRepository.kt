package com.personal.accountantAssistant.domain.repository

import com.personal.accountantAssistant.domain.models.BillsModel
import com.personal.accountantAssistant.domain.models.BuysModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import kotlinx.coroutines.flow.Flow

interface ExpensesRepository {
    fun getBuys(): Flow<BuysModel?>
    fun getBills(): Flow<BillsModel?>
    fun saveExpense(model: ExpenseModel): Flow<Unit>
    fun setDefaultBuys(): Flow<Unit>
    fun setDefaultBills(): Flow<Unit>
    fun setAllBuysActive(isActive: Boolean): Flow<Unit>
    fun setAllBillsActive(isActive: Boolean): Flow<Unit>
    fun updateExpense(model: ExpenseModel): Flow<Long>
    fun deleteExpense(model: ExpenseModel): Flow<Long>
    fun deleteAllBuys(): Flow<Unit>
    fun deleteAllBills(): Flow<Unit>
}