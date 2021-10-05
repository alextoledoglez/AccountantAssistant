package com.personal.accountantAssistant.ui.home

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.core.BaseViewModel
import com.personal.accountantAssistant.core.extensions.toValue
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import com.personal.accountantAssistant.utils.Constants.STR_DEFAULT_MONETARY_VALUE
import com.personal.accountantAssistant.utils.DateUtils
import com.personal.accountantAssistant.utils.NumberUtils
import com.personal.accountantAssistant.utils.PaymentsFragmentsUtils
import kotlin.math.abs

class HomeViewModel(private val localStorage: LocalStorage?) : BaseViewModel() {

    private var model: HomeModel? = null

    private val _availableMoney = MutableLiveData<String>()
    val availableMoney: LiveData<String> get() = _availableMoney

    private val _periodText = MutableLiveData<String>()
    val periodText: LiveData<String> get() = _periodText

    private val _periodDays = MutableLiveData<Int>()
    val periodDays: LiveData<Int> get() = _periodDays

    private fun isMoreThanAvailableMoney(value: String): Boolean? =
        localStorage?.getAvailableMoney()
            ?.let { model?.isMoreThanAvailableMoney(value.toDouble(), it) }

    private fun isMoreThanOrEqualToZero(value: String) = (value.toDouble() >= 0)

    private fun setCardTitleTextViewStyle(tvTitle: TextView) = model?.apply {
        tvTitle.text = cardTitle
        fontColor.let { tvTitle.setTextColor(it) }
        backgroundColor?.let { tvTitle.setBackgroundColor(it) }
        textSize?.let { tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.toFloat()) }
    }

    private fun hideTitleCardViewImage(imageView: ImageView) {
        imageView.visibility = View.GONE
    }

    private fun fillDashBoardCard(root: View, idResource: Int, cardTextValue: String) {

        val cardView: CardView = root.findViewById(idResource)
        val cardTitleTextView = cardView.findViewById<TextView>(R.id.card_title)
        val cardSubtitleTextView = cardView.findViewById<TextView>(R.id.card_sub_title)
        val imageView = cardView.findViewById<ImageView>(R.id.card_image).also {
            it.visibility = View.VISIBLE
        }

        val isMoreThanOrEqualToZero = isMoreThanOrEqualToZero(cardTextValue)
        val isMoreThanAvailableMoney = isMoreThanAvailableMoney(cardTextValue)
        model?.initializeBy(idResource, isMoreThanOrEqualToZero, isMoreThanAvailableMoney)

        when (idResource) {
            R.id.available_card -> {
                model?.setupAvailableCardTitleBy(isMoreThanAvailableMoney)
                hideTitleCardViewImage(imageView)
            }
            R.id.needed_card -> {
                model?.setupNeededCardTitleBy(isMoreThanOrEqualToZero)
                hideTitleCardViewImage(imageView)
            }
            R.id.total_card -> {
                model?.setupTotalCardTitleBy(isMoreThanAvailableMoney)
                hideTitleCardViewImage(imageView)
            }
            R.id.daily_card -> model?.setupDailyImageCardTitleBy(isMoreThanAvailableMoney)
            R.id.buy_card -> model?.setupBuysImageCardTitleBy(isMoreThanAvailableMoney)
            R.id.bill_card -> model?.setupBillsImageCardTitleBy(isMoreThanAvailableMoney)
        }

        setCardTitleTextViewStyle(cardTitleTextView)
        model?.imageResource?.let { imageView.setImageResource(it) }

        if (idResource != R.id.available_card) {
            cardSubtitleTextView.text = abs(cardTextValue.toDouble()).toString()
            model?.fontColor?.let { cardSubtitleTextView.setTextColor(it) }
        }
    }

    private fun updateAvailableMoney(value: Float? = null) = value?.let {
        localStorage?.setAvailableMoney(it)
        setAvailableMoney()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun calculateExpenses(context: Context?, activity: Activity?, rootView: View) {

        model = context?.let { HomeModel(it) }

        _availableMoney.value = localStorage?.getAvailableMoney().toString()

        val firstDate = localStorage?.getFirstDate()
        val lastDate = localStorage?.getLastDate()
        _periodText.value = DateUtils.toPeriodStr(firstDate, lastDate)
        _periodDays.value = DateUtils.getDaysBetween(firstDate, lastDate)

        fillDashBoardCard(rootView, R.id.available_card, availableMoney.value.toString())
        val days = periodDays.value?.let { if (it > 0) it else 1 } ?: run { 1 }
        val dailyExpenses = NumberUtils.roundTo(availableMoney.value?.toDouble()?.div(days))
        fillDashBoardCard(rootView, R.id.daily_card, dailyExpenses.toString())

        val buysUtils = PaymentsFragmentsUtils(context, activity, PaymentsType.BUY)
        val buysExpenses = buysUtils.getTotalPriceUntil(localStorage?.getLastDate())
        fillDashBoardCard(rootView, R.id.buy_card, buysExpenses.toString())

        val billsUtils = PaymentsFragmentsUtils(context, activity, PaymentsType.BILL)
        val billsExpenses = billsUtils.getTotalPriceUntil(localStorage?.getLastDate())
        fillDashBoardCard(rootView, R.id.bill_card, billsExpenses.toString())

        val value = buysExpenses?.let { buy ->
            billsExpenses?.let { bill ->
                buy.plus(bill).plus(dailyExpenses)
            }
        }
        val totalExpenses = NumberUtils.roundTo(value)
        fillDashBoardCard(rootView, R.id.total_card, totalExpenses.toString())
        val neededExpenses =
            NumberUtils.roundTo(availableMoney.value?.toDouble()?.minus(totalExpenses))
        fillDashBoardCard(rootView, R.id.needed_card, neededExpenses.toString())
    }

    fun updateAvailableMoney(value: String? = null) = updateAvailableMoney(
        value?.toValue(STR_DEFAULT_MONETARY_VALUE)?.toFloat()
    )

    fun setAvailableMoney() = _availableMoney.postValue(getAvailableMoneyStr())

    fun getAvailableMoneyStr() = localStorage?.getAvailableMoneyStr().orEmpty()

    override fun onCleared() {
        super.onCleared()
    }
}