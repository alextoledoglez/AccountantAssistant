package com.personal.accountantAssistant.domain.models.home

import androidx.annotation.ColorRes
import androidx.annotation.StringRes

data class DashboardItemModel(
    val value: Double?,
    @StringRes val strResource: Int?,
    @ColorRes var colorResource: Int?
)
