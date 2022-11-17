package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.dao.ExpenseDao
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.rounded
import com.personal.accountantAssistant.extensions.toDateStr
import java.util.*

class ExpensesRemoteDataSource(private val expenseDao: ExpenseDao) {

    suspend fun getBuys() = expenseDao.getSummary(ExpensesType.BUY.name).totalValue
        .orZero().toBigDecimal().rounded()

    suspend fun getBills(lastDate: Date?) = expenseDao.getTotalValueUntil(
        lastDate.toDateStr(), ExpensesType.BILL.name
    ).totalValue.orZero().toBigDecimal().rounded()

    suspend fun getTotal(lastDate: Date?) = getBuys().plus(getBills(lastDate).orZero())
}