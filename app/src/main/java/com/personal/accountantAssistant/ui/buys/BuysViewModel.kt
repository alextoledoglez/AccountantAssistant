package com.personal.accountantAssistant.ui.buys

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.domain.models.BuyModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.ExpensesRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BuysViewModel(val repository: ExpensesRepository?) : BaseViewModel() {
    private var _buys = MutableLiveData<BuyModel>()
    var buys: LiveData<BuyModel> = _buys
    fun getBuys() = launch { repository?.getBuys()?.collect { _buys.postValue(it) } }
    fun setDefaultBuys() = launch { repository?.setDefaultBuys()?.collect() }
    fun updateExpense(model: ExpenseModel) = launch { repository?.updateExpense(model)?.collect() }
    fun deleteExpense(model: ExpenseModel) = launch { repository?.deleteExpense(model)?.collect() }
    fun deleteAllBuys() = launch { repository?.deleteAllBuys()?.collect() }
}