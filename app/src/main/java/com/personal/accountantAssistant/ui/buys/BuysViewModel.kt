package com.personal.accountantAssistant.ui.buys

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.mappers.toBuy
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.domain.useCases.buys.*
import com.personal.accountantAssistant.extensions.onError
import com.personal.accountantAssistant.providers.AnalyticsProvider
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class BuysViewModel(
    private val getBuysUseCase: GetBuysUseCase,
    private val getBuysSummaryUseCase: GetBuysSummaryUseCase,
    private val saveBuyUseCase: SaveBuyUseCase,
    private val activeBuysUseCase: ActiveBuysUseCase,
    private val deleteBuysUseCase: DeleteBuysUseCase,
    analytics: AnalyticsProvider? = null,
) : BaseViewModel(analytics) {

    private var _summary = MutableLiveData<SummaryModel>()
    var summary: LiveData<SummaryModel> = _summary

    private var _buys = MutableLiveData<MutableList<ExpenseModel>?>()
    var buys: LiveData<MutableList<ExpenseModel>?> = _buys

    fun loadSummary() {
        launch {
            getBuysSummaryUseCase()
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _summary.postValue(it) }
        }
    }

    fun loadBuys() {
        launch {
            getBuysUseCase()
                .onStart { setLoading() }
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _buys.postValue(it) }
        }
    }

    fun saveBuy(model: ExpenseModel) {
        launch {
            saveBuyUseCase(model.toBuy())
                .onStart { setLoading() }
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _buys.postValue(it) }
        }
    }

    fun setAllBuysActive(isActive: Boolean) {
        launch {
            activeBuysUseCase.setAllBuysActive(isActive)
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _buys.postValue(it) }
        }
    }

    fun switchActiveBuy(model: ExpenseModel) {
        launch {
            activeBuysUseCase.switchActiveBuy(model.toBuy())
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _buys.postValue(it) }
        }
    }

    fun deleteBuy(model: ExpenseModel) {
        launch {
            deleteBuysUseCase.deleteBuy(model.toBuy())
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _buys.postValue(it) }
        }
    }

    fun deleteAllBuys() {
        launch {
            deleteBuysUseCase.deleteAllBuys()
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _buys.postValue(it) }
        }
    }
}