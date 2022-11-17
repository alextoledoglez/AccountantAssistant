package com.personal.accountantAssistant.data.repository

import com.personal.accountantAssistant.data.remote.ExpensesRemoteDataSource
import com.personal.accountantAssistant.domain.models.ExpensesValuesModel
import com.personal.accountantAssistant.domain.repository.ExpensesRepository
import com.personal.accountantAssistant.extensions.flowEmit
import com.personal.accountantAssistant.extensions.orZero
import java.math.BigDecimal
import java.util.*

class ExpensesDataRepository(
    private val dataSource: ExpensesRemoteDataSource
) : ExpensesRepository {

    override fun getExpenses(lastDate: Date?, availableMoney: BigDecimal?) = flowEmit {
        ExpensesValuesModel(
            buys = dataSource.getBuys(),
            bills = dataSource.getBills(lastDate),
            total = dataSource.getTotal(lastDate),
            available = availableMoney.orZero()
        )
    }
}