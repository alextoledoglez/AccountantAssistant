package com.personal.accountantAssistant.data.repository

import com.personal.accountantAssistant.data.mappers.toEntity
import com.personal.accountantAssistant.data.mappers.toListModel
import com.personal.accountantAssistant.data.mappers.toSummaryModel
import com.personal.accountantAssistant.data.remote.BillsRemoteDataSource
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.BillsRepository
import com.personal.accountantAssistant.extensions.*
import kotlinx.coroutines.flow.map
import java.util.*

class BillsDataRepository(private val dataSource: BillsRemoteDataSource) : BillsRepository {

    override fun getBills() = dataSource.getBills().map { it.toListModel() }

    override fun getBillsDueSoon() = dataSource.getActiveBills().map { entities ->
        entities.toListModel().filter { it.date.isDueSoon() }
    }

    override fun getBillsDueToday() = dataSource.getActiveBills().map { entities ->
        entities.toListModel().filter { it.date.isDueToday() }
    }

    override fun getSummary() = dataSource.getSummary().map { it.toSummaryModel() }

    override fun getTotalValueOn(period: Pair<Date?, Date?>) =
        dataSource.getTotalValueOn(period).map { it.orZero().toBigDecimal().rounded() }

    override fun saveBill(model: ExpenseModel) = dataSource.saveBill(model.toEntity()).map {
        it.toListModel()
    }

    override fun setAllBillsActive(isActive: Boolean) = dataSource.setAllBillsActive(
        isActive.toInt()
    ).map { it.toListModel() }

    override fun switchActiveBill(model: ExpenseModel) = dataSource.switchActiveBill(
        model.toEntity()
    ).map { it.toListModel() }

    override fun deleteBill(model: ExpenseModel) = dataSource.deleteBill(model.toEntity()).map {
        it.toListModel()
    }

    override fun deleteAllBills() = dataSource.deleteAllBills().map { it.toListModel() }
}