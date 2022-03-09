package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.dao.ExpenseDao
import com.personal.accountantAssistant.data.enums.BillsEnum
import com.personal.accountantAssistant.data.enums.BuysEnum
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.data.mappers.toEntity
import com.personal.accountantAssistant.data.mappers.toListModel
import com.personal.accountantAssistant.domain.models.BillModel
import com.personal.accountantAssistant.domain.models.BuyModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.DEFAULT_UID
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.utils.CalculatorUtils
import java.math.BigDecimal
import java.util.stream.Collectors

class ExpensesRemoteDataSource(private val expenseDao: ExpenseDao) {

    private suspend fun insertOrUpdateExpense(model: ExpenseModel?) = model?.let {
        if (it.id >= 0)
            expenseDao.update(it.toEntity())
        else
            expenseDao.insert(it.toEntity())
    }

    private suspend fun getExpenses(): List<ExpenseModel> =
        expenseDao.selectAll().toList().toListModel()

    private fun isAllActiveFrom(list: List<ExpenseModel>) = list.stream().allMatch { it.isActive }

    private fun isAnyActiveFrom(list: List<ExpenseModel>) = list.stream().anyMatch { it.isActive }

    private fun getTotalValuesFrom(list: List<ExpenseModel>) = list.stream().map { it.totalValue }
        .reduce(BigDecimal.ZERO, CalculatorUtils.accumulatedDecimalSum)

    private fun getSortedExpensesFrom(list: List<ExpenseModel>) = list.stream().sorted(
        Comparator.comparing<ExpenseModel?, Boolean?> { it.isActive }
            .thenComparingDouble { it.totalValue.orZero().toDouble() }
    ).collect(Collectors.toList()).asReversed()

    suspend fun getBuys() = getExpenses().let { list ->
        BuyModel(
            isAllChecked = isAllActiveFrom(list),
            isAnyChecked = isAnyActiveFrom(list),
            total = getTotalValuesFrom(list),
            expenses = getSortedExpensesFrom(list)
        )
    }

    suspend fun getBills() = getExpenses().let { list ->
        BillModel(
            isAllChecked = isAllActiveFrom(list),
            isAnyChecked = isAnyActiveFrom(list),
            total = getTotalValuesFrom(list),
            expenses = getSortedExpensesFrom(list)
        )
    }

    suspend fun setDefaultBuys() {
        if (expenseDao.deleteByType(ExpensesType.BUY.name) > Int.DEFAULT_UID) {
            BuysEnum.values().forEach { insertOrUpdateExpense(ExpenseModel(it.value)) }
        }
    }

    suspend fun setDefaultBills() {
        if (expenseDao.deleteByType(ExpensesType.BILL.name) > Int.DEFAULT_UID) {
            BillsEnum.values().forEach { insertOrUpdateExpense(ExpenseModel(it.value)) }
        }
    }

    suspend fun setAllBuysActive(isActive: Boolean) = getExpenses()
        .stream().filter { it.type == ExpensesType.BUY }.forEach {
            it.isActive = isActive
            //cardDao.update(it.toEntity())
        }

    suspend fun setAllBillsActive(isActive: Boolean) = getExpenses()
        .stream().filter { it.type == ExpensesType.BILL }.forEach {
            it.isActive = isActive
            //cardDao.update(it.toEntity())
        }

    suspend fun updateExpense(model: ExpenseModel) = expenseDao.update(model.toEntity()).toLong()

    suspend fun deleteExpense(model: ExpenseModel) = expenseDao.delete(model.toEntity()).toLong()

    suspend fun deleteAllBuys() {
        expenseDao.deleteByType(ExpensesType.BUY.name)
    }

    suspend fun deleteAllBills() {
        expenseDao.deleteByType(ExpensesType.BILL.name)
    }
}