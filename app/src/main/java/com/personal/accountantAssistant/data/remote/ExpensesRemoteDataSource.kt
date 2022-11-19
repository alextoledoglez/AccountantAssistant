package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.dao.ExpenseDao
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.extensions.*
import java.util.*

class ExpensesRemoteDataSource(private val expenseDao: ExpenseDao) {

    suspend fun getBuys() = expenseDao.getSummary(ExpensesType.BUY.name).totalValue
        .orZero().toBigDecimal().rounded()

    suspend fun getBillsOn(period: Pair<Date?, Date?>) = expenseDao.selectActiveBills().toList()
        .filter { it.date.toDate().isDateBetween(period.first, period.second) }
        .sumOf { it.totalValue.orZero() }
        .orZero()
        .toBigDecimal()
        .rounded()

    suspend fun getTotalOn(period: Pair<Date?, Date?>) = getBuys().plus(getBillsOn(period).orZero())
}