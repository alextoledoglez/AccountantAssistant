package com.personal.accountantAssistant.domain.models

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import java.math.BigDecimal

data class DashboardItemModel(
    @DrawableRes val drawableRes: Int,
    val text: String,
    @ColorInt var color: Int,
    val value: BigDecimal
)
