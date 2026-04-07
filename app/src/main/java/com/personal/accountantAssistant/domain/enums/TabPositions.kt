package com.personal.accountantAssistant.domain.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.personal.accountantAssistant.R

enum class TabPositions(
    val position: Int,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int
) {
    HOME(0, R.string.menu_home, R.drawable.ic_home),
    WALLET(1, R.string.menu_wallet, R.drawable.ic_wallet),
    BUYS(2, R.string.menu_buys, R.drawable.ic_buys),
    BILLS(3, R.string.menu_bills, R.drawable.ic_bills),
    PROFILE(4, R.string.menu_profile, R.drawable.ic_profile)
}