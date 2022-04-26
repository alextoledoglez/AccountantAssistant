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
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.providers.AnalyticsProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
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

    private val _firstDate = MutableLiveData<Date?>()
    private val firstDate = _firstDate

    private val _lastDate = MutableLiveData<Date?>()
    private val lastDate = _lastDate

    private val _periodValue = MutableLiveData<String>()
    val periodValue: LiveData<String> get() = _periodValue

    private val _availableMoney = MutableLiveData<BigDecimal>()
    val availableMoney: LiveData<BigDecimal> get() = _availableMoney

    private val _expensesValues = MutableLiveData<ExpensesValuesModel>()
    val expensesValues = _expensesValues

    private val _dashboardValues = MutableLiveData<List<DashboardItemModel>>()
    val dashboardValues = _dashboardValues

    fun isZeroLessThan(value: BigDecimal) = (value >= BigDecimal.ZERO)

    fun isExpensesLessThanAvailable(value: BigDecimal) = (value <= availableMoney.value.orZero())

    fun isExpensesMoreThanAvailable(value: BigDecimal) = !isExpensesLessThanAvailable(value)

    fun calculateExpenses() {
        launch {
            setLoading()

            combine(getFirstDate(), getLastDate()) { init, end ->
                val period = "${init.toDateStr()}${String.DASH_SEPARATOR}${end.toDateStr()}"
                _firstDate.postValue(init)
                _lastDate.postValue(end)
                _periodValue.postValue(period)
            }.onError { setMessage(it.message) }.collect()

            getAvailableMoney()
                .onError { setMessage(it.message) }
                .collect { _availableMoney.postValue(it) }

            combine(
                buysRepository.getTotalValueUntil(lastDate.value),
                billsRepository.getTotalValueUntil(lastDate.value)
            ) { buys, bills ->
                val total = buys?.plus(bills.orZero())
                val expenses = ExpensesValuesModel(buys, bills, total)
                _expensesValues.postValue(expenses)
            }.onError { setMessage(it.message) }.collect()

            setData()
        }
    }

    fun getSelectedPeriod() = androidx.core.util.Pair(
        firstDate.value?.time.toUtcTime(), lastDate.value?.time.toUtcTime()
    )

    fun savePeriodDates(period: androidx.core.util.Pair<Long, Long>?) {
        period?.let {
            val init = it.first.toUtcDate()
            val end = it.second.toUtcDate()
            launch {
                setPeriodDates(init, end).collect {
                    _firstDate.postValue(init)
                    _lastDate.postValue(end)
                }
            }
        }
    }

    fun postDashboardValues(list: List<DashboardItemModel>) {
        _dashboardValues.postValue(list)
    }

}