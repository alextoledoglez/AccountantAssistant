package com.personal.accountantAssistant.ui.bills

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.enums.ListNotifyTypes
import com.personal.accountantAssistant.domain.models.BillsModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.ExpensesRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class BillsViewModel(val repository: ExpensesRepository?) : BaseViewModel() {

    private var _bills = MutableLiveData<BillsModel>()
    var bills: LiveData<BillsModel> = _bills

    fun getBills() = launch {
        repository?.getBills()?.onStart { setLoading() }?.collect {
            _bills.postValue(it)
            setData()
        }
    }

    fun setDefaultBills() = launch { repository?.setDefaultBills()?.collect() }

    fun setAllBillsActive(isActive: Boolean) = launch {
        repository?.setAllBillsActive(isActive)?.collect {
            setListNotifier(ListNotifyTypes.UPDATE_ALL)
        }
    }

    fun updateExpense(position: Int, model: ExpenseModel) = launch {
        repository?.updateExpense(model)?.collect {
            setListNotifier(ListNotifyTypes.UPDATE, position)
        }
    }

    fun deleteExpense(position: Int, model: ExpenseModel) = launch {
        repository?.deleteExpense(model)?.collect {
            setListNotifier(ListNotifyTypes.DELETE, position)
        }
    }

    fun deleteAllBills() = launch {
        repository?.deleteAllBills()?.collect { setListNotifier(ListNotifyTypes.DELETE_ALL) }
    }
}