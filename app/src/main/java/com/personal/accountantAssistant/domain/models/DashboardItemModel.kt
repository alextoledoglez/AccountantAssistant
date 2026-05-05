package com.personal.accountantAssistant.domain.models

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.domain.enums.TabPositions
import java.math.BigDecimal

data class DashboardItemModel(
    @field:DrawableRes val drawableRes: Int,
    val text: String,
    @field:ColorInt var color: Int,
    val value: BigDecimal
) {
    fun toTabPosition(): TabPositions = when (drawableRes) {
        R.drawable.ic_buys -> TabPositions.BUYS
        R.drawable.ic_bills -> TabPositions.BILLS
        else -> TabPositions.WALLET
    }
}
