package com.personal.accountantAssistant.ui.scanner.parser

import com.google.mlkit.vision.barcode.common.Barcode

object BarcodeParser {

    private fun Barcode.isQrCodeFormat(): Boolean = format == Barcode.FORMAT_QR_CODE

    fun Barcode.isBarcodeFormat(): Boolean {
        return format == Barcode.FORMAT_EAN_13 ||
                format == Barcode.FORMAT_EAN_8 ||
                format == Barcode.FORMAT_UPC_A ||
                format == Barcode.FORMAT_UPC_E ||
                format == Barcode.FORMAT_CODE_128 ||
                format == Barcode.FORMAT_CODE_39 ||
                format == Barcode.FORMAT_ITF ||
                format == Barcode.FORMAT_CODABAR
    }

    fun Barcode.toScannedCodeData(): ScannedCodeData = when {
        isQrCodeFormat() -> QrcodeParser.parse(barcode = this)
        isBarcodeFormat() -> BankSlipParser.parse(barcode = this)
        else -> ScannedCodeData(
            barcode = rawValue.orEmpty(),
            confidence = 1f,
            rawText = rawValue.orEmpty()
        )
    }
}