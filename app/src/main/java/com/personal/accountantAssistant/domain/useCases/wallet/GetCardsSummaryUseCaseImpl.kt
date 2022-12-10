package com.personal.accountantAssistant.domain.useCases.wallet

import com.personal.accountantAssistant.domain.repository.CardsRepository

class GetCardsSummaryUseCaseImpl(val repository: CardsRepository) : GetCardsSummaryUseCase {
    override fun invoke() = repository.getSummary()
}