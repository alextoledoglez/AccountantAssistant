package com.personal.accountantAssistant.data.repository

import com.personal.accountantAssistant.data.mappers.toEntity
import com.personal.accountantAssistant.data.mappers.toListModel
import com.personal.accountantAssistant.data.remote.BuysRemoteDataSource
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.BuysRepository
import kotlinx.coroutines.flow.map
import java.util.*

class BuysDataRepository(private val dataSource: BuysRemoteDataSource) : BuysRepository {

    override fun getBuys() = dataSource.getBuys().map { it.toListModel() }

    override fun getTotalPriceUntil(
        lastPeriodDate: Date?
    ) = dataSource.getTotalPriceUntil(lastPeriodDate)

    override fun saveBuy(model: ExpenseModel) = dataSource.saveBuy(model.toEntity()).map {
        it.toListModel()
    }

    override fun setDefaultBuys() = dataSource.setDefaultBuys().map { it.toListModel() }

    override fun setAllBuysActive(isActive: Boolean) = dataSource.setAllBuysActive(isActive).map {
        it.toListModel()
    }

    override fun switchActiveBuy(model: ExpenseModel) = dataSource.switchActiveBuy(model).map {
        it.toListModel()
    }

    override fun deleteBuy(model: ExpenseModel) = dataSource.deleteBuy(model.toEntity()).map {
        it.toListModel()
    }

    override fun deleteAllBuys() = dataSource.deleteAllBuys().map { it.toListModel() }

}