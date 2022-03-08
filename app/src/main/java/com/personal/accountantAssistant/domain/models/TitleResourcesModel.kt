package com.personal.accountantAssistant.domain.models

import androidx.annotation.StringRes
import com.personal.accountantAssistant.R

data class TitleResourcesModel(
    @StringRes val gain: Int = R.string.gain,
    @StringRes val missing: Int = R.string.missing,
    @StringRes val available: Int = R.string.available,
    @StringRes val total: Int = R.string.total,
    @StringRes val daily: Int = R.string.daily,
    @StringRes val buys: Int = R.string.menu_buys,
    @StringRes val bills: Int = R.string.menu_bills,
)
