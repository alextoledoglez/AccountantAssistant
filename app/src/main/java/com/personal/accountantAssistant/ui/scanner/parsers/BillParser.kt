package com.personal.accountantAssistant.ui.scanner.parsers

import com.personal.accountantAssistant.ui.scanner.ScanResult

/**
 * Parses OCR text from Brazilian utility bills and invoices.
 *
 * Handles:
 * - Energy bills: Energisa, Cemig, CPFL, Copel, Light, Coelba, Celpa, Enel, Neoenergia
 * - Internet/telecom: Claro, Vivo, Oi, Tim, Net, Nextel
 * - Water: Sabesp, Cedae, Saneago, Embasa
 * - Generic: any bill with amount + due date
 */
internal object BillParser {

    private val knownProviders = listOf(
        // Energy
        "ENERGISA", "CEMIG", "CPFL", "COPEL", "LIGHT", "COELBA",
        "CELPA", "ENEL", "NEOENERGIA", "CELESC", "COELCE", "ELEKTRO",
        // Telecom
        "CLARO", "VIVO", "OI", "TIM", "NET", "NEXTEL", "ALGAR",
        // Water/sanitation
        "SABESP", "CEDAE", "SANEAGO", "EMBASA", "CAGECE", "COMPESA",
        // Other
        "GAS", "GÁS", "COMGAS", "COPAGAZ"
    )

    private val amountKeywords = listOf(
        "VALOR A PAGAR", "TOTAL A PAGAR", "VALOR DO DOCUMENTO",
        "VALOR TOTAL", "TOTAL GERAL", "VALOR", "TOTAL", "MONTANTE",
        "VALOR FATURA", "TOTAL DA FATURA"
    )

    private val dateKeywords = listOf(
        "VENCIMENTO", "VCTO", "VENC.", "DATA DE VENCIMENTO",
        "DATA VENC", "DATA LIMITE", "PAGAR ATÉ", "PAGAR ATE"
    )

    private val amountRegex = Regex(
        """R\$?\s*([\d]{1,3}(?:[.,]\d{3})*[.,]\d{2}|[\d]+[.,]\d{2})""",
        RegexOption.IGNORE_CASE
    )
    private val dateRegex = Regex("""\b(\d{2}/\d{2}/\d{2,4})\b""")

    fun parse(text: String): ScanResult.Bill? {
        if (text.isBlank()) return null
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
        val upperText = text.uppercase()

        val amount = extractAmount(upperText, lines)
        val date = extractDate(upperText, lines)
        if (amount.isEmpty() && date.isEmpty()) return null

        val name = extractProvider(upperText, lines)
        return ScanResult.Bill(name = name, value = amount, date = date)
    }

    private fun extractAmount(upperText: String, lines: List<String>): String {
        // Priority: search near known amount keywords
        for (keyword in amountKeywords) {
            val idx = lines.indexOfFirst { it.uppercase().contains(keyword) }
            if (idx >= 0) {
                val window = lines.drop(idx).take(3).joinToString(" ")
                amountRegex.find(window)?.groupValues?.getOrNull(1)?.trim()
                    ?.let { return it }
            }
        }
        // Fallback: largest R$ amount in document (likely the total)
        return amountRegex.findAll(upperText)
            .mapNotNull { it.groupValues.getOrNull(1)?.trim() }
            .maxByOrNull { it.replace(Regex("[.,]"), "").toLongOrNull() ?: 0L }
            .orEmpty()
    }

    private fun extractDate(upperText: String, lines: List<String>): String {
        // Priority: search near known date keywords
        for (keyword in dateKeywords) {
            val idx = lines.indexOfFirst { it.uppercase().contains(keyword) }
            if (idx >= 0) {
                val window = lines.drop(idx).take(3).joinToString(" ")
                dateRegex.find(window)?.groupValues?.getOrNull(1)?.trim()
                    ?.let { return it }
            }
        }
        // Fallback: first date found in text
        return dateRegex.find(upperText)?.groupValues?.getOrNull(1).orEmpty()
    }

    private fun extractProvider(upperText: String, lines: List<String>): String {
        knownProviders.firstOrNull { upperText.contains(it) }?.let { return it }
        // Fallback: first line composed only of letters (likely the company name)
        return lines.firstOrNull { line ->
            line.length >= 3 &&
            line.any { it.isLetter() } &&
            line.none { it.isDigit() } &&
            !amountRegex.containsMatchIn(line)
        }?.uppercase().orEmpty()
    }
}