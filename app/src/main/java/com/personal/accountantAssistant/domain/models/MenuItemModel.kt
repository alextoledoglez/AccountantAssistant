package com.personal.accountantAssistant.domain.models

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.personal.accountantAssistant.R

data class MenuItemModel(
    val id: Int,
    @field:DrawableRes val icon: Int,
    @field:StringRes val text: Int,
    @field:ColorRes var color: Int = R.color.blackColor,
)
