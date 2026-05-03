package com.personal.accountantAssistant.ui.scanner.parser

object ProductBarcodeParser {

    fun parse(data: ScannedCodeData): ScannedCodeData {
        return data.copy(barcode = data.barcode, rawText = data.rawText)
    }
}