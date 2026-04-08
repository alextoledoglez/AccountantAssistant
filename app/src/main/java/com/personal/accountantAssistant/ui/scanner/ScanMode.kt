package com.personal.accountantAssistant.ui.scanner

import com.personal.accountantAssistant.R

enum class ScanMode {
    PRODUCT_BARCODE,
    BILL_TEXT,
    CARD_TEXT;

    val hintRes: Int
        get() = when (this) {
            PRODUCT_BARCODE -> R.string.scan_product_hint
            BILL_TEXT -> R.string.scan_bill_hint
            CARD_TEXT -> R.string.scan_card_hint
        }
}