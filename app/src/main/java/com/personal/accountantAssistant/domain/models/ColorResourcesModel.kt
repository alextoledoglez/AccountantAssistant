package com.personal.accountantAssistant.domain.models

import androidx.annotation.ColorRes
import com.personal.accountantAssistant.R

data class ColorResourcesModel(
    @ColorRes var error: Int = R.color.errorColor,
    @ColorRes var success: Int = R.color.successColor
)
