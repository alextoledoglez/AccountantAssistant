package com.personal.accountantAssistant.ui.bills

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.domain.models.BillModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.ExpensesRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class BillsViewModel(val repository: ExpensesRepository?) : BaseViewModel() {

    private var _bills = MutableLiveData<BillModel>()
    var bills: LiveData<BillModel> = _bills

    fun getBills() = launch {
        repository?.getBills()?.onStart { setLoading() }?.collect {
            _bills.postValue(it)
            setData()
        }
    }

    fun setDefaultBills() = launch { repository?.setDefaultBills()?.collect() }

    fun setAllBillsActive(isActive: Boolean) = launch {
        repository?.setAllBillsActive(isActive)?.collect { getBills() }
    }

    fun updateExpense(model: ExpenseModel) = launch { repository?.updateExpense(model)?.collect() }
    fun deleteExpense(model: ExpenseModel) = launch { repository?.deleteExpense(model)?.collect() }
    fun deleteAllBills() = launch { repository?.deleteAllBills()?.collect() }
}