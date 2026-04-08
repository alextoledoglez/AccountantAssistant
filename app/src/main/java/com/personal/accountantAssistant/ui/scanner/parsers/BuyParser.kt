package com.personal.accountantAssistant.ui.scanner.parsers

import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.ui.scanner.ScanResult

/**
 * Parses OCR text from buy price labels (supermarkets, wholesale stores).
 * Extracts buy name and price alongside the barcode already detected.
 */
internal object BuyParser {

    private val priceRegex = Regex(
        pattern = """R\$?\s*(\d{1,3}(?:[.,]\d{3})*[.,]\d{2}|\d+[.,]\d{2})""",
        option = RegexOption.IGNORE_CASE
    )

    private val priceSeparatorRegex = Regex(pattern = "[.,]")

    private val unitTokens = setOf(
        "UN", "UND", "UNID", "KG", "GR", "G", "LT", "L", "ML",
        "PCT", "PÇ", "PC", "CX", "SC", "FD", "BD", "PT",
        "KG/", "/KG", "GR/", "/GR"
    )

    private val billsPatterns = listOf(
        Regex(pattern = """R\$""", RegexOption.IGNORE_CASE),   // price lines
        Regex(pattern = """^\d+$"""),                            // pure digits (barcodes)
        Regex(pattern = """^\d{2}/\d{2}"""),                    // dates
        Regex(pattern = """^(VALIDADE|VENC|COD|REF)\b""", RegexOption.IGNORE_CASE)
    )

    private fun extractPrice(text: String): String = priceRegex.findAll(text)
        .mapNotNull { it.groupValues.getOrNull(index = 1)?.trim() }
        .maxByOrNull { it.replace(priceSeparatorRegex, replacement = "").toLongOrNull().orZero() }
        .orEmpty()

    private fun extractName(text: String): String = text.lines().map { it.trim() }
        .filter { line ->
            line.length >= 4 &&
                    line.any { it.isLetter() } &&
                    !billsPatterns.any { it.containsMatchIn(input = line) } &&
                    line.uppercase().split(Regex(pattern = "\\s+")).none { it in unitTokens }
        }
        .maxByOrNull { it.count { c -> c.isLetter() } }
        .orEmpty()
        .uppercase()

    fun parse(text: String): ScanResult.Product {
        val price = extractPrice(text)
        val name = extractName(text)
        return ScanResult.Product(name, price)
    }
}