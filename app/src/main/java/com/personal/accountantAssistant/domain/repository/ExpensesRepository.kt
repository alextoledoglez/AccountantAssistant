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
    fun setDefaultBuys(): Flow<MutableList<ExpenseModel>?>
    fun setDefaultBills(): Flow<MutableList<ExpenseModel>?>
    fun setAllBuysActive(isActive: Boolean): Flow<MutableList<ExpenseModel>?>
    fun setAllBillsActive(isActive: Boolean): Flow<MutableList<ExpenseModel>?>
    fun switchActiveExpense(model: ExpenseModel): Flow<MutableList<ExpenseModel>?>
    fun deleteExpense(model: ExpenseModel): Flow<Long>
    fun deleteAllBuys(): Flow<MutableList<ExpenseModel>?>
    fun deleteAllBills(): Flow<MutableList<ExpenseModel>?>
}