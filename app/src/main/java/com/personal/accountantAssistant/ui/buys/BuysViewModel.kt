package com.personal.accountantAssistant.ui.buys

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.mappers.toBuy
import com.personal.accountantAssistant.data.mappers.toSummaryModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.domain.repository.BuysRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class BuysViewModel(private val repository: BuysRepository?) : BaseViewModel() {

    private var _summary = MutableLiveData<SummaryModel>()
    var summary: LiveData<SummaryModel> = _summary

    private var _buys = MutableLiveData<MutableList<ExpenseModel>?>()
    var buys: LiveData<MutableList<ExpenseModel>?> = _buys

    fun getBuys() = launch {
        repository?.getBuys()?.onStart { setLoading() }?.collect {
            _buys.postValue(it)
            setData()
        }
    }

    fun loadSummary(list: MutableList<ExpenseModel>?) {
        _summary.postValue(list?.toSummaryModel())
    }

    fun editExpense(model: ExpenseModel) = launch {
        repository?.saveBuy(model.toBuy())?.onStart { setLoading() }?.collect {
            _buys.postValue(it)
            setData()
        }
    }

    fun setDefaultBuys() = launch {
        repository?.setDefaultBuys()?.collect { _buys.postValue(it) }
    }

    fun setAllBuysActive(isActive: Boolean) = launch {
        repository?.setAllBuysActive(isActive)?.collect { _buys.postValue(it) }
    }

    fun switchActiveExpense(model: ExpenseModel) = launch {
        repository?.switchActiveBuy(model.toBuy())?.collect { _buys.postValue(it) }
    }

    fun deleteExpense(model: ExpenseModel) = launch {
        repository?.deleteBuy(model.toBuy())?.collect { _buys.postValue(it) }
    }

    fun deleteAllBuys() = launch {
        repository?.deleteAllBuys()?.collect { _buys.postValue(it) }
    }
}