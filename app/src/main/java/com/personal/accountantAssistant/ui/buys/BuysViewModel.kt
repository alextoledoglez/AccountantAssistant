package com.personal.accountantAssistant.ui.buys

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.mappers.toBuy
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.domain.repository.BuysRepository
import com.personal.accountantAssistant.extensions.onError
import com.personal.accountantAssistant.providers.AnalyticsProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class BuysViewModel(
    analytics: AnalyticsProvider?, private val repository: BuysRepository?
) : BaseViewModel(analytics) {

    private var _summary = MutableLiveData<SummaryModel>()
    var summary: LiveData<SummaryModel> = _summary

    private var _buys = MutableLiveData<MutableList<ExpenseModel>?>()
    var buys: LiveData<MutableList<ExpenseModel>?> = _buys

    fun getBuys() {
        launch {
            repository?.getBuys()
                ?.onStart { setLoading() }
                ?.onError { setMessage(it.message) }
                ?.onCompletion { setData() }
                ?.collect { _buys.postValue(it) }
        }
    }

    fun loadSummary() {
        launch {
            repository?.getSummary()
                ?.onError { setMessage(it.message) }
                ?.onCompletion { setData() }
                ?.collect { _summary.postValue(it) }
        }
    }

    fun saveBuy(model: ExpenseModel) {
        launch {
            repository?.saveBuy(model.toBuy())
                ?.onStart { setLoading() }
                ?.onError { setMessage(it.message) }
                ?.onCompletion { setData() }
                ?.collect { _buys.postValue(it) }
        }
    }

    fun setAllBuysActive(isActive: Boolean) {
        launch {
            repository?.setAllBuysActive(isActive)
                ?.onError { setMessage(it.message) }
                ?.onCompletion { setData() }
                ?.collect { _buys.postValue(it) }
        }
    }

    fun switchActiveBuy(model: ExpenseModel) {
        launch {
            repository?.switchActiveBuy(model.toBuy())
                ?.onError { setMessage(it.message) }
                ?.onCompletion { setData() }
                ?.collect { _buys.postValue(it) }
        }
    }

    fun deleteBuy(model: ExpenseModel) {
        launch {
            repository?.deleteBuy(model.toBuy())
                ?.onError { setMessage(it.message) }
                ?.onCompletion { setData() }
                ?.collect { _buys.postValue(it) }
        }
    }

    fun deleteAllBuys() {
        launch {
            repository?.deleteAllBuys()
                ?.onError { setMessage(it.message) }
                ?.onCompletion { setData() }
                ?.collect { _buys.postValue(it) }
        }
    }
}