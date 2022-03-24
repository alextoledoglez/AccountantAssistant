package com.personal.accountantAssistant.domain.repository

import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.ExpenseModel
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.*

interface ExpensesRepository {
    fun getBuys(): Flow<MutableList<ExpenseModel>?>
    fun getBills(): Flow<MutableList<ExpenseModel>?>
    fun getTotalPriceUntil(type: ExpensesType, lastPeriodDate: Date?): Flow<BigDecimal?>
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