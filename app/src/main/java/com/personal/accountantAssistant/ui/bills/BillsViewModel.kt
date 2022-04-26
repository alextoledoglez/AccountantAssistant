package com.personal.accountantAssistant.ui.bills

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.mappers.toBill
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.domain.repository.BillsRepository
import com.personal.accountantAssistant.extensions.onError
import com.personal.accountantAssistant.providers.AnalyticsProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class BillsViewModel(
    analytics: AnalyticsProvider?, private val repository: BillsRepository?
) : BaseViewModel(analytics) {

    private var _summary = MutableLiveData<SummaryModel>()
    var summary: LiveData<SummaryModel> = _summary

    private var _bills = MutableLiveData<MutableList<ExpenseModel>?>()
    var bills: LiveData<MutableList<ExpenseModel>?> = _bills

    private fun postBillsValues(list: MutableList<ExpenseModel>?) {
        _bills.postValue(list)
        setData()
    }

    fun getBills() {
        launch {
            repository?.getBills()
                ?.onStart { setLoading() }
                ?.onError { setMessage(it.message) }
                ?.collect { postBillsValues(it) }
        }
    }

    fun loadSummary() {
        launch {
            repository?.getSummary()
                ?.onError { setMessage(it.message) }
                ?.collect {
                    _summary.postValue(it)
                    setData()
                }
        }
    }

    fun saveBill(model: ExpenseModel) {
        launch {
            repository?.saveBill(model.toBill())?.onStart { setLoading() }
                ?.onError { setMessage(it.message) }
                ?.collect { postBillsValues(it) }
        }
    }

    fun setAllBillsActive(isActive: Boolean) {
        launch {
            repository?.setAllBillsActive(isActive)
                ?.onError { setMessage(it.message) }
                ?.collect { postBillsValues(it) }
        }
    }

    fun switchActiveBill(model: ExpenseModel) {
        launch {
            repository?.switchActiveBill(model.toBill())
                ?.onError { setMessage(it.message) }
                ?.collect { postBillsValues(it) }
        }
    }

    fun deleteBill(model: ExpenseModel) {
        launch {
            repository?.deleteBill(model.toBill())
                ?.onError { setMessage(it.message) }
                ?.collect { postBillsValues(it) }
        }
    }

    fun deleteAllBills() {
        launch {
            repository?.deleteAllBills()
                ?.onError { setMessage(it.message) }
                ?.collect { postBillsValues(it) }
        }
    }
}