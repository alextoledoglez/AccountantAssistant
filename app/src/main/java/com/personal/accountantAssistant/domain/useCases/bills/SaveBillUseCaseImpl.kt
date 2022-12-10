package com.personal.accountantAssistant.domain.useCases.bills

import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.BillsRepository

class SaveBillUseCaseImpl(val repository: BillsRepository) : SaveBillUseCase {
    override fun invoke(model: ExpenseModel) = repository.saveBill(model)
}