package com.personal.accountantAssistant.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.domain.models.ExpensesValuesModel
import com.personal.accountantAssistant.domain.useCases.home.GetAvailableMoneyUseCase
import com.personal.accountantAssistant.domain.useCases.home.GetExpensesUseCase
import com.personal.accountantAssistant.domain.useCases.home.GetPeriodDatesUseCase
import com.personal.accountantAssistant.domain.useCases.home.SetPeriodDatesUseCase
import com.personal.accountantAssistant.extensions.onError
import com.personal.accountantAssistant.extensions.toUtcDate
import com.personal.accountantAssistant.extensions.toUtcTime
import com.personal.accountantAssistant.providers.AnalyticsProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Date

class HomeViewModel(
    val getPeriodDates: GetPeriodDatesUseCase,
    val setPeriodDates: SetPeriodDatesUseCase,
    val getAvailableMoney: GetAvailableMoneyUseCase,
    val getExpenses: GetExpensesUseCase,
    analytics: AnalyticsProvider? = null,
) : BaseViewModel(analytics) {

    private val _periodDates = MutableLiveData<Pair<Date?, Date?>>()
    val periodDates: LiveData<Pair<Date?, Date?>> get() = _periodDates

    private val _availableMoney = MutableLiveData<BigDecimal>()
    val availableMoney: LiveData<BigDecimal> get() = _availableMoney

    private val _expensesValues = MutableLiveData<ExpensesValuesModel>()
    val expensesValues = _expensesValues

    fun loadPeriodDates() {
        launch {
            getPeriodDates()
                .onStart { setLoading() }
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _periodDates.postValue(it) }
        }
    }

    fun loadAvailableMoney() {
        launch {
            getAvailableMoney()
                .onStart { setLoading() }
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _availableMoney.postValue(it) }
        }
    }

    fun loadExpenses(period: Pair<Date?, Date?>, availableMoney: BigDecimal?) {
        launch {
            getExpenses(period, availableMoney)
                .onStart { setLoading() }
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _expensesValues.postValue(it) }
        }
    }

    fun getSelectedPeriod(): androidx.core.util.Pair<Long?, Long?> = _periodDates.value.let {
        androidx.core.util.Pair(it?.first?.time.toUtcTime(), it?.second?.time.toUtcTime())
    }

    fun savePeriodDates(period: androidx.core.util.Pair<Long, Long>?) {
        launch {
            setPeriodDates(period?.first.toUtcDate(), period?.second.toUtcDate())
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect()
        }
    }
}