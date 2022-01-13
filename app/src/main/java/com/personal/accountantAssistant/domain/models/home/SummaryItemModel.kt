package com.personal.accountantAssistant.domain.models.home

import androidx.annotation.ColorRes
import androidx.annotation.StringRes

data class SummaryItemModel(
    val value: Float?,
    @StringRes val strResource: Int?,
    @ColorRes var colorResource: Int?
)
