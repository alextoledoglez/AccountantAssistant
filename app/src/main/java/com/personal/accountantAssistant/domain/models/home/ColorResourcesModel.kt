package com.personal.accountantAssistant.domain.models.home

import androidx.annotation.ColorRes
import com.personal.accountantAssistant.R

data class ColorResourcesModel(
    @ColorRes var error: Int = R.color.colorError,
    @ColorRes var success: Int = R.color.colorSuccess
)
