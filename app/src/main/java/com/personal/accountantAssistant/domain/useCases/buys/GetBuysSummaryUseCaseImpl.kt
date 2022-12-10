package com.personal.accountantAssistant.domain.useCases.buys

import com.personal.accountantAssistant.domain.repository.BuysRepository

class GetBuysSummaryUseCaseImpl(val repository: BuysRepository) : GetBuysSummaryUseCase {
    override fun invoke() = repository.getSummary()
}