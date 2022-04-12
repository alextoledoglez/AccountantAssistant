package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.dao.ExpenseDao
import com.personal.accountantAssistant.data.entities.ExpenseEntity
import com.personal.accountantAssistant.data.enums.BuysEnum
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.utils.CalculatorUtils.accumulatedDecimalSum
import com.personal.accountantAssistant.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.*

class BuysRemoteDataSource(private val expenseDao: ExpenseDao) {

    private suspend fun getBuysBy(result: Int) = if (result > Int.DEFAULT_UID)
        expenseDao.selectAll(ExpensesType.BUY.name).toList()
    else
        mutableListOf()

    fun getBuys(): Flow<List<ExpenseEntity>> = flowEmit {
        expenseDao.selectAll(ExpensesType.BUY.name).toList()
    }

    fun getTotalPriceUntil(lastPeriodDate: Date?): Flow<BigDecimal> = flowEmit {
        expenseDao.selectAll(ExpensesType.BUY.name).toList().stream().filter {
            it.isActive.orFalse() && DateUtils.isInRange(DateUtils.toDate(it.date), lastPeriodDate)
        }?.map {
            it.totalValue?.toBigDecimal()
        }?.reduce(BigDecimal.ZERO, accumulatedDecimalSum).orZero().rounded()
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

    fun setAllBuysActive(isActive: Boolean): Flow<List<ExpenseEntity>> = flowEmit {
        val activeBuys = expenseDao.activeAll(isActive.toString(), ExpensesType.BUY.toString())
        getBuysBy(activeBuys)
    }

    fun switchActiveBuy(model: ExpenseModel): Flow<List<ExpenseEntity>> = flowEmit {
        val activeBuy = expenseDao.setActive(model.id, !model.isActive)
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