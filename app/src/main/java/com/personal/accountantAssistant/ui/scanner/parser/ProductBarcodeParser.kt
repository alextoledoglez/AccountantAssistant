package com.personal.accountantAssistant.ui.scanner.parser

import com.personal.accountantAssistant.extensions.toDigitsStr

object ProductBarcodeParser {

    fun parse(data: ScannedCodeData): ScannedCodeData {
        val rawText = data.rawText
        val barcode = rawText.toDigitsStr()
        return data.copy(barcode = barcode, confidence = 1f, rawText = rawText)
    }
}