package com.personal.accountantAssistant.domain.useCases.buys

import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.BuysRepository

class ActiveBuysUseCaseImpl(val repository: BuysRepository) : ActiveBuysUseCase {
    override fun switchActiveBuy(model: ExpenseModel) = repository.switchActiveBuy(model)
    override fun setAllBuysActive(isActive: Boolean) = repository.setAllBuysActive(isActive)
}