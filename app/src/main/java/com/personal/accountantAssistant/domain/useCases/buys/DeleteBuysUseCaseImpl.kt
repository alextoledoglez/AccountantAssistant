package com.personal.accountantAssistant.domain.useCases.buys

import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.BuysRepository
import kotlinx.coroutines.flow.Flow

class DeleteBuysUseCaseImpl(val repository: BuysRepository) : DeleteBuysUseCase {
    override fun deleteBuy(model: ExpenseModel) = repository.deleteBuy(model)
    override fun deleteAllBuys() = repository.deleteAllBuys()
}