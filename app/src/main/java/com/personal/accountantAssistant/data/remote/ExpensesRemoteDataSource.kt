package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.dao.ExpenseDao
import com.personal.accountantAssistant.data.enums.BillsEnum
import com.personal.accountantAssistant.data.enums.BuysEnum
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.data.mappers.toEntity
import com.personal.accountantAssistant.data.mappers.toListModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.DEFAULT_UID
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.rounded
import com.personal.accountantAssistant.utils.CalculatorUtils.accumulatedDecimalSum
import com.personal.accountantAssistant.utils.DateUtils
import java.math.BigDecimal
import java.util.*

class ExpensesRemoteDataSource(private val expenseDao: ExpenseDao) {

    suspend fun saveExpense(model: ExpenseModel?) = model?.let {
        if (it.id > Int.DEFAULT_UID)
            expenseDao.update(it.toEntity())
        else
            expenseDao.insert(it.toEntity())
    }

    suspend fun getBuys() = expenseDao.selectAll(ExpensesType.BUY.name).toList().toListModel()

    suspend fun getBills() = expenseDao.selectAll(ExpensesType.BILL.name).toList().toListModel()

    suspend fun getTotalPriceUntil(
        type: ExpensesType, lastPeriodDate: Date?
    ) = expenseDao.selectAll(type.name).toList().toListModel().stream().filter {
        it.isActive && DateUtils.isInRange(it.date, lastPeriodDate)
    }?.map { it.totalValue }?.reduce(BigDecimal.ZERO, accumulatedDecimalSum).orZero().rounded()

    suspend fun setDefaultBuys() {
        deleteAllBuys()
        BuysEnum.values().forEach { saveExpense(ExpenseModel(it.value, ExpensesType.BUY)) }
    }

    suspend fun setDefaultBills() {
        deleteAllBills()
        BillsEnum.values().forEach { saveExpense(ExpenseModel(it.value, ExpensesType.BILL)) }
    }

    suspend fun setAllBuysActive(isActive: Boolean) = expenseDao.activeAll(
        isActive.toString(), ExpensesType.BUY.toString()
    )

    suspend fun setAllBillsActive(isActive: Boolean) = expenseDao.activeAll(
        isActive.toString(), ExpensesType.BILL.toString()
    )

    suspend fun updateExpense(model: ExpenseModel) = expenseDao.update(model.toEntity()).toLong()

    suspend fun deleteExpense(model: ExpenseModel) = expenseDao.delete(model.toEntity()).toLong()

    suspend fun deleteAllBuys() {
        expenseDao.deleteByType(ExpensesType.BUY.name)
    }

    suspend fun deleteAllBills() {
        expenseDao.deleteByType(ExpensesType.BILL.name)
    }
}