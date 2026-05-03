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
import java.math.BigDecimal

class BuysViewModel(
    private val getBuysUseCase: GetBuysUseCase,
    private val saveBuyUseCase: SaveBuyUseCase,
    private val activeBuysUseCase: ActiveBuysUseCase,
    private val deleteBuysUseCase: DeleteBuysUseCase,
    analytics: AnalyticsProvider? = null
) : BaseViewModel(analytics) {

    private val _summary = MutableLiveData<SummaryModel>()
    val summary: LiveData<SummaryModel> = _summary

    private val _buys = MutableLiveData<MutableList<ExpenseModel>?>()
    val buys: LiveData<MutableList<ExpenseModel>?> = _buys

    private fun postBuys(list: MutableList<ExpenseModel>?) {
        _buys.postValue(list)
        val activeItems = list?.filter { it.isActive }.orEmpty()
        val activeItemsAmount = activeItems.fold(BigDecimal.ZERO) { acc, item ->
            acc.add(item.calculateTotalValue())
        }
        _summary.postValue(SummaryModel(activeCount = activeItems.size, total = activeItemsAmount))
    }

    fun loadBuys() {
        launch {
            getBuysUseCase()
                .onStart { setLoading() }
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { postBuys(list = it) }
        }
    }

    fun saveBuy(model: ExpenseModel) {
        launch {
            saveBuyUseCase(model.toBuy())
                .onStart { setLoading() }
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { postBuys(list = it) }
        }
    }

    fun setAllBuysActive(isActive: Boolean) {
        launch {
            activeBuysUseCase.setAllBuysActive(isActive)
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { postBuys(list = it) }
        }
    }

    fun switchActiveBuy(model: ExpenseModel) {
        launch {
            activeBuysUseCase.switchActiveBuy(model.toBuy())
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { postBuys(list = it) }
        }
    }

    fun deleteBuy(model: ExpenseModel) {
        launch {
            deleteBuysUseCase.deleteBuy(model.toBuy())
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { postBuys(list = it) }
        }
    }

    fun deleteAllBuys() {
        launch {
            deleteBuysUseCase.deleteAllBuys()
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { postBuys(list = it) }
        }
    }
}