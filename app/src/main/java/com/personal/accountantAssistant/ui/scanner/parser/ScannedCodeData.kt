package com.personal.accountantAssistant.ui.scanner.parser

import com.personal.accountantAssistant.ui.scanner.ScanResult

data class ScannedCodeData(
    val barcode: String = "",
    val name: String = "",
    val value: String = "",
    val date: String = "",
    val confidence: Float = 0f,
    val rawText: String = ""
) {
    fun toBuyScanResult() = ScanResult.Buy(barcode, name, value, confidence, rawText)

    fun toBillScanResult() = ScanResult.Bill(barcode, name, value, date, confidence, rawText)
}
