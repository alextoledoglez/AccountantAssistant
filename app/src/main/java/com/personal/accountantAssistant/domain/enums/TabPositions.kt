package com.personal.accountantAssistant.domain.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.ui.scanner.ScanMode

enum class TabPositions(
    val position: Int,
    @field:StringRes val titleRes: Int,
    @field:DrawableRes val iconRes: Int,
    val scanMode: ScanMode? = null,
    val hasDeleteAll: Boolean = false
) {
    HOME(0, R.string.menu_home, R.drawable.ic_home),
    WALLET(1, R.string.menu_wallet, R.drawable.ic_wallet, ScanMode.CARD_TEXT, hasDeleteAll = true),
    BUYS(2, R.string.menu_buys, R.drawable.ic_buys, ScanMode.PRODUCT_BARCODE, hasDeleteAll = true),
    BILLS(3, R.string.menu_bills, R.drawable.ic_bills, ScanMode.BILL_TEXT, hasDeleteAll = true),
    PROFILE(4, R.string.menu_profile, R.drawable.ic_profile)
}