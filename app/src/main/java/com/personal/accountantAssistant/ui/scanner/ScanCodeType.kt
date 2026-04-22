package com.personal.accountantAssistant.ui.scanner

import androidx.annotation.StringRes
import com.personal.accountantAssistant.R

enum class ScanCodeType(@field:StringRes val textRes: Int) {
    QR_CODE(textRes = R.string.scan_qr_code_text),
    BARCODE(textRes = R.string.scan_barcode_text);

    fun isQrCode() = this == QR_CODE

    fun isBarcode() = this == BARCODE
}