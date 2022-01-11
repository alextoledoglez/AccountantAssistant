package com.personal.accountantAssistant.ui.home

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.data.DatabaseManager
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.domain.models.home.SummaryModel
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType
import com.personal.accountantAssistant.utils.DateUtils
import com.personal.accountantAssistant.utils.DateUtils.toDate
import com.personal.accountantAssistant.utils.NumberUtils
import java.util.*
import kotlin.math.abs

class HomeViewModel(
    private val localStorage: LocalStorage?, private val databaseManager: DatabaseManager?
) : BaseViewModel() {

    private var model: HomeModel? = null

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

    private fun isMoreThanAvailableMoney(value: String): Boolean? =
        localStorage?.getAvailableMoney()
            ?.let { model?.isMoreThanAvailableMoney(value.toDouble(), it) }

    private fun isMoreThanOrEqualTo(available: Double?, total: Double?) =
        (available.orZero() >= total.orZero())

    private fun isMoreThanOrEqualToZero(value: String) = (value.toDouble() >= 0)

    private fun setupCardImageView(cardImageView: ImageView) = cardImageView.apply {
        model?.imageResource?.let { setImageResource(it) }
        visibility = if (model?.hideImageView == true) View.GONE else View.VISIBLE
    }

    private fun setupCardTitleTextView(tvTitle: TextView) = model?.apply {
        tvTitle.text = cardTitle
        fontColor.let { tvTitle.setTextColor(it) }
        backgroundColor?.let { tvTitle.setBackgroundColor(it) }
        textSize?.let { tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.toFloat()) }
    }

    private fun setupCardSubtitleTextView(value: String, tvSubtitle: TextView) = tvSubtitle.apply {
        text = abs(value.toDouble()).toString()
        model?.fontColor?.let { setTextColor(it) }
    }

    private fun fillDashBoardCard(root: View, idResource: Int, cardTextValue: String) {

        val rootView: View = root.findViewById(idResource)
        val cardImageView = rootView.findViewById<ImageView>(R.id.ivCardImage)
        val cardTitleTextView = rootView.findViewById<TextView>(R.id.tvCardTitle)
        val cardSubtitleTextView = rootView.findViewById<TextView>(R.id.tvCardSubtitle)

        val isMoreThanAvailableMoney = isMoreThanAvailableMoney(cardTextValue)
        model?.initializeTheme(isMoreThanAvailableMoney)

        when (idResource) {
            R.id.daily_card -> model?.setupDailyImageCardTitleBy(isMoreThanAvailableMoney)
            R.id.buy_card -> model?.setupBuysImageCardTitleBy(isMoreThanAvailableMoney)
            R.id.bill_card -> model?.setupBillsImageCardTitleBy(isMoreThanAvailableMoney)
        }

        setupCardImageView(cardImageView)
        setupCardTitleTextView(cardTitleTextView)
        setupCardSubtitleTextView(cardTextValue, cardSubtitleTextView)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun calculateExpenses(context: Context?, rootView: View) {

        model = context?.let { HomeModel(it) }

        val availableMoney = localStorage?.getAvailableMoney()
        setPeriodDates(localStorage?.getFirstDate(), localStorage?.getLastDate())

        val days = periodDays.value?.let { if (it > 0) it else 1 } ?: run { 1 }
        val dailyExpenses = NumberUtils.roundTo(availableMoney?.toDouble()?.div(days))
        fillDashBoardCard(rootView, R.id.daily_card, dailyExpenses.toString())

        val buysExpenses = databaseManager?.getExpensesTotalPriceUntil(
            ExpensesType.BUY, localStorage?.getLastDate()
        )
        fillDashBoardCard(rootView, R.id.buy_card, buysExpenses.toString())

        val billsExpenses = databaseManager?.getExpensesTotalPriceUntil(
            ExpensesType.BILL, localStorage?.getLastDate()
        )
        fillDashBoardCard(rootView, R.id.bill_card, billsExpenses.toString())

        val expenses = buysExpenses?.plus(billsExpenses.orZero())?.plus(dailyExpenses)
        val totalExpenses = NumberUtils.roundTo(expenses)
        val needed = NumberUtils.roundTo(availableMoney?.minus(totalExpenses))
        val isMoreThanOrEqualToExpenses =
            isMoreThanOrEqualTo(availableMoney?.toDouble(), totalExpenses)
        val isMoreThanAvailableMoney = isMoreThanAvailableMoney(expenses.toString())
        val isMoreThanOrEqualToZero = isMoreThanOrEqualToZero(needed.toString())

        summaryValues.postValue(
            SummaryModel(
                availableMoney.orZero(),
                model?.cardTitles?.available,
                model?.getAvailableBackgroundColorBy(isMoreThanOrEqualToExpenses),
                totalExpenses.toFloat(),
                model?.cardTitles?.total,
                model?.getTotalBackgroundColorBy(isMoreThanAvailableMoney),
                needed.toFloat(),
                model?.getNeededTitleBy(isMoreThanOrEqualToZero),
                model?.getNeededBackgroundColorBy(isMoreThanOrEqualToZero)
            )
        )
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

    fun getSelectedPeriod() =
        androidx.core.util.Pair(firstPeriodDate.value?.time, lastPeriodDate.value?.time)

    override fun onCleared() {
        super.onCleared()
    }
}