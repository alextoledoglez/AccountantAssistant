package com.personal.accountantAssistant.domain.repository

import com.personal.accountantAssistant.domain.models.ExpenseModel
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.*

interface BuysRepository {
    fun getBuys(): Flow<MutableList<ExpenseModel>?>
    fun getTotalPriceUntil(lastPeriodDate: Date?): Flow<BigDecimal?>
    fun saveBuy(model: ExpenseModel): Flow<MutableList<ExpenseModel>?>
    fun setDefaultBuys(): Flow<MutableList<ExpenseModel>?>
    fun setAllBuysActive(isActive: Boolean): Flow<MutableList<ExpenseModel>?>
    fun switchActiveBuy(model: ExpenseModel): Flow<MutableList<ExpenseModel>?>
    fun deleteBuy(model: ExpenseModel): Flow<MutableList<ExpenseModel>?>
    fun deleteAllBuys(): Flow<MutableList<ExpenseModel>?>
}