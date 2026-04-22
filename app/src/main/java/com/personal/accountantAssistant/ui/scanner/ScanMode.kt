package com.personal.accountantAssistant.ui.scanner

import androidx.annotation.StringRes
import com.personal.accountantAssistant.R

enum class ScanMode(@field:StringRes val hintRes: Int) {
    BUY(hintRes = R.string.scan_buy_hint),
    BILL(hintRes = R.string.scan_bill_hint)
}