package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.dao.ExpenseDao
import com.personal.accountantAssistant.data.entities.ExpenseEntity
import com.personal.accountantAssistant.data.enums.BillsEnum
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.extensions.flowEmit
import com.personal.accountantAssistant.extensions.isMoreThanZero
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.toDateStr
import kotlinx.coroutines.flow.Flow
import java.util.*

class BillsRemoteDataSource(private val expenseDao: ExpenseDao) {

    private suspend fun getBillsBy(result: Int) = if (result.isMoreThanZero())
        expenseDao.selectAll(ExpensesType.BILL.name).toList()
    else
        mutableListOf()

    fun getBills(): Flow<List<ExpenseEntity>> = flowEmit {
        expenseDao.selectAll(ExpensesType.BILL.name).toList()
    }

    fun getSummary(): Flow<ExpenseEntity> = flowEmit {
        expenseDao.getSummary(ExpensesType.BILL.name)
    }

    fun getTotalValueUntil(date: Date?): Flow<ExpenseEntity> = flowEmit {
        expenseDao.getTotalValueUntil(date.toDateStr(), ExpensesType.BILL.name)
    }

    fun saveBill(entity: ExpenseEntity): Flow<List<ExpenseEntity>> = flowEmit {
        val edited = if (entity.id.isMoreThanZero())
            expenseDao.update(entity)
        else
            expenseDao.insert(entity)
        getBillsBy(edited.toInt())
    }

    fun setDefaultBills(): Flow<List<ExpenseEntity>> = flowEmit {
        deleteAllBills()
        val insertedBills = expenseDao.insert(BillsEnum.toBillsEntities()).size
        getBillsBy(insertedBills)
    }

    fun setAllBillsActive(active: Int): Flow<List<ExpenseEntity>> = flowEmit {
        val activeBills = expenseDao.activeAll(active, ExpensesType.BILL.name)
        getBillsBy(activeBills)
    }

    fun switchActiveBill(entity: ExpenseEntity): Flow<List<ExpenseEntity>> = flowEmit {
        val activeBill = expenseDao.setActive(entity.id.orZero(), entity.active.orZero())
        getBillsBy(activeBill)
    }

    fun deleteBill(entity: ExpenseEntity): Flow<List<ExpenseEntity>> = flowEmit {
        val deletedBill = expenseDao.delete(entity)
        getBillsBy(deletedBill)
    }

    fun deleteAllBills(): Flow<List<ExpenseEntity>> = flowEmit {
        val deletedBills = expenseDao.deleteByType(ExpensesType.BILL.name)
        getBillsBy(deletedBills)
    }

}