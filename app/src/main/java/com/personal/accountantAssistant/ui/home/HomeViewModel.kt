package com.personal.accountantAssistant.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.DashboardItemModel
import com.personal.accountantAssistant.domain.models.ExpensesValuesModel
import com.personal.accountantAssistant.domain.repository.ExpensesRepository
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.utils.DateUtils
import com.personal.accountantAssistant.utils.DateUtils.toUtcDate
import com.personal.accountantAssistant.utils.DateUtils.toUtcPair
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*

class HomeViewModel(
    private val localStorage: LocalStorage?, private val repository: ExpensesRepository?
) : BaseViewModel() {

    private val _dashboardValues = MutableLiveData<List<DashboardItemModel>>()
    val dashboardValues = _dashboardValues

    private val _expensesValues = MutableLiveData<ExpensesValuesModel>()
    val expensesValues = _expensesValues

    private val _periodValue = MutableLiveData<String>()
    val periodValue: LiveData<String> get() = _periodValue

    private val _availableMoney = MutableLiveData<BigDecimal>()
    val availableMoney: LiveData<BigDecimal> get() = _availableMoney

    private val _firstPeriodDate = MutableLiveData<Date?>(localStorage?.getFirstDate())
    private val firstPeriodDate = _firstPeriodDate

    private val _lastPeriodDate = MutableLiveData<Date?>(localStorage?.getLastDate())
    private val lastPeriodDate = _lastPeriodDate

    fun isZeroLessThan(value: BigDecimal?) = (value.orZero() >= BigDecimal.ZERO)

    fun isExpensesLessThanAvailable(value: BigDecimal?) =
        (availableMoney.value.orZero() >= value.orZero())

    fun isExpensesMoreThanAvailable(value: BigDecimal?): Boolean =
        !isExpensesLessThanAvailable(value)

    fun calculateExpenses() = launch {
        setLoading()
        setPeriodDates(localStorage?.getFirstDate(), localStorage?.getLastDate())

        val buysExpenses = repository?.getTotalPriceUntil(
            ExpensesType.BUY, localStorage?.getLastDate()
        )?.singleOrNull().orZero()

        val billsExpenses = repository?.getTotalPriceUntil(
            ExpensesType.BILL, localStorage?.getLastDate()
        )?.singleOrNull().orZero()

        val totalExpenses = buysExpenses.plus(billsExpenses)
        _availableMoney.postValue(localStorage?.getAvailableMoney())
        _expensesValues.postValue(ExpensesValuesModel(buysExpenses, billsExpenses, totalExpenses))
        setData()
    }

    fun getSelectedPeriod() = toUtcPair(firstPeriodDate.value?.time, lastPeriodDate.value?.time)

    fun savePeriodDates(period: androidx.core.util.Pair<Long, Long>?) {
        period?.let { savePeriodDates(toUtcDate(it.first), toUtcDate(it.second)) }
    }

    private fun setPeriodDates(firstDate: Date?, lastDate: Date?) {
        _firstPeriodDate.postValue(firstDate)
        _lastPeriodDate.postValue(lastDate)
        _periodValue.postValue(DateUtils.toPeriodStr(firstDate, lastDate))
    }

    private fun savePeriodDates(firstDate: Date?, lastDate: Date?) {
        setPeriodDates(firstDate, lastDate)
        localStorage?.setPeriodDates(firstDate, lastDate)
    }

    fun postDashboardValues(list: List<DashboardItemModel>) {
        _dashboardValues.postValue(list)
    }

    override fun onCleared() {
        super.onCleared()
    }
}