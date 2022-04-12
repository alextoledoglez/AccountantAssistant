package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.dao.ExpenseDao
import com.personal.accountantAssistant.data.entities.ExpenseEntity
import com.personal.accountantAssistant.data.enums.BillsEnum
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.data.mappers.toEntity
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.utils.CalculatorUtils.accumulatedDecimalSum
import com.personal.accountantAssistant.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.*

class BillsRemoteDataSource(private val expenseDao: ExpenseDao) {

    private suspend fun getBillsBy(result: Int) = if (result > Int.DEFAULT_UID)
        expenseDao.selectAll(ExpensesType.BILL.name).toList()
    else
        mutableListOf()

    fun getBills(): Flow<List<ExpenseEntity>> = flowEmit {
        expenseDao.selectAll(ExpensesType.BILL.name).toList()
    }

    fun getTotalPriceUntil(lastPeriodDate: Date?): Flow<BigDecimal> = flowEmit {
        expenseDao.selectAll(ExpensesType.BILL.name).toList().stream().filter {
            it.isActive.orFalse() && DateUtils.isInRange(DateUtils.toDate(it.date), lastPeriodDate)
        }?.map {
            it.totalValue?.toBigDecimal()
        }?.reduce(BigDecimal.ZERO, accumulatedDecimalSum).orZero().rounded()
    }

    fun saveBill(model: ExpenseModel): Flow<List<ExpenseEntity>> = flowEmit {
        val edited = if (model.id > Int.DEFAULT_UID)
            expenseDao.update(model.toEntity())
        else
            expenseDao.insert(model.toEntity())
        getBillsBy(edited.toInt())
    }

    fun setDefaultBills(): Flow<List<ExpenseEntity>> = flowEmit {
        deleteAllBills()
        val insertedBills = expenseDao.insert(BillsEnum.toBillsEntities()).size
        getBillsBy(insertedBills)
    }

    fun setAllBillsActive(isActive: Boolean): Flow<List<ExpenseEntity>> = flowEmit {
        val activeBills = expenseDao.activeAll(isActive.toString(), ExpensesType.BILL.toString())
        getBillsBy(activeBills)
    }

    fun switchActiveBill(model: ExpenseModel): Flow<List<ExpenseEntity>> = flowEmit {
        val activeBill = expenseDao.setActive(model.id, !model.isActive)
        getBillsBy(activeBill)
    }

    fun deleteBill(model: ExpenseModel): Flow<List<ExpenseEntity>> = flowEmit {
        val deletedBill = expenseDao.delete(model.toEntity())
        getBillsBy(deletedBill)
    }

    fun deleteAllBills(): Flow<List<ExpenseEntity>> = flowEmit {
        val deletedBills = expenseDao.deleteByType(ExpensesType.BILL.name)
        getBillsBy(deletedBills)
    }

}