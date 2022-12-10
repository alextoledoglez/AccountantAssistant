package com.personal.accountantAssistant.domain.useCases.bills

import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.BillsRepository

class DeleteBillsUseCaseImpl(val repository: BillsRepository) : DeleteBillsUseCase {
    override fun deleteBill(model: ExpenseModel) = repository.deleteBill(model)
    override fun deleteAllBills() = repository.deleteAllBills()
}