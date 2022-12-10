package com.personal.accountantAssistant.domain.useCases.buys

import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.BuysRepository
import kotlinx.coroutines.flow.Flow

class GetBuysUseCaseImpl(val repository: BuysRepository) : GetBuysUseCase {
    override fun invoke(): Flow<MutableList<ExpenseModel>?> = repository.getBuys()
}