package com.personal.accountantAssistant.ui.bills

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.mappers.toBill
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.domain.useCases.bills.*
import com.personal.accountantAssistant.extensions.onError
import com.personal.accountantAssistant.providers.AnalyticsProvider
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.math.BigDecimal

class BillsViewModel(
    private val getBillsUseCase: GetBillsUseCase,
    private val saveBillUseCase: SaveBillUseCase,
    private val activeBillsUseCase: ActiveBillsUseCase,
    private val deleteBillsUseCase: DeleteBillsUseCase,
    analytics: AnalyticsProvider? = null
) : BaseViewModel(analytics) {

    private val _summary = MutableLiveData<SummaryModel>()
    val summary: LiveData<SummaryModel> = _summary

    private val _bills = MutableLiveData<MutableList<ExpenseModel>?>()
    val bills: LiveData<MutableList<ExpenseModel>?> = _bills

    private fun postBills(list: MutableList<ExpenseModel>?) {
        _bills.postValue(list)
        val activeItems = list?.filter { it.isActive }.orEmpty()
        val activeItemsAmount = activeItems.fold(BigDecimal.ZERO) { acc, item ->
            acc.add(item.calculateTotalValue())
        }
        _summary.postValue(SummaryModel(activeCount = activeItems.size, total = activeItemsAmount))
    }

    fun loadBills() {
        launch {
            getBillsUseCase()
                .onStart { setLoading() }
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { postBills(list = it) }
        }
    }

    fun saveBill(model: ExpenseModel) {
        launch {
            saveBillUseCase(model.toBill())
                .onStart { setLoading() }
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { postBills(list = it) }
        }
    }

    fun setAllBillsActive(isActive: Boolean) {
        launch {
            activeBillsUseCase.setAllBillsActive(isActive)
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { postBills(list = it) }
        }
    }

    fun switchActiveBill(model: ExpenseModel) {
        launch {
            activeBillsUseCase.switchActiveBill(model.toBill())
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { postBills(list = it) }
        }
    }

    fun deleteBill(model: ExpenseModel) {
        launch {
            deleteBillsUseCase.deleteBill(model.toBill())
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { postBills(list = it) }
        }
    }

    fun deleteAllBills() {
        launch {
            deleteBillsUseCase.deleteAllBills()
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { postBills(list = it) }
        }
    }
}