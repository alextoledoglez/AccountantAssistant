package com.personal.accountantAssistant.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.DatabaseManager
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType
import com.personal.accountantAssistant.domain.models.home.DashboardItemModel
import com.personal.accountantAssistant.domain.models.home.ExpensesValuesModel
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.utils.Constants
import com.personal.accountantAssistant.utils.DateUtils
import java.util.*

class HomeViewModel(
    private val localStorage: LocalStorage?, private val databaseManager: DatabaseManager?
) : BaseViewModel() {

    private val _dashboardValues = MutableLiveData<List<DashboardItemModel>>()
    val dashboardValues = _dashboardValues

    private val _expensesValues = MutableLiveData<ExpensesValuesModel>()
    val expensesValues = _expensesValues

    private val _periodValue = MutableLiveData<String>()
    val periodValue: LiveData<String> get() = _periodValue

    private val _availableMoney = MutableLiveData<Double>()
    val availableMoney: LiveData<Double> get() = _availableMoney

    private val _totalExpenses = MutableLiveData(Constants.DEFAULT_VALUE)
    val totalExpenses: LiveData<Double> get() = _totalExpenses

    private val _firstPeriodDate = MutableLiveData<Date?>(localStorage?.getFirstDate())
    private val firstPeriodDate = _firstPeriodDate

    private val _lastPeriodDate = MutableLiveData<Date?>(localStorage?.getLastDate())
    private val lastPeriodDate = _lastPeriodDate

    fun isZeroLessThan(value: Double?) = (value.orZero() >= Constants.DEFAULT_VALUE)

    fun isExpensesLessThanAvailable(value: Double?) =
        (availableMoney.value.orZero() >= value.orZero())

    fun isExpensesMoreThanAvailable(value: Double?): Boolean = !isExpensesLessThanAvailable(value)

    @RequiresApi(Build.VERSION_CODES.P)
    fun calculateExpenses() {

        setPeriodDates(localStorage?.getFirstDate(), localStorage?.getLastDate())

        val buysExpenses = databaseManager?.getExpensesTotalPriceUntil(
            ExpensesType.BUY, localStorage?.getLastDate()
        )

        val billsExpenses = databaseManager?.getExpensesTotalPriceUntil(
            ExpensesType.BILL, localStorage?.getLastDate()
        )

        _totalExpenses.postValue(buysExpenses?.plus(billsExpenses.orZero()))
        _availableMoney.postValue(localStorage?.getAvailableMoney())
        _expensesValues.postValue(
            ExpensesValuesModel(buysExpenses, billsExpenses, totalExpenses.value)
        )

    }

    fun savePeriodDates(period: androidx.core.util.Pair<Long, Long>?) {
        period?.let { savePeriodDates(Date(it.first), Date(it.second)) }
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

    fun getSelectedPeriod() = androidx.core.util.Pair(
        firstPeriodDate.value?.time, lastPeriodDate.value?.time
    )

    fun postDashboardValues(list: List<DashboardItemModel>) {
        _dashboardValues.postValue(list)
    }

    override fun onCleared() {
        super.onCleared()
    }
}