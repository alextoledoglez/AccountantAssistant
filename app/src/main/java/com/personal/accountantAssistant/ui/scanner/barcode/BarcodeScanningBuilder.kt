package com.personal.accountantAssistant.ui.scanner.barcode

import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.personal.accountantAssistant.ui.scanner.ScanCodeType

object BarcodeScanningBuilder {

    private val qrcodeScannerBuilder = BarcodeScannerOptions.Builder().setBarcodeFormats(
        Barcode.FORMAT_QR_CODE
    )

    private val barcodeScannerBuilder = BarcodeScannerOptions.Builder().setBarcodeFormats(
        Barcode.FORMAT_EAN_13,
        Barcode.FORMAT_EAN_8,
        Barcode.FORMAT_UPC_A,
        Barcode.FORMAT_UPC_E,
        Barcode.FORMAT_CODE_128,
        Barcode.FORMAT_CODE_39,
        Barcode.FORMAT_ITF,
        Barcode.FORMAT_CODABAR
    )

    fun buildQrScanner() = BarcodeScanning.getClient(qrcodeScannerBuilder.build())

    fun buildBarcodeScanner() = BarcodeScanning.getClient(barcodeScannerBuilder.build())

    fun buildScanner(codeType: ScanCodeType) = when (codeType) {
        ScanCodeType.QR_CODE -> buildQrScanner()
        ScanCodeType.BARCODE -> buildBarcodeScanner()
    }
}