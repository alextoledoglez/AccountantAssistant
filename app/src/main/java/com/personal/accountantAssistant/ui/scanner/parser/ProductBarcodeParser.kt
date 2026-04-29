package com.personal.accountantAssistant.ui.scanner.parser

import com.google.mlkit.vision.barcode.common.Barcode
import com.personal.accountantAssistant.ui.scanner.parser.BarcodeParser.normalizeLinearCode

object ProductBarcodeParser {

    fun parse(barcode: Barcode): ScannedCodeData {
        val rawValue = barcode.rawValue.orEmpty()
        val digits = barcode.normalizeLinearCode()
        return ScannedCodeData(
            barcode = digits,
            name = "",
            value = "",
            date = "",
            confidence = 1f,
            rawText = rawValue
        )
    }
}