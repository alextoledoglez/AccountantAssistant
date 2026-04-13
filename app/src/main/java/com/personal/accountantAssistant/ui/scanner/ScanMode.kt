package com.personal.accountantAssistant.ui.scanner

import androidx.annotation.StringRes
import com.personal.accountantAssistant.R

enum class ScanMode(
    @field:StringRes val hintRes: Int,
    val windowWidthFraction: Float,
    val windowHeightFraction: Float
) {
    PRODUCT_BARCODE(
        hintRes = R.string.scan_product_hint,
        windowWidthFraction = 0.85f,
        windowHeightFraction = 0.28f
    ),
    BILL_TEXT(
        hintRes = R.string.scan_bill_hint,
        windowWidthFraction = 0.85f,
        windowHeightFraction = 0.58f
    )
}