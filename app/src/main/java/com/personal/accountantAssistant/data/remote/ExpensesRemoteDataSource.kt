package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.dao.ExpenseDao
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.extensions.isDateUntil
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.rounded
import com.personal.accountantAssistant.extensions.toDate
import java.util.*

class ExpensesRemoteDataSource(private val expenseDao: ExpenseDao) {

    private suspend fun getAllBills() = expenseDao.selectAll(ExpensesType.BILL.name).toList()

    suspend fun getBuys() = expenseDao.getSummary(ExpensesType.BUY.name).totalValue
        .orZero().toBigDecimal().rounded()

    suspend fun getBills(lastDate: Date?) = getAllBills()
        .filter { it.date.toDate().isDateUntil(lastDate) }
        .sumOf { it.totalValue.orZero() }
        .orZero()
        .toBigDecimal()
        .rounded()

    suspend fun getTotal(lastDate: Date?) = getBuys().plus(getBills(lastDate).orZero())
}