package com.personal.accountantAssistant.domain.models

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import java.math.BigDecimal

data class DashboardItemModel(
    @field:DrawableRes val drawableRes: Int,
    val text: String,
    @field:ColorInt var color: Int,
    val value: BigDecimal
)
