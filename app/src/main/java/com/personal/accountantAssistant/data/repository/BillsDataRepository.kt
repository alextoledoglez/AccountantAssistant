package com.personal.accountantAssistant.data.repository

import com.personal.accountantAssistant.data.mappers.toListModel
import com.personal.accountantAssistant.data.remote.BillsRemoteDataSource
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.BillsRepository
import kotlinx.coroutines.flow.map
import java.util.*

class BillsDataRepository(private val dataSource: BillsRemoteDataSource) : BillsRepository {

    override fun getBills() = dataSource.getBills().map { it.toListModel() }

    override fun getTotalPriceUntil(
        lastPeriodDate: Date?
    ) = dataSource.getTotalPriceUntil(lastPeriodDate)

    override fun saveBill(model: ExpenseModel) = dataSource.saveBill(model).map { it.toListModel() }

    override fun setDefaultBills() = dataSource.setDefaultBills().map { it.toListModel() }

    override fun setAllBillsActive(isActive: Boolean) = dataSource.setAllBillsActive(isActive).map {
        it.toListModel()
    }

    override fun switchActiveBill(model: ExpenseModel) = dataSource.switchActiveBill(model).map {
        it.toListModel()
    }

    override fun deleteBill(model: ExpenseModel) = dataSource.deleteBill(model).map {
        it.toListModel()
    }

    override fun deleteAllBills() = dataSource.deleteAllBills().map { it.toListModel() }
}