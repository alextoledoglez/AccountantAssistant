package com.personal.accountantAssistant.ui.bills

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

class BillsViewModel(val repository: ExpensesRepository?) : BaseViewModel() {

    private var _summary = MutableLiveData<SummaryModel>()
    var summary: LiveData<SummaryModel> = _summary

    private var _bills = MutableLiveData<MutableList<ExpenseModel>?>()
    var bills: LiveData<MutableList<ExpenseModel>?> = _bills

    private fun getExpenses() = _bills.value

    fun getBills() = launch {
        repository?.getBills()?.onStart { setLoading() }?.collect {
            _bills.postValue(it)
            setData()
        }
    }

    fun loadSummary(list: MutableList<ExpenseModel>?) {
        _summary.postValue(list?.toSummaryModel())
    }

    fun setDefaultBills() = launch {
        repository?.setDefaultBills()?.collect { _bills.postValue(it) }
    }

    fun setAllBillsActive(isActive: Boolean) = launch {
        repository?.setAllBillsActive(isActive)?.collect { _bills.postValue(it) }
    }

    fun activeExpense(model: ExpenseModel) = launch {
        val expenses = getExpenses()?.replaceActiveStateOf(model)
        repository?.updateExpense(model)?.collect { _bills.postValue(expenses) }
    }

    fun deleteExpense(model: ExpenseModel) = launch {
        repository?.deleteExpense(model)?.collect {
            val expenses = getExpenses()
            if (expenses?.remove(model).orFalse()) {
                _bills.postValue(expenses)
            }
        }
    }

    fun deleteAllBills() = launch {
        repository?.deleteAllBills()?.collect { _bills.postValue(it) }
    }
}