package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.dao.ExpenseDao
import com.personal.accountantAssistant.data.entities.ExpenseEntity
import com.personal.accountantAssistant.data.enums.BuysEnum
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.extensions.DEFAULT_UID
import com.personal.accountantAssistant.extensions.flowEmit
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.toDateStr
import kotlinx.coroutines.flow.Flow
import java.util.*

class BuysRemoteDataSource(private val expenseDao: ExpenseDao) {

    private suspend fun getBuysBy(result: Int) = if (result > Int.DEFAULT_UID)
        expenseDao.selectAll(ExpensesType.BUY.name).toList()
    else
        mutableListOf()

    fun getBuys(): Flow<List<ExpenseEntity>> = flowEmit {
        expenseDao.selectAll(ExpensesType.BUY.name).toList()
    }

    fun getSummary(): Flow<ExpenseEntity> = flowEmit { expenseDao.getSummary() }

    fun getTotalValueUntil(date: Date?): Flow<ExpenseEntity> = flowEmit {
        expenseDao.getTotalValueUntil(date.toDateStr())
    }

    fun saveBuy(entity: ExpenseEntity): Flow<List<ExpenseEntity>> = flowEmit {
        val edited = if (entity.id.orZero() > Int.DEFAULT_UID)
            expenseDao.update(entity)
        else
            expenseDao.insert(entity)
        getBuysBy(edited.toInt())
    }

    fun setDefaultBuys(): Flow<List<ExpenseEntity>> = flowEmit {
        deleteAllBuys()
        val insertedBuys = expenseDao.insert(BuysEnum.toBuysEntities()).size
        getBuysBy(insertedBuys)
    }

    fun setAllBuysActive(active: Int): Flow<List<ExpenseEntity>> = flowEmit {
        val activeBuys = expenseDao.activeAll(active, ExpensesType.BUY.toString())
        getBuysBy(activeBuys)
    }

    fun switchActiveBuy(entity: ExpenseEntity): Flow<List<ExpenseEntity>> = flowEmit {
        val activeBuy = expenseDao.setActive(entity.id.orZero(), entity.active.orZero())
        getBuysBy(activeBuy)
    }

    fun deleteBuy(entity: ExpenseEntity): Flow<List<ExpenseEntity>> = flowEmit {
        val deletedBuy = expenseDao.delete(entity)
        getBuysBy(deletedBuy)
    }

    fun deleteAllBuys(): Flow<List<ExpenseEntity>> = flowEmit {
        val deletedBuys = expenseDao.deleteByType(ExpensesType.BUY.name)
        getBuysBy(deletedBuys)
    }

}