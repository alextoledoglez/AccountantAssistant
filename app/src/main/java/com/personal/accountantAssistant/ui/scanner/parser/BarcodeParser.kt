package com.personal.accountantAssistant.ui.scanner.parser

import com.google.mlkit.vision.barcode.common.Barcode
import com.personal.accountantAssistant.ui.scanner.ScanMode
import com.personal.accountantAssistant.ui.scanner.ScanResult
import com.personal.accountantAssistant.ui.scanner.provider.ProductLookupProvider

object BarcodeParser {

    private fun Barcode.isQrCodeFormat(): Boolean = format == Barcode.FORMAT_QR_CODE

    private fun Barcode.isLinearProductFormat(): Boolean {
        return format == Barcode.FORMAT_EAN_13 ||
                format == Barcode.FORMAT_EAN_8 ||
                format == Barcode.FORMAT_UPC_A ||
                format == Barcode.FORMAT_UPC_E ||
                format == Barcode.FORMAT_CODE_128 ||
                format == Barcode.FORMAT_CODE_39 ||
                format == Barcode.FORMAT_ITF ||
                format == Barcode.FORMAT_CODABAR
    }

    private fun Barcode.toScannedCodeData(): ScannedCodeData = when {
        isQrCodeFormat() -> QrcodeParser.parse(barcode = this)
        isLinearProductFormat() -> BankSlipParser.parse(barcode = this)
        else -> ScannedCodeData(
            barcode = rawValue.orEmpty(),
            confidence = 1f,
            rawText = rawValue.orEmpty()
        )
    }

    suspend fun parse(scanMode: ScanMode, barcode: Barcode): ScanResult? {
        val scannedCodeData = barcode.toScannedCodeData()
        val enrichedScannedCodeData = when {
            scanMode == ScanMode.BUY && barcode.isLinearProductFormat() && scannedCodeData.name.isBlank() -> {
                val productName = ProductLookupProvider.getProductName(scannedCodeData.barcode)
                scannedCodeData.copy(name = productName)
            }

            else -> scannedCodeData
        }
        return when (scanMode) {
            ScanMode.BUY -> enrichedScannedCodeData.toBuyScanResult()
            ScanMode.BILL -> enrichedScannedCodeData.toBillScanResult()
        }
    }
}