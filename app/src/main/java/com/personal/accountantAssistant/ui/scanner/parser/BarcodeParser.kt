package com.personal.accountantAssistant.ui.scanner.parser

import com.google.mlkit.vision.barcode.common.Barcode

object BarcodeParser {

    private fun Barcode.isQrCodeFormat(): Boolean = format == Barcode.FORMAT_QR_CODE

    fun Barcode.isBarcodeFormat(): Boolean = isProductBarcodeFormat() || isBankSlipBarcodeFormat()

    fun Barcode.isProductBarcodeFormat(): Boolean {
        return format in setOf(
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E
        )
    }

    fun Barcode.isBankSlipBarcodeFormat(): Boolean {
        return format in setOf(
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_ITF,
            Barcode.FORMAT_CODABAR
        )
    }

    fun Barcode.isValidProductBarcode(): Boolean {
        val isValidProductCodeLength = normalizeLinearCode().length in setOf(8, 12, 13, 14)
        return isProductBarcodeFormat() && isValidProductCodeLength
    }

    fun Barcode.isValidBankSlipBarcode(): Boolean {
        val isValidBankSlipCodeLength = normalizeLinearCode().length in setOf(44, 47, 48)
        return isBankSlipBarcodeFormat() && isValidBankSlipCodeLength
    }

    fun Barcode.normalizeLinearCode(): String = rawValue.orEmpty().filter(predicate = Char::isDigit)

    fun Barcode.toScannedCodeData(): ScannedCodeData = when {
        isQrCodeFormat() -> QrcodeParser.parse(barcode = this)
        isValidProductBarcode() -> ProductBarcodeParser.parse(barcode = this)
        isValidBankSlipBarcode() -> BankSlipBarcodeParser.parse(barcode = this)
        else -> ScannedCodeData(
            barcode = rawValue.orEmpty(),
            confidence = 0f,
            rawText = rawValue.orEmpty()
        )
    }
}