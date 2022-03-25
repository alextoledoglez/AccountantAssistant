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

    suspend fun setDefaultBuys(): MutableList<ExpenseModel> {
        deleteAllBuys()
        val list = BuysEnum.values().map { ExpenseModel(it.value, ExpensesType.BUY).toEntity() }
        return if (expenseDao.insert(list).size > Int.DEFAULT_UID) list.toListModel() else mutableListOf()
    }

    suspend fun setDefaultBills(): MutableList<ExpenseModel> {
        deleteAllBills()
        val list = BillsEnum.values().map { ExpenseModel(it.value, ExpensesType.BILL).toEntity() }
        return if (expenseDao.insert(list).size > Int.DEFAULT_UID) list.toListModel() else mutableListOf()
    }

    suspend fun setAllBuysActive(isActive: Boolean) = if (
        expenseDao.activeAll(isActive.toString(), ExpensesType.BUY.toString()) > Int.DEFAULT_UID
    ) getBuys() else mutableListOf()

    suspend fun setAllBillsActive(isActive: Boolean) = if (
        expenseDao.activeAll(isActive.toString(), ExpensesType.BILL.toString()) > Int.DEFAULT_UID
    ) getBills() else mutableListOf()

    suspend fun switchActiveExpense(model: ExpenseModel): MutableList<ExpenseModel> {
        val entity = model.also { it.isActive = !it.isActive }.toEntity()
        return if (expenseDao.update(entity).toLong() > Int.DEFAULT_UID) {
            when (model.type) {
                ExpensesType.BUY -> getBuys()
                ExpensesType.BILL -> getBills()
                else -> mutableListOf()
            }
        } else mutableListOf()
    }

    suspend fun deleteExpense(model: ExpenseModel): MutableList<ExpenseModel> {
        return if (expenseDao.delete(model.toEntity()).toLong() > Int.DEFAULT_UID) {
            when (model.type) {
                ExpensesType.BUY -> getBuys()
                ExpensesType.BILL -> getBills()
                else -> mutableListOf()
            }
        } else mutableListOf()
    }

    suspend fun deleteAllBuys() = if (
        expenseDao.deleteByType(ExpensesType.BUY.name) > Int.DEFAULT_UID
    ) getBuys() else mutableListOf()

    suspend fun deleteAllBills() = if (
        expenseDao.deleteByType(ExpensesType.BILL.name) > Int.DEFAULT_UID
    ) getBills() else mutableListOf()
}