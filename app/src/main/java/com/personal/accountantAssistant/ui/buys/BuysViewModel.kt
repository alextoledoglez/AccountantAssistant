package com.personal.accountantAssistant.ui.buys

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.mappers.replaceActiveStateOf
import com.personal.accountantAssistant.data.mappers.toSummaryModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.domain.repository.ExpensesRepository
import com.personal.accountantAssistant.extensions.orFalse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class BuysViewModel(val repository: ExpensesRepository?) : BaseViewModel() {

    private var _summary = MutableLiveData<SummaryModel>()
    var summary: LiveData<SummaryModel> = _summary

    private var _buys = MutableLiveData<MutableList<ExpenseModel>?>()
    var buys: LiveData<MutableList<ExpenseModel>?> = _buys

    private fun getExpenses() = _buys.value

    fun getBuys() = launch {
        repository?.getBuys()?.onStart { setLoading() }?.collect {
            _buys.postValue(it)
            setData()
        }
    }

    fun loadSummary(list: MutableList<ExpenseModel>?) {
        _summary.postValue(list?.toSummaryModel())
    }

    fun setDefaultBuys() = launch {
        repository?.setDefaultBuys()?.collect { _buys.postValue(it) }
    }

    fun setAllBuysActive(isActive: Boolean) = launch {
        repository?.setAllBuysActive(isActive)?.collect { _buys.postValue(it) }
    }

    fun activeExpense(model: ExpenseModel) = launch {
        val expenses = getExpenses()?.replaceActiveStateOf(model)
        repository?.updateExpense(model)?.collect { _buys.postValue(expenses) }
    }

    fun deleteExpense(model: ExpenseModel) = launch {
        repository?.deleteExpense(model)?.collect {
            val expenses = getExpenses()
            if (expenses?.remove(model).orFalse()) {
                _buys.postValue(expenses)
            }
        }
    }

    fun deleteAllBuys() = launch {
        repository?.deleteAllBuys()?.collect { _buys.postValue(it) }
    }
}