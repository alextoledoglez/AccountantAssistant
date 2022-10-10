package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.domain.repository.BillsRepository

class GetBillsDueSoonUseCaseImpl(
    private val billsRepository: BillsRepository
) : GetBillsDueSoonUseCase {
    override fun invoke() = billsRepository.getBillsDueSoon()
}