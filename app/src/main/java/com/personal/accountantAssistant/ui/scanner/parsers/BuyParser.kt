package com.personal.accountantAssistant.ui.scanner.parsers

import com.personal.accountantAssistant.ui.scanner.ScanResult

internal class BuyParser(private val profile: ScanParsingProfile) {

    private val moneyRegex = Regex(
        """(?:${profile.currencySymbols.joinToString("|") { Regex.escape(it) }}\s*)?(\d{1,3}(?:[.,]\d{3})*[.,]\d{2}|\d+[.,]\d{2})""",
        RegexOption.IGNORE_CASE
    )

    private val genericDateRegex = Regex("""\b\d{2}/\d{2}(?:/\d{2,4})?\b""")
    private val barcodeLineRegex = Regex("""^\d{8,14}$""")
    private val weightRegex = Regex(
        """\b\d+(?:[.,]\d+)?\s?(${profile.unitKeywords.joinToString("|")})\b""",
        RegexOption.IGNORE_CASE
    )

    fun parse(text: String, barcode: String = ""): ScanResult.Buy {
        if (text.isBlank() && barcode.isBlank()) return ScanResult.Buy()

        val lines = normalize(text)
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val bestPrice = extractBestPrice(lines)
        val bestName = extractBestName(lines)
        val confidence = buildConfidence(barcode, bestName, bestPrice)

        return ScanResult.Buy(
            barcode = barcode,
            name = bestName,
            price = bestPrice,
            confidence = confidence,
            rawText = text
        )
    }

    private fun normalize(text: String): String {
        return text
            .replace("|", " ")
            .replace(Regex("""[ \t]+"""), " ")
            .replace(Regex("""\n+"""), "\n")
            .trim()
    }

    private fun extractBestPrice(lines: List<String>): String {
        val candidates = mutableListOf<Pair<String, Int>>()

        lines.forEachIndexed { index, line ->
            moneyRegex.findAll(line).forEach { match ->
                val value = match.groupValues.getOrNull(1).orEmpty()
                if (value.isBlank()) return@forEach

                val upper = line.uppercase(profile.locale)
                var score = 0

                if (profile.currencySymbols.any { upper.contains(it.uppercase(profile.locale)) }) score += 4
                if (profile.amountKeywords.any { upper.contains(it) }) score += 4
                if (upper.contains("POR")) score += 1
                if (upper.contains("CADA")) score += 2
                if (upper.contains("PRICE")) score += 2
                if (weightRegex.containsMatchIn(line)) score -= 2
                if (genericDateRegex.containsMatchIn(line)) score -= 3
                score += maxOf(0, 8 - index)

                candidates += value to score
            }
        }

        return candidates.maxByOrNull { it.second }?.first.orEmpty()
    }

    private fun extractBestName(lines: List<String>): String {
        return lines.map { line ->
            val upper = line.uppercase(profile.locale)
            val tokens = upper.split(Regex("\\s+"))
            var score = 0

            if (line.length in 4..64) score += 2
            if (line.any(Char::isLetter)) score += 3
            if (!moneyRegex.containsMatchIn(line)) score += 3
            if (!genericDateRegex.containsMatchIn(line)) score += 2
            if (!barcodeLineRegex.matches(line.replace(" ", ""))) score += 2

            val unitTokenCount = tokens.count { it in profile.unitKeywords }
            when (unitTokenCount) {
                0 -> score += 1
                1 -> score += 0
                else -> score -= 2
            }

            if (tokens.none { it in profile.bannedTokens }) score += 3
            if (tokens.count { it.any(Char::isLetter) } >= 2) score += 2

            if (profile.currencySymbols.any { upper.contains(it.uppercase(profile.locale)) }) score -= 5
            if (profile.totalKeywords.any { upper.contains(it) }) score -= 3
            if (upper.contains("CNPJ") || upper.contains("NIF") || upper.contains("TAX ID")) score -= 4

            upper to score
        }
            .filter { (_, score) -> score >= 6 }
            .maxByOrNull { it.second }
            ?.first
            .orEmpty()
    }

    private fun buildConfidence(
        barcode: String,
        name: String,
        price: String
    ): Float {
        var confidence = 0f
        if (barcode.isNotBlank()) confidence += 0.45f
        if (name.isNotBlank()) confidence += 0.25f
        if (price.isNotBlank()) confidence += 0.30f
        return confidence.coerceIn(0f, 1f)
    }
}