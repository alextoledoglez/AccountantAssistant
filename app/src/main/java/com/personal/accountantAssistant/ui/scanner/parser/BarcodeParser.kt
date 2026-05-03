package com.personal.accountantAssistant.ui.scanner.parser

import android.util.Log
import com.google.mlkit.vision.barcode.common.Barcode
import com.personal.accountantAssistant.extensions.toDigitsStr
import com.personal.accountantAssistant.ui.scanner.ScanMode

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
        val isValidProductCodeLength = rawValue.toDigitsStr().length in setOf(8, 12, 13, 14)
        val isValidProductBarcode = isProductBarcodeFormat() && isValidProductCodeLength
        Log.i(TAG, "isValidProductBarcode: $isValidProductBarcode")
        return isValidProductBarcode
    }

    fun Barcode.isValidBankSlipBarcode(): Boolean {
        val isValidBankSlipCodeLength = rawValue.toDigitsStr().length in setOf(44, 47, 48)
        val isValidBankSlipBarcode = isBankSlipBarcodeFormat() && isValidBankSlipCodeLength
        Log.i(TAG, "isValidBankSlipBarcode: $isValidBankSlipBarcode")
        return isValidBankSlipBarcode
    }

    fun Barcode.toScannedCodeData(scanMode: ScanMode): ScannedCodeData? {
        val data = ScannedCodeData(
            scanMode = scanMode,
            barcode = rawValue.toDigitsStr(),
            displayText = displayValue?.takeIf { it.isNotBlank() } ?: rawValue.orEmpty(),
            rawText = rawValue.orEmpty()
        )
        Log.i(TAG, "scannedData: $data")
        return when {
            isQrCodeFormat() -> QrcodeParser.parse(data)
            isValidProductBarcode() && scanMode.isBuy() -> ProductBarcodeParser.parse(data)
            isValidBankSlipBarcode() && scanMode.isBill() -> BankSlipBarcodeParser.parse(data)
            else -> null
        }
    }

    val TAG: String = BarcodeParser.javaClass.simpleName
}