package com.personal.accountantAssistant.ui.buys

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.enums.ListNotifyTypes
import com.personal.accountantAssistant.domain.models.BuysModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.ExpensesRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class BuysViewModel(val repository: ExpensesRepository?) : BaseViewModel() {

    private var _buys = MutableLiveData<BuysModel>()
    var buys: LiveData<BuysModel> = _buys

    fun getBuys() = launch {
        repository?.getBuys()?.onStart { setLoading() }?.collect {
            _buys.postValue(it)
            setData()
        }
    }

    fun setDefaultBuys() = launch {
        repository?.setDefaultBuys()?.collect {
            setListNotifier(ListNotifyTypes.INSERT_ALL)
        }
    }

    fun setAllBuysActive(isActive: Boolean) = launch {
        _buys.value?.expenses?.forEach { it.isActive = isActive }
        repository?.setAllBuysActive(isActive)?.collect {
            setListNotifier(ListNotifyTypes.ACTIVE_ALL)
        }
    }

    fun updateExpense(position: Int, model: ExpenseModel) = launch {
        repository?.updateExpense(model)?.collect {
            setListNotifier(ListNotifyTypes.UPDATE, position)
        }
    }

    fun deleteExpense(position: Int, model: ExpenseModel) = launch {
        repository?.deleteExpense(model)?.collect {
            _buys.value?.expenses?.remove(model)
            setListNotifier(ListNotifyTypes.DELETE, position)
        }
    }

    fun deleteAllBuys() = launch {
        repository?.deleteAllBuys()?.collect {
            _buys.value?.expenses?.clear()
            setListNotifier(ListNotifyTypes.DELETE_ALL)
        }
    }
}