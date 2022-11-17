package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.domain.repository.ExpensesRepository
import java.math.BigDecimal
import java.util.*

class GetExpensesUseCaseImpl(
    private val expensesRepository: ExpensesRepository
) : GetExpensesUseCase {

    override fun invoke(lastDate: Date?, availableMoney: BigDecimal?) =
        expensesRepository.getExpenses(lastDate, availableMoney)
}