package com.personal.accountantAssistant.ui.home

import android.content.Context
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.core.extensions.EMPTY

data class HomeModel(val context: Context) {

    data class CardTitles(val context: Context) {
        val available = context.getString(R.string.available)
        val gain = context.getString(R.string.gain)
        val missing = context.getString(R.string.missing)
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
        var warning = context.getColor(R.color.colorRed)
        var okMedium = context.getColor(R.color.colorPrimaryMedium)
        var okPrimary = context.getColor(R.color.colorPrimary)
    }

    var fontColor = getDefaultFontColor()

    var cardTitle = String.EMPTY
    private lateinit var cardTitles: CardTitles

    var textSize: Int? = null
    private var textSizes = TextSizes(context)

    var backgroundColor: Int? = null
    private var backgroundColors: BackgroundColors? = null

    var imageResource: Int? = null

    init {
        apply {
            fontColor = getDefaultFontColor()
            cardTitles = CardTitles(context)
            backgroundColor = getDefaultBackgroundColor()
            backgroundColors = BackgroundColors(context)
        }
    }

    private fun getDefaultFontColor() = context.getColor(R.color.colorBlack)

    private fun setDefaultFontColor() {
        fontColor = getDefaultFontColor()
    }

    private fun setTitleTextSize() {
        textSize = textSizes.big
    }

    private fun getDefaultBackgroundColor() = context.getColor(R.color.colorWhite)

    private fun getColorResource(
        isCondition: Boolean?, trueResource: Int?, falseResource: Int?
    ) = if (isCondition == true) trueResource else falseResource

    private fun getNeededBackgroundColorBy(isMoreThanOrEqualToZero: Boolean?) = getColorResource(
        isMoreThanOrEqualToZero,
        backgroundColors?.okMedium,
        backgroundColors?.warning
    )

    private fun getBackgroundColorBy(isMoreThanAvailableMoney: Boolean?) = getColorResource(
        isMoreThanAvailableMoney,
        backgroundColors?.warning,
        backgroundColors?.okMedium
    )

    private fun setupCardTitlesBy(title: String, isMoreThanAvailableMoney: Boolean?) {
        setDefaultFontColor()
        setTitleTextSize()
        cardTitle = title
        imageResource = null
        backgroundColor = getBackgroundColorBy(isMoreThanAvailableMoney)
    }

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
        backgroundColor = getDefaultBackgroundColor()
    }

    fun isMoreThanAvailableMoney(value: Double, available: Float): Boolean = value > available

    fun setupAvailableCardTitleBy(isMoreThanAvailableMoney: Boolean?) {
        setupCardTitlesBy(cardTitles.available, isMoreThanAvailableMoney)
    }

    fun setupTotalCardTitleBy(isMoreThanAvailableMoney: Boolean?) {
        setupCardTitlesBy(cardTitles.total, isMoreThanAvailableMoney)
    }

    fun setupNeededCardTitleBy(isMoreThanOrEqualToZero: Boolean?) {
        setDefaultFontColor()
        setTitleTextSize()
        cardTitle = if (isMoreThanOrEqualToZero == true) {
            cardTitles.gain
        } else {
            cardTitles.missing
        }
        imageResource = null
        backgroundColor = getNeededBackgroundColorBy(isMoreThanOrEqualToZero)
    }

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

    fun initializeBy(
        idResource: Int,
        isMoreThanOrEqualToZero: Boolean,
        isMoreThanAvailableMoney: Boolean?
    ) {
        cardTitle = String.EMPTY
        textSize = textSizes.normal
        fontColor = (if (idResource == R.id.needed_card) {
            if (isMoreThanOrEqualToZero) backgroundColors?.okPrimary else backgroundColors?.warning
        } else
            if (isMoreThanAvailableMoney == true) backgroundColors?.warning else backgroundColors?.okPrimary)
            ?: getDefaultFontColor()
        backgroundColor = getDefaultBackgroundColor()
    }
}
