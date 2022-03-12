package com.personal.accountantAssistant.ui.bills

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.enums.ListNotifyTypes
import com.personal.accountantAssistant.data.mappers.toBillsModel
import com.personal.accountantAssistant.domain.models.BillsModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.ExpensesRepository
import com.personal.accountantAssistant.extensions.orFalse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class BillsViewModel(val repository: ExpensesRepository?) : BaseViewModel() {

    private var _bills = MutableLiveData<BillsModel>()
    var bills: LiveData<BillsModel> = _bills

    private fun getExpenses() = _bills.value?.expenses

    fun getBills() = launch {
        repository?.getBills()?.onStart { setLoading() }?.collect {
            _bills.postValue(it)
            setData()
        }
    }

    fun setDefaultBills() = launch {
        repository?.setDefaultBills()?.collect { setListNotifier(ListNotifyTypes.INSERT_ALL) }
    }

    fun setAllBillsActive(isActive: Boolean) = launch {
        repository?.setAllBillsActive(isActive)?.collect {
            val expenses = getExpenses()
            expenses?.forEach { it.isActive = isActive }
            _bills.postValue(expenses?.toBillsModel())
            setListNotifier(ListNotifyTypes.ACTIVE_ALL)
        }
    }

    fun updateExpense(position: Int, model: ExpenseModel) = launch {
        repository?.updateExpense(model)?.collect {
            val expenses = getExpenses()
            expenses?.filter { it.id == model.id }?.map { it.updateWith(model) }
            _bills.postValue(expenses?.toBillsModel())
            setListNotifier(ListNotifyTypes.UPDATE, position)
        }
    }

    fun deleteExpense(position: Int, model: ExpenseModel) = launch {
        repository?.deleteExpense(model)?.collect {
            val expenses = getExpenses()
            if (expenses?.remove(model).orFalse()) {
                _bills.postValue(expenses?.toBillsModel())
                setListNotifier(ListNotifyTypes.DELETE, position)
            }
        }
    }

    fun deleteAllBills() = launch {
        repository?.deleteAllBills()?.collect {
            _bills.value?.expenses?.clear()
            setListNotifier(ListNotifyTypes.DELETE_ALL)
        }
    }
}