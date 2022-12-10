package com.personal.accountantAssistant.domain.useCases.bills

import com.personal.accountantAssistant.domain.repository.BillsRepository

class GetBillsSummaryUseCaseImpl(val repository: BillsRepository) : GetBillsSummaryUseCase {
    override fun invoke() = repository.getSummary()
}