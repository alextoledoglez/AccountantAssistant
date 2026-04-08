package com.personal.accountantAssistant.ui.scanner.parsers

import com.personal.accountantAssistant.ui.scanner.ScanResult

/**
 * Parses OCR text from credit/debit card faces.
 * Extracts last 4 digits, expiry date, and card company/flag name.
 */
internal object CardParser {

    private val cardRegex = Regex("""\b\d{4}[\s\-]?\d{4}[\s\-]?\d{4}[\s\-]?\d{4}\b""")
    private val expiryRegex = Regex("""\b(\d{2}/\d{2})\b""")

    private val knownFlags = listOf(
        "VISA", "MASTERCARD", "MASTER", "ELO", "HIPERCARD",
        "AMEX", "AMERICAN EXPRESS", "CABAL", "DINERS", "DISCOVER"
    )

    fun parse(text: String): ScanResult.Card? {
        if (text.isBlank()) return null
        val cardNumber = cardRegex.find(text)
            ?.value
            ?.replace(Regex("""[\s\-]"""), "")
            ?: return null

        val lastDigits = cardNumber.takeLast(4)
        val expiry = expiryRegex.find(text)?.groupValues?.getOrNull(1).orEmpty()
        val company = extractCompany(text)
        return ScanResult.Card(company = company, lastDigits = lastDigits, expiry = expiry)
    }

    private fun extractCompany(text: String): String {
        val upper = text.uppercase()
        knownFlags.firstOrNull { upper.contains(it) }?.let { return it }
        return text.lines()
            .map { it.trim() }
            .firstOrNull { line ->
                line.isNotBlank() &&
                line.length >= 3 &&
                line.all { c -> c.isLetter() || c.isWhitespace() }
            }
            ?.uppercase()
            .orEmpty()
    }
}