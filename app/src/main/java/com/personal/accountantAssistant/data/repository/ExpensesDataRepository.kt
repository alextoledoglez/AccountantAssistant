package com.personal.accountantAssistant.data.repository

import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.data.remote.ExpensesRemoteDataSource
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.ExpensesRepository
import com.personal.accountantAssistant.extensions.flowEmit
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.*

class ExpensesDataRepository(
    private val dataSource: ExpensesRemoteDataSource
) : ExpensesRepository {

    override fun getBuys(): Flow<MutableList<ExpenseModel>?> = flowEmit { dataSource.getBuys() }

    override fun getBills(): Flow<MutableList<ExpenseModel>?> = flowEmit { dataSource.getBills() }

    override fun getTotalPriceUntil(
        type: ExpensesType, lastPeriodDate: Date?
    ): Flow<BigDecimal?> = flowEmit { dataSource.getTotalPriceUntil(type, lastPeriodDate) }

    override fun saveExpense(model: ExpenseModel): Flow<Unit> = flowEmit {
        dataSource.saveExpense(model)
    }

    override fun setDefaultBuys(): Flow<MutableList<ExpenseModel>?> = flowEmit {
        dataSource.setDefaultBuys()
    }

    override fun setDefaultBills(): Flow<MutableList<ExpenseModel>?> = flowEmit {
        dataSource.setDefaultBills()
    }

    override fun setAllBuysActive(isActive: Boolean): Flow<MutableList<ExpenseModel>?> = flowEmit {
        dataSource.setAllBuysActive(isActive)
    }

    override fun setAllBillsActive(isActive: Boolean): Flow<MutableList<ExpenseModel>?> = flowEmit {
        dataSource.setAllBillsActive(isActive)
    }

    override fun switchActiveExpense(model: ExpenseModel): Flow<MutableList<ExpenseModel>?> =
        flowEmit { dataSource.switchActiveExpense(model) }

    override fun deleteExpense(model: ExpenseModel): Flow<Long> = flowEmit {
        dataSource.deleteExpense(model)
    }

    override fun deleteAllBuys(): Flow<MutableList<ExpenseModel>?> = flowEmit {
        dataSource.deleteAllBuys()
    }

    override fun deleteAllBills(): Flow<MutableList<ExpenseModel>?> = flowEmit {
        dataSource.deleteAllBills()
    }
}