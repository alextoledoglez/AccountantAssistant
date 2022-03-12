package com.personal.accountantAssistant.ui.buys

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.enums.ListNotifyTypes
import com.personal.accountantAssistant.data.mappers.toBuysModel
import com.personal.accountantAssistant.domain.models.BuysModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.ExpensesRepository
import com.personal.accountantAssistant.extensions.orFalse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class BuysViewModel(val repository: ExpensesRepository?) : BaseViewModel() {

    private var _buys = MutableLiveData<BuysModel>()
    var buys: LiveData<BuysModel> = _buys

    private fun getExpenses() = _buys.value?.expenses

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
        repository?.setAllBuysActive(isActive)?.collect {
            val expenses = getExpenses()
            expenses?.forEach { it.isActive = isActive }
            _buys.postValue(expenses?.toBuysModel())
            setListNotifier(ListNotifyTypes.ACTIVE_ALL)
        }
    }

    fun updateExpense(position: Int, model: ExpenseModel) = launch {
        repository?.updateExpense(model)?.collect {
            val expenses = getExpenses()
            expenses?.filter { it.id == model.id }?.map { it.updateWith(model) }
            _buys.postValue(expenses?.toBuysModel())
            setListNotifier(ListNotifyTypes.UPDATE, position)
        }
    }

    fun deleteExpense(position: Int, model: ExpenseModel) = launch {
        repository?.deleteExpense(model)?.collect {
            val expenses = getExpenses()
            if (expenses?.remove(model).orFalse()) {
                _buys.postValue(expenses?.toBuysModel())
                setListNotifier(ListNotifyTypes.DELETE, position)
            }
        }
    }

    fun deleteAllBuys() = launch {
        repository?.deleteAllBuys()?.collect {
            _buys.value?.expenses?.clear()
            setListNotifier(ListNotifyTypes.DELETE_ALL)
        }
    }
}