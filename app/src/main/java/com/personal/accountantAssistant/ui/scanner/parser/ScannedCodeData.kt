package com.personal.accountantAssistant.ui.scanner.parser

data class ScannedCodeData(
    val barcode: String = "",
    val segment: String = "",
    val company: String = "",
    val name: String = "",
    val value: String = "",
    val date: String = "",
    val confidence: Float = 0f,
    val rawText: String = ""
)
