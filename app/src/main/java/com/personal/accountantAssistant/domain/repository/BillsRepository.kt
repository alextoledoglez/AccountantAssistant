package com.personal.accountantAssistant.domain.repository

import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.*

interface BillsRepository {
    fun getBills(): Flow<MutableList<ExpenseModel>?>
    fun getBillsDueSoon(): Flow<List<ExpenseModel>?>
    fun getBillsDueToday(): Flow<List<ExpenseModel>?>
    fun getSummary(): Flow<SummaryModel?>
    fun getTotalValueOn(period: Pair<Date?, Date?>): Flow<BigDecimal?>
    fun saveBill(model: ExpenseModel): Flow<MutableList<ExpenseModel>?>
    fun setAllBillsActive(isActive: Boolean): Flow<MutableList<ExpenseModel>?>
    fun switchActiveBill(model: ExpenseModel): Flow<MutableList<ExpenseModel>?>
    fun deleteBill(model: ExpenseModel): Flow<MutableList<ExpenseModel>?>
    fun deleteAllBills(): Flow<MutableList<ExpenseModel>?>
}