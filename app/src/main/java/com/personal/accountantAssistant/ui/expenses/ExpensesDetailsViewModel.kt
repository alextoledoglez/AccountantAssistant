package com.personal.accountantAssistant.ui.expenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.repository.ExpensesRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ExpensesDetailsViewModel(private val repository: ExpensesRepository?) : BaseViewModel() {

    private var _isSaved = MutableLiveData(false)
    var isSaved: LiveData<Boolean> = _isSaved

    fun saveExpense(model: ExpenseModel) = launch {
        repository?.saveExpense(model)?.collect { _isSaved.postValue(true) }
    }

}