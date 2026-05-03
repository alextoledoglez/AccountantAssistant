package com.personal.accountantAssistant.ui.scanner.parser

import com.personal.accountantAssistant.ui.scanner.ScanMode

data class ScannedCodeData(
    val scanMode: ScanMode,
    val barcode: String = "",
    val displayText: String = "",
    val segment: String = "",
    val company: String = "",
    val name: String = "",
    val amount: String = "",
    val date: String = "",
    val rawText: String = ""
)
