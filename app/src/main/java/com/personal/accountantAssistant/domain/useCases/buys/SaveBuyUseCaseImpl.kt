package com.personal.accountantAssistant.domain.useCases.buys

import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.BuysRepository

class SaveBuyUseCaseImpl(val repository: BuysRepository) : SaveBuyUseCase {
    override fun invoke(model: ExpenseModel) = repository.saveBuy(model)
}