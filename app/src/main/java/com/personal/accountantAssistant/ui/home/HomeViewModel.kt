package com.personal.accountantAssistant.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.domain.models.DashboardItemModel
import com.personal.accountantAssistant.domain.models.ExpensesValuesModel
import com.personal.accountantAssistant.domain.repository.BillsRepository
import com.personal.accountantAssistant.domain.repository.BuysRepository
import com.personal.accountantAssistant.domain.useCases.GetAvailableMoneyUseCase
import com.personal.accountantAssistant.domain.useCases.GetFirstDateUseCase
import com.personal.accountantAssistant.domain.useCases.GetLastDateUseCase
import com.personal.accountantAssistant.domain.useCases.SetPeriodDatesUseCase
import com.personal.accountantAssistant.extensions.onError
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.toUtcDate
import com.personal.accountantAssistant.extensions.toUtcTime
import com.personal.accountantAssistant.providers.AnalyticsProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*

class HomeViewModel(
    analytics: AnalyticsProvider?,
    val getFirstDate: GetFirstDateUseCase,
    val getLastDate: GetLastDateUseCase,
    val setPeriodDates: SetPeriodDatesUseCase,
    val getAvailableMoney: GetAvailableMoneyUseCase,
    private val buysRepository: BuysRepository,
    private val billsRepository: BillsRepository
) : BaseViewModel(analytics) {

    private val _periodDates = MutableLiveData<Pair<Date?, Date?>>()
    val periodDates: LiveData<Pair<Date?, Date?>> get() = _periodDates

    private val _availableMoney = MutableLiveData<BigDecimal>()
    val availableMoney: LiveData<BigDecimal> get() = _availableMoney

    private val _expensesValues = MutableLiveData<ExpensesValuesModel>()
    val expensesValues = _expensesValues

    private val _dashboardValues = MutableLiveData<List<DashboardItemModel>>()
    val dashboardValues = _dashboardValues

    fun loadPeriodDates() {
        launch {
            combine(getFirstDate(), getLastDate()) { init, end -> Pair(init, end) }
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

    fun loadExpenses() {
        val lastDate = _periodDates.value?.second
        launch {
            combine(
                buysRepository.getTotalValueUntil(lastDate),
                billsRepository.getTotalValueUntil(lastDate)
            ) { buys, bills ->
                val total = buys?.plus(bills.orZero())
                ExpensesValuesModel(buys, bills, total)
            }.onStart { setLoading() }
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

    fun postDashboardValues(list: List<DashboardItemModel>) {
        _dashboardValues.postValue(list)
    }

}