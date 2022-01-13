package com.personal.accountantAssistant.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.DatabaseManager
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType
import com.personal.accountantAssistant.domain.models.home.HomeExpensesModel
import com.personal.accountantAssistant.domain.models.home.SummaryItemModel
import com.personal.accountantAssistant.domain.models.home.SummaryModel
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.utils.Constants
import com.personal.accountantAssistant.utils.DateUtils
import com.personal.accountantAssistant.utils.DateUtils.toDate
import com.personal.accountantAssistant.utils.NumberUtils
import java.util.*

class HomeViewModel(
    private val localStorage: LocalStorage?, private val databaseManager: DatabaseManager?
) : BaseViewModel() {

    var availableMoney: Double? = 00.00

    private val _expensesValues = MutableLiveData<HomeExpensesModel>()
    val expensesValues = _expensesValues

    private val _totalExpensesValue = MutableLiveData<Double>()
    val totalExpensesValue = _totalExpensesValue

    private val _summaryValues = MutableLiveData<SummaryModel>()
    val summaryValues = _summaryValues

    private val _periodText = MutableLiveData<String>()
    val periodText: LiveData<String> get() = _periodText

    private val _periodDays = MutableLiveData<Int>()
    private val periodDays: LiveData<Int> get() = _periodDays

    private val _firstPeriodDate = MutableLiveData<Date?>(localStorage?.getFirstDate())
    private val firstPeriodDate = _firstPeriodDate

    private val _lastPeriodDate = MutableLiveData<Date?>(localStorage?.getLastDate())
    private val lastPeriodDate = _lastPeriodDate

    fun isZeroLessThan(value: Double?) = (value.orZero() >= Constants.DEFAULT_VALUE)

    fun isExpensesLessThanAvailable(value: Double?) = (availableMoney.orZero() >= value.orZero())

    fun isExpensesMoreThanAvailable(value: Double?): Boolean = !isExpensesLessThanAvailable(value)

    @RequiresApi(Build.VERSION_CODES.P)
    fun calculateExpenses() {

        availableMoney = localStorage?.getAvailableMoney()?.toDouble()
        setPeriodDates(localStorage?.getFirstDate(), localStorage?.getLastDate())

        val days = periodDays.value?.let { if (it > 0) it else 1 } ?: run { 1 }
        val dailyExpenses = NumberUtils.roundTo(availableMoney?.div(days))

        val buysExpenses = databaseManager?.getExpensesTotalPriceUntil(
            ExpensesType.BUY, localStorage?.getLastDate()
        )

        val billsExpenses = databaseManager?.getExpensesTotalPriceUntil(
            ExpensesType.BILL, localStorage?.getLastDate()
        )

        _expensesValues.postValue(HomeExpensesModel(dailyExpenses, buysExpenses, billsExpenses))
        val totalExpenses = buysExpenses?.plus(billsExpenses.orZero())?.plus(dailyExpenses)
        _totalExpensesValue.postValue(totalExpenses.orZero())
    }

    fun savePeriodDates(firstTimeInMillis: Long?, lastTimeInMillis: Long?) {
        savePeriodDates(toDate(firstTimeInMillis), toDate(lastTimeInMillis))
    }

    private fun setPeriodDates(firstDate: Date?, lastDate: Date?) {
        _firstPeriodDate.postValue(firstDate)
        _lastPeriodDate.postValue(lastDate)
        _periodText.value = DateUtils.toPeriodStr(firstDate, lastDate)
        _periodDays.value = DateUtils.getDaysBetween(firstDate, lastDate)
    }

    private fun savePeriodDates(firstDate: Date?, lastDate: Date?) {
        setPeriodDates(firstDate, lastDate)
        localStorage?.setPeriodDates(firstDate, lastDate)
    }

    fun getSelectedPeriod() = androidx.core.util.Pair(
        firstPeriodDate.value?.time, lastPeriodDate.value?.time
    )

    fun setHomeSummaryModel(
        available: SummaryItemModel?,
        total: SummaryItemModel?,
        gainOrNeeded: SummaryItemModel?
    ) {
        _summaryValues.postValue(SummaryModel(available, total, gainOrNeeded))
    }

    override fun onCleared() {
        super.onCleared()
    }
}