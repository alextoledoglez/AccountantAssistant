package com.personal.accountantAssistant.ui.home

import android.content.Context
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.core.extensions.EMPTY

data class HomeModel(val context: Context) {

    data class CardTitles(val context: Context) {
        val gain = context.getString(R.string.gain)
        val missing = context.getString(R.string.missing)
        val available = context.getString(R.string.available)
        val total = context.getString(R.string.total)
        val daily = context.getString(R.string.daily)
        val buys = context.getString(R.string.menu_buys)
        val bills = context.getString(R.string.menu_bills)
    }

    data class TextSizes(val context: Context) {
        var normal = context.resources.getDimensionPixelSize(R.dimen.card_title_text_size)
        var big = context.resources.getDimensionPixelSize(R.dimen.card_title_text_big_size)
    }

    data class BackgroundColors(val context: Context) {
        var error = context.getColor(R.color.colorError)
        var success = context.getColor(R.color.colorSuccess)
    }

    var fontColor = getDefaultFontColor()

    var cardTitle: String? = String.EMPTY
    lateinit var cardTitles: CardTitles

    var textSize: Int? = null
    private var textSizes = TextSizes(context)

    var backgroundColor: Int? = null
    private var backgroundColors: BackgroundColors? = null

    var imageResource: Int? = null
    var hideImageView: Boolean? = false

    init {
        apply {
            fontColor = getDefaultFontColor()
            cardTitles = CardTitles(context)
            backgroundColors = BackgroundColors(context)
            backgroundColor = getDefaultBackgroundColor()
        }
    }

    private fun getDefaultFontColor() = context.getColor(R.color.colorBlack)

    private fun getDefaultBackgroundColor() = context.getColor(R.color.colorWhite)

    private fun getColorBy(isCondition: Boolean?, trueResource: Int?, falseResource: Int?) =
        if (isCondition == true) trueResource else falseResource

    fun getAvailableBackgroundColorBy(isMoreThanOrEqualToExpenses: Boolean?) = getColorBy(
        isMoreThanOrEqualToExpenses,
        backgroundColors?.success,
        backgroundColors?.error
    )

    fun getTotalBackgroundColorBy(isMoreThanAvailableMoney: Boolean?) = getColorBy(
        isMoreThanAvailableMoney,
        backgroundColors?.error,
        backgroundColors?.success
    )

    fun getNeededBackgroundColorBy(isMoreThanOrEqualToZero: Boolean?) = getColorBy(
        isMoreThanOrEqualToZero,
        backgroundColors?.success,
        backgroundColors?.error
    )

    private fun getFontColorBy(isMoreThanAvailableMoney: Boolean?) = (
            if (isMoreThanAvailableMoney == true) backgroundColors?.error else backgroundColors?.success
            ) ?: getDefaultFontColor()

    private fun setupImageCardTitleBy(
        title: String,
        isMoreThanAvailableMoney: Boolean?,
        warningImageResource: Int,
        okImageResource: Int
    ) {
        cardTitle = title
        imageResource = if (isMoreThanAvailableMoney == true)
            warningImageResource
        else
            okImageResource
        hideImageView = false
        backgroundColor = getDefaultBackgroundColor()
    }

    fun isMoreThanAvailableMoney(value: Double, available: Float): Boolean = value > available

    fun getNeededTitleBy(isMoreThanOrEqualToZero: Boolean?) = if (isMoreThanOrEqualToZero == true)
        cardTitles.gain
    else
        cardTitles.missing

    fun setupDailyImageCardTitleBy(isMoreThanAvailableMoney: Boolean?) = setupImageCardTitleBy(
        cardTitles.daily,
        isMoreThanAvailableMoney,
        R.drawable.ic_menu_red_daily,
        R.drawable.ic_menu_green_daily
    )

    fun setupBuysImageCardTitleBy(isMoreThanAvailableMoney: Boolean?) = setupImageCardTitleBy(
        cardTitles.buys,
        isMoreThanAvailableMoney,
        R.drawable.ic_menu_red_buys,
        R.drawable.ic_menu_green_buys
    )

    fun setupBillsImageCardTitleBy(isMoreThanAvailableMoney: Boolean?) = setupImageCardTitleBy(
        cardTitles.bills,
        isMoreThanAvailableMoney,
        R.drawable.ic_menu_red_bills,
        R.drawable.ic_menu_green_bills
    )

    fun initializeTheme(isMoreThanAvailableMoney: Boolean?) {
        cardTitle = String.EMPTY
        textSize = textSizes.normal
        fontColor = getFontColorBy(isMoreThanAvailableMoney)
        backgroundColor = getDefaultBackgroundColor()
    }
}
