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
import com.personal.accountantAssistant.domain.models.home.DashboardModel
import com.personal.accountantAssistant.domain.models.home.ExpensesItemsModel
import com.personal.accountantAssistant.domain.models.home.ExpensesValuesModel
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.utils.Constants
import com.personal.accountantAssistant.utils.DateUtils
import java.util.*

class HomeViewModel(
    private val localStorage: LocalStorage?, private val databaseManager: DatabaseManager?
) : BaseViewModel() {

    var availableMoney: Double? = 00.00

    private val _dashboardValues = MutableLiveData<DashboardModel>()
    val dashboardValues = _dashboardValues

    private val _expensesValues = MutableLiveData<ExpensesValuesModel>()
    val expensesValues = _expensesValues

    private val _periodValue = MutableLiveData<String>()
    val periodValue: LiveData<String> get() = _periodValue

    private val _firstPeriodDate = MutableLiveData<Date?>(localStorage?.getFirstDate())
    private val firstPeriodDate = _firstPeriodDate

    private val _lastPeriodDate = MutableLiveData<Date?>(localStorage?.getLastDate())
    private val lastPeriodDate = _lastPeriodDate

    fun isZeroLessThan(value: Double?) = (value.orZero() >= Constants.DEFAULT_VALUE)

    fun isExpensesLessThanAvailable(value: Double?) = (availableMoney.orZero() >= value.orZero())

    fun isExpensesMoreThanAvailable(value: Double?): Boolean = !isExpensesLessThanAvailable(value)

    @RequiresApi(Build.VERSION_CODES.P)
    fun calculateExpenses() {

        availableMoney = localStorage?.getAvailableMoney()
        setPeriodDates(localStorage?.getFirstDate(), localStorage?.getLastDate())

        val buys = databaseManager?.getExpensesTotalPriceUntil(
            ExpensesType.BUY, localStorage?.getLastDate()
        )

        val bills = databaseManager?.getExpensesTotalPriceUntil(
            ExpensesType.BILL, localStorage?.getLastDate()
        )

        val total = buys?.plus(bills.orZero())
        _expensesValues.postValue(ExpensesValuesModel(buys, bills, total))
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

    fun postDashboardValues(
        available: DashboardItemModel?,
        expensesItems: ExpensesItemsModel?,
        gainOrNeeded: DashboardItemModel?
    ) {
        _dashboardValues.postValue(DashboardModel(available, expensesItems, gainOrNeeded))
    }

    override fun onCleared() {
        super.onCleared()
    }
}