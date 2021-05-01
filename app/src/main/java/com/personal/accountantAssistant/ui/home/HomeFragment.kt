package com.personal.accountantAssistant.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.db.LocalStorage
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import com.personal.accountantAssistant.utils.*
import com.savvi.rangedatepicker.CalendarPickerView
import kotlin.math.abs

class HomeFragment : Fragment() {

    private var activity: Activity? = null
    private var localStorage: LocalStorage? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        MenuHelper.initializeHomeOptions()
        localStorage = context?.let { LocalStorage(it) }
        activity = getActivity()
        val rootView: View = inflater.inflate(R.layout.fragment_home, container, java.lang.Boolean.FALSE)

        //Money
        val availableMoneyCard = rootView.findViewById<View>(R.id.available_card)
        val availableMoneyEditText = availableMoneyCard.findViewById<EditText>(R.id.card_edit_text)
        availableMoneyEditText.setText(localStorage?.getAvailableMoney().toString())
        EditableTextsUtils.initializeListeners(activity, availableMoneyEditText) {
            localStorage?.setAvailableMoney(EditableTextsUtils.getEditTextValue().toFloat())
            calculateExpenses(context, activity, rootView)
        }

        //Period
        val periodCardSubTitle = rootView.findViewById<TextView>(R.id.period_card_text_view)
        periodCardSubTitle.text = DateUtils.toPeriodStr(localStorage?.getFirstDate(), localStorage?.getLastDate())
        val calendarPickerView: CalendarPickerView = rootView.findViewById(R.id.period_calendar_picker_view)
        DatePickerDialogUtils.initializeCalendarPickerView(calendarPickerView,
                { periodCardSubTitle.text = DateUtils.toPeriodStr(localStorage?.getFirstDate(), localStorage?.getLastDate()) }
        ) {
            calculateExpenses(context, activity, rootView)
            EditableTextsUtils.hideSoftInputFromWindow(activity, availableMoneyEditText)
        }
        calculateExpenses(context, activity, rootView)
        return rootView
    }

    private fun calculateExpenses(context: Context?, activity: Activity?, rootView: View) {
        val availableMoney = localStorage?.getAvailableMoney()?.toDouble()
        fillDashBoardCard(rootView, R.id.available_card, availableMoney.toString())
        var periodDays = DateUtils.getDaysBetween(localStorage?.getFirstDate(), localStorage?.getLastDate())
        periodDays = if (periodDays > 0) periodDays else 1
        val dailyExpenses = NumberUtils.roundTo(availableMoney?.div(periodDays))
        fillDashBoardCard(rootView, R.id.daily_card, dailyExpenses.toString())
        val lastPeriodDate = localStorage?.getLastDate()
        val buysUtils = PaymentsFragmentsUtils(context, activity, PaymentsType.BUY)
        val buysExpenses = buysUtils.getTotalPriceUntil(lastPeriodDate)
        fillDashBoardCard(rootView, R.id.buy_card, buysExpenses.toString())
        val billsUtils = PaymentsFragmentsUtils(context, activity, PaymentsType.BILL)
        val billsExpenses = billsUtils.getTotalPriceUntil(lastPeriodDate)
        fillDashBoardCard(rootView, R.id.bill_card, billsExpenses.toString())

        val value = buysExpenses?.let { buy -> billsExpenses?.let { bill -> buy.plus(bill).plus(dailyExpenses) } }
        val totalExpenses = NumberUtils.roundTo(value)
        fillDashBoardCard(rootView, R.id.total_card, totalExpenses.toString())
        val neededExpenses = NumberUtils.roundTo(availableMoney?.minus(totalExpenses))
        fillDashBoardCard(rootView, R.id.needed_card, neededExpenses.toString())
    }

    private fun isMoreThanAvailableMoney(value: String): Boolean? {
        return localStorage?.getAvailableMoney()?.let { value.toDouble() > it }
    }

    private fun isMoreThanAvailableMoney(value: String, resourceIfTrue: Int, resourceIfFalse: Int): Int {
        return if (isMoreThanAvailableMoney(value) == true) resourceIfTrue else resourceIfFalse
    }

    private fun isMoreThanOrEqualToZero(value: String): Boolean {
        return value.toDouble() >= 0
    }

    private fun isMoreThanOrEqualToZero(value: String, resourceIfTrue: Int, resourceIfFalse: Int): Int {
        return if (isMoreThanOrEqualToZero(value)) resourceIfTrue else resourceIfFalse
    }

    private fun setTitleTextViewStyleFor(cardTitleTextView: TextView,
                                         textSizeResource: Int,
                                         fontColorResource: Int,
                                         backgroundColorResource: Int) {
        cardTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeResource.toFloat())
        cardTitleTextView.setTextColor(fontColorResource)
        cardTitleTextView.setBackgroundColor(backgroundColorResource)
    }

    private fun getTitleColorResourceFrom(idResource: Int,
                                          cardTextValue: String): Int? {
        val context = context
        val goodColorResource = context?.getColor(R.color.colorPrimaryMedium)
        val badColorResource = context?.getColor(R.color.colorRed)
        return goodColorResource?.also { good ->
            badColorResource?.let { bad ->
                getColorResourceFrom(idResource, cardTextValue, good, bad)
            }
        }
    }

    private fun getColorResourceFrom(idResource: Int,
                                     cardTextValue: String): Int? {
        val context = context
        val goodColorResource = context?.getColor(R.color.colorPrimary)
        val badColorResource = context?.getColor(R.color.colorRed)
        return goodColorResource?.also { good ->
            badColorResource?.let { bad ->
                getColorResourceFrom(idResource, cardTextValue, good, bad)
            }
        }
    }

    private fun getColorResourceFrom(idResource: Int,
                                     cardTextValue: String,
                                     goodColorResource: Int,
                                     badColorResource: Int): Int {
        return if (idResource == R.id.needed_card) {
            isMoreThanOrEqualToZero(cardTextValue, goodColorResource, badColorResource)
        } else {
            isMoreThanAvailableMoney(cardTextValue, badColorResource, goodColorResource)
        }
    }

    private fun setCardViewStyle(cardView: CardView,
                                 cardVisibility: Int,
                                 textSizeResource: Int,
                                 fontColorResource: Int,
                                 backgroundColorResource: Int) {
        val imageView = cardView.findViewById<ImageView>(R.id.card_image)
        imageView.visibility = cardVisibility
        val cardTitleTextView = cardView.findViewById<TextView>(R.id.card_title)
        setTitleTextViewStyleFor(cardTitleTextView, textSizeResource, fontColorResource, backgroundColorResource)
    }

    private fun setTitleCardViewStyle(idResource: Int,
                                      cardView: CardView,
                                      cardTextValue: String,
                                      cardTextSizeResource: Int) {
        val context = context
        val cardVisibility = View.GONE
        val fontColorResource = context?.getColor(R.color.colorBlack)
        val backgroundColorResource = getTitleColorResourceFrom(idResource, cardTextValue)
        fontColorResource?.let { font ->
            backgroundColorResource?.let { background ->
                setCardViewStyle(cardView, cardVisibility, cardTextSizeResource, font, background)
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    private fun fillDashBoardCard(root: View,
                                  idResource: Int,
                                  cardTextValue: String) {
        var cardTitle = ""
        var imageResource = 0
        val context = context
        val fontColorResource = getColorResourceFrom(idResource, cardTextValue)
        val backgroundColorResource = context?.getColor(R.color.colorWhite)
        val textSizeResource = resources.getDimensionPixelSize(R.dimen.card_title_text_size)
        val titleTextSizeResource = resources.getDimensionPixelSize(R.dimen.card_title_text_big_size)
        val cardView: CardView = root.findViewById(idResource)
        val imageView = cardView.findViewById<ImageView>(R.id.card_image)
        imageView.visibility = View.VISIBLE
        val cardTitleTextView = cardView.findViewById<TextView>(R.id.card_title)
        fontColorResource?.let { font ->
            backgroundColorResource?.let { background ->
                setCardViewStyle(cardView, View.VISIBLE, textSizeResource, font, background)
            }
        }

        when (idResource) {
            R.id.available_card -> {
                cardTitle = getString(R.string.available)
                setTitleCardViewStyle(idResource, cardView, cardTextValue, titleTextSizeResource)
            }
            R.id.needed_card -> {
                cardTitle = if (isMoreThanOrEqualToZero(cardTextValue)) {
                    getString(R.string.gain)
                } else {
                    getString(R.string.missing)
                }
                setTitleCardViewStyle(idResource, cardView, cardTextValue, titleTextSizeResource)
            }
            R.id.total_card -> {
                cardTitle = getString(R.string.total)
                setTitleCardViewStyle(idResource, cardView, cardTextValue, titleTextSizeResource)
            }
            R.id.daily_card -> {
                cardTitle = getString(R.string.daily)
                imageResource = isMoreThanAvailableMoney(cardTextValue, R.drawable.ic_menu_red_daily, R.drawable.ic_menu_green_daily)
            }
            R.id.buy_card -> {
                cardTitle = getString(R.string.menu_buys)
                imageResource = isMoreThanAvailableMoney(cardTextValue, R.drawable.ic_menu_red_buys, R.drawable.ic_menu_green_buys)
            }
            R.id.bill_card -> {
                cardTitle = getString(R.string.menu_bills)
                imageResource = isMoreThanAvailableMoney(cardTextValue, R.drawable.ic_menu_red_bills, R.drawable.ic_menu_green_bills)
            }
        }
        cardTitleTextView.text = cardTitle
        imageView.setImageResource(imageResource)
        if (idResource != R.id.available_card) {
            val cardSubTitleTextView = cardView.findViewById<TextView>(R.id.card_sub_title)
            cardSubTitleTextView.text = abs(cardTextValue.toDouble()).toString()
            fontColorResource?.let { cardSubTitleTextView.setTextColor(it) }
        }
    }
}