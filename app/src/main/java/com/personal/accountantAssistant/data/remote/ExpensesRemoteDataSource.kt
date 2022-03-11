package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.dao.ExpenseDao
import com.personal.accountantAssistant.data.enums.BillsEnum
import com.personal.accountantAssistant.data.enums.BuysEnum
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.data.mappers.toBillsModel
import com.personal.accountantAssistant.data.mappers.toBuysModel
import com.personal.accountantAssistant.data.mappers.toEntity
import com.personal.accountantAssistant.data.mappers.toListModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.DEFAULT_UID

class ExpensesRemoteDataSource(private val expenseDao: ExpenseDao) {

    private suspend fun insertOrUpdateExpense(model: ExpenseModel?) = model?.let {
        if (it.id >= 0)
            expenseDao.update(it.toEntity())
        else
            expenseDao.insert(it.toEntity())
    }

    suspend fun getBuys() = expenseDao.selectAll(
        ExpensesType.BUY.name
    ).toList().toListModel().toBuysModel()

    suspend fun getBills() = expenseDao.selectAll(
        ExpensesType.BILL.name
    ).toList().toListModel().toBillsModel()

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