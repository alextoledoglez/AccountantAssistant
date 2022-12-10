package com.personal.accountantAssistant.domain.useCases.bills

import com.personal.accountantAssistant.domain.repository.BillsRepository

class GetBillsUseCaseImpl(val repository: BillsRepository) : GetBillsUseCase {
    override fun invoke() = repository.getBills()
}