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
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*

class HomeViewModel(
    analytics: AnalyticsProvider?,
    val getFirstDate: GetFirstDateUseCase,
    val getLastDate: GetLastDateUseCase,
    val setPeriodDates: SetPeriodDatesUseCase,
    val getAvailableMoney: GetAvailableMoneyUseCase,
    private val buysRepository: BuysRepository?,
    private val billsRepository: BillsRepository?
) : BaseViewModel(analytics) {

    private val _dashboardValues = MutableLiveData<List<DashboardItemModel>>()
    val dashboardValues = _dashboardValues

    private val _expensesValues = MutableLiveData<ExpensesValuesModel>()
    val expensesValues = _expensesValues

    private val _periodValue = MutableLiveData<String>()
    val periodValue: LiveData<String> get() = _periodValue

    private val _availableMoney = MutableLiveData<BigDecimal>()
    val availableMoney: LiveData<BigDecimal> get() = _availableMoney

    private val _firstPeriodDate = MutableLiveData<Date?>()
    private val firstPeriodDate = _firstPeriodDate

    private val _lastPeriodDate = MutableLiveData<Date?>()
    private val lastPeriodDate = _lastPeriodDate

    fun isZeroLessThan(value: BigDecimal) = (value >= BigDecimal.ZERO)

    fun isExpensesLessThanAvailable(value: BigDecimal) = (value <= availableMoney.value.orZero())

    fun isExpensesMoreThanAvailable(value: BigDecimal) = !isExpensesLessThanAvailable(value)

    fun calculateExpenses() = launch {
        setLoading()
        val lastDate = getLastDate().singleOrNull()
        setPeriodDates(getFirstDate().singleOrNull(), lastDate)
        val buysExpenses = buysRepository?.getTotalValueUntil(lastDate)
            ?.onError { setMessage(it.message) }
            ?.singleOrNull().orZero()
        val billsExpenses = billsRepository?.getTotalValueUntil(lastDate)
            ?.onError { setMessage(it.message) }
            ?.singleOrNull().orZero()
        val totalExpenses = buysExpenses.plus(billsExpenses)
        _availableMoney.postValue(getAvailableMoney().singleOrNull())
        _expensesValues.postValue(ExpensesValuesModel(buysExpenses, billsExpenses, totalExpenses))
        setData()
    }

    fun getSelectedPeriod() = androidx.core.util.Pair(
        firstPeriodDate.value?.time.toUtcTime(), lastPeriodDate.value?.time.toUtcTime()
    )

    fun savePeriodDates(period: androidx.core.util.Pair<Long, Long>?) {
        period?.let { savePeriodDates(it.first.toUtcDate(), it.second.toUtcDate()) }
    }

    private fun savePeriodDates(firstDate: Date?, lastDate: Date?) {
        _firstPeriodDate.postValue(firstDate)
        _lastPeriodDate.postValue(lastDate)
        val period = "${firstDate.toDateStr()}${String.DASH_SEPARATOR}${lastDate.toDateStr()}"
        _periodValue.postValue(period)
        setPeriodDates(firstDate, lastDate)
    }

    fun postDashboardValues(list: List<DashboardItemModel>) {
        _dashboardValues.postValue(list)
    }

}