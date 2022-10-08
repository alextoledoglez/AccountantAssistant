package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.domain.repository.BillsRepository

class GetBillsDueTodayUseCaseImpl(
    private val billsRepository: BillsRepository
) : GetBillsDueTodayUseCase {
    override fun invoke() = billsRepository.getBillsDueToday()
}