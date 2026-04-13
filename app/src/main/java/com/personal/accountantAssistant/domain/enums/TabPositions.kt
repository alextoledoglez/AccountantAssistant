package com.personal.accountantAssistant.domain.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.personal.accountantAssistant.R

enum class TabPositions(
    val route: String,
    @field:StringRes val titleRes: Int,
    @field:DrawableRes val iconRes: Int
) {
    HOME("home", R.string.menu_home, R.drawable.ic_home),
    WALLET("wallet", R.string.menu_wallet, R.drawable.ic_wallet),
    BUYS("buys", R.string.menu_buys, R.drawable.ic_buys),
    BILLS("bills", R.string.menu_bills, R.drawable.ic_bills),
    PROFILE("profile", R.string.menu_profile, R.drawable.ic_profile)
}