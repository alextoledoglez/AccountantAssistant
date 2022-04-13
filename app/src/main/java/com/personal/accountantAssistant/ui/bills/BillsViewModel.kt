package com.personal.accountantAssistant.ui.bills

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.mappers.toBill
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.domain.repository.BillsRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.launch

class BillsViewModel(private val repository: BillsRepository?) : BaseViewModel() {

    private var _summary = MutableLiveData<SummaryModel>()
    var summary: LiveData<SummaryModel> = _summary

    private var _bills = MutableLiveData<MutableList<ExpenseModel>?>()
    var bills: LiveData<MutableList<ExpenseModel>?> = _bills

    fun getBills() = launch {
        repository?.getBills()?.onStart { setLoading() }?.collect {
            _bills.postValue(it)
            setData()
        }
    }

    fun loadSummary() = launch {
        _summary.postValue(repository?.getSummary()?.singleOrNull())
    }

    fun saveBill(model: ExpenseModel) = launch {
        repository?.saveBill(model.toBill())?.onStart { setLoading() }?.collect {
            _bills.postValue(it)
            setData()
        }
    }

    fun setDefaultBills() = launch {
        repository?.setDefaultBills()?.collect { _bills.postValue(it) }
    }

    fun setAllBillsActive(isActive: Boolean) = launch {
        repository?.setAllBillsActive(isActive)?.collect { _bills.postValue(it) }
    }

    fun switchActiveBill(model: ExpenseModel) = launch {
        repository?.switchActiveBill(model.toBill())?.collect { _bills.postValue(it) }
    }

    fun deleteBill(model: ExpenseModel) = launch {
        repository?.deleteBill(model.toBill())?.collect { _bills.postValue(it) }
    }

    fun deleteAllBills() = launch {
        repository?.deleteAllBills()?.collect { _bills.postValue(it) }
    }
}