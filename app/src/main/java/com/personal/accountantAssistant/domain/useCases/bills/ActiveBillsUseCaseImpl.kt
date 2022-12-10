package com.personal.accountantAssistant.domain.useCases.bills

import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.BillsRepository

class ActiveBillsUseCaseImpl(val repository: BillsRepository) : ActiveBillsUseCase {
    override fun switchActiveBill(model: ExpenseModel) = repository.switchActiveBill(model)
    override fun setAllBillsActive(isActive: Boolean) = repository.setAllBillsActive(isActive)
}