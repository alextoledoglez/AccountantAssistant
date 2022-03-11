package com.personal.accountantAssistant.data.repository

import com.personal.accountantAssistant.data.remote.ExpensesRemoteDataSource
import com.personal.accountantAssistant.domain.models.BillsModel
import com.personal.accountantAssistant.domain.models.BuysModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.ExpensesRepository
import com.personal.accountantAssistant.extensions.flowEmit
import kotlinx.coroutines.flow.Flow

class ExpensesDataRepository(
    private val dataSource: ExpensesRemoteDataSource
) : ExpensesRepository {

    override fun getBuys(): Flow<BuysModel?> = flowEmit { dataSource.getBuys() }
    override fun getBills(): Flow<BillsModel?> = flowEmit { dataSource.getBills() }
    override fun setDefaultBuys(): Flow<Unit> = flowEmit { dataSource.setDefaultBuys() }
    override fun setDefaultBills(): Flow<Unit> = flowEmit { dataSource.setDefaultBills() }

    override fun setAllBuysActive(isActive: Boolean): Flow<Unit> = flowEmit {
        dataSource.setAllBuysActive(isActive)
    }

    override fun setAllBillsActive(isActive: Boolean): Flow<Unit> = flowEmit {
        dataSource.setAllBillsActive(isActive)
    }

    override fun updateExpense(model: ExpenseModel): Flow<Long> = flowEmit {
        dataSource.updateExpense(model)
    }

    override fun deleteExpense(model: ExpenseModel): Flow<Long> = flowEmit {
        dataSource.deleteExpense(model)
    }

    override fun deleteAllBuys(): Flow<Unit> = flowEmit { dataSource.deleteAllBuys() }
    override fun deleteAllBills(): Flow<Unit> = flowEmit { dataSource.deleteAllBills() }
}