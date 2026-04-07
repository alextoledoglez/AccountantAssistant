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

class BillsViewModel(
    private val getBillsUseCase: GetBillsUseCase,
    private val getBillsSummaryUseCase: GetBillsSummaryUseCase,
    private val saveBillUseCase: SaveBillUseCase,
    private val activeBillsUseCase: ActiveBillsUseCase,
    private val deleteBillsUseCase: DeleteBillsUseCase,
    analytics: AnalyticsProvider? = null
) : BaseViewModel(analytics) {

    private var _summary = MutableLiveData<SummaryModel>()
    var summary: LiveData<SummaryModel> = _summary

    private var _bills = MutableLiveData<MutableList<ExpenseModel>?>()
    var bills: LiveData<MutableList<ExpenseModel>?> = _bills

    fun loadSummary() {
        launch {
            getBillsSummaryUseCase()
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _summary.postValue(it) }
        }
    }

    fun loadBills() {
        launch {
            getBillsUseCase()
                .onStart { setLoading() }
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _bills.postValue(it) }
        }
    }

    fun saveBill(model: ExpenseModel) {
        launch {
            saveBillUseCase(model.toBill())
                .onStart { setLoading() }
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _bills.postValue(it) }
        }
    }

    fun setAllBillsActive(isActive: Boolean) {
        launch {
            activeBillsUseCase.setAllBillsActive(isActive)
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _bills.postValue(it) }
        }
    }

    fun switchActiveBill(model: ExpenseModel) {
        launch {
            activeBillsUseCase.switchActiveBill(model.toBill())
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _bills.postValue(it) }
        }
    }

    fun deleteBill(model: ExpenseModel) {
        launch {
            deleteBillsUseCase.deleteBill(model.toBill())
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _bills.postValue(it) }
        }
    }

    fun deleteAllBills() {
        launch {
            deleteBillsUseCase.deleteAllBills()
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _bills.postValue(it) }
        }
    }
}