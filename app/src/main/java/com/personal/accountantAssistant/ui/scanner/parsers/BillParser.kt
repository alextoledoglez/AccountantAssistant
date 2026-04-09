package com.personal.accountantAssistant.ui.scanner.parsers

import com.personal.accountantAssistant.ui.scanner.ScanResult

internal class BillParser(private val profile: ScanParsingProfile) {

    private val amountRegex = Regex(
        """(?:${profile.currencySymbols.joinToString("|") { Regex.escape(it) }}\s*)?(\d{1,3}(?:[.,]\d{3})*[.,]\d{2}|\d+[.,]\d{2})""",
        RegexOption.IGNORE_CASE
    )

    fun parse(text: String): ScanResult.Bill? {
        if (text.isBlank()) return null

        val normalized = normalize(text)
        val lines = normalized.lines().map { it.trim() }.filter { it.isNotBlank() }
        val upperLines = lines.map { it.uppercase(profile.locale) }
        val upperText = normalized.uppercase(profile.locale)

        val amount = extractAmount(lines, upperLines)
        val dueDate = extractDate(lines, upperLines)
        val provider = extractProvider(upperLines, upperText)
        val confidence = buildConfidence(provider, amount, dueDate, upperText)

        if (amount.isBlank() && dueDate.isBlank()) return null
        if (confidence < 0.45f) return null

        return ScanResult.Bill(
            name = provider,
            value = amount,
            date = dueDate,
            confidence = confidence,
            rawText = text
        )
    }

    private fun normalize(text: String): String {
        return text
            .replace(Regex("""[ \t]+"""), " ")
            .replace(Regex("""\n+"""), "\n")
            .trim()
    }

    private fun extractAmount(lines: List<String>, upperLines: List<String>): String {
        val candidates = mutableListOf<Pair<String, Int>>()

        upperLines.forEachIndexed { index, upper ->
            val original = lines[index]

            amountRegex.findAll(original).forEach { match ->
                val value = match.groupValues.getOrNull(1).orEmpty()
                if (value.isBlank()) return@forEach

                var score = 0
                if (profile.amountKeywords.any { upper.contains(it) }) score += 8
                if (profile.currencySymbols.any { upper.contains(it.uppercase(profile.locale)) }) score += 3
                if (profile.billStrongKeywords.any { upper.contains(it) }) score += 2
                if (upper.contains("CONSUMO") || upper.contains("USAGE")) score -= 2
                if (upper.contains("JUROS") || upper.contains("INTEREST")) score -= 2
                if (upper.contains("MULTA") || upper.contains("FEE")) score -= 2
                if (upper.contains("LEITURA") || upper.contains("READING")) score -= 2
                if (upper.contains("MEDIÇÃO") || upper.contains("MEDICAO") || upper.contains("MEASUREMENT")) score -= 2
                score += maxOf(0, 10 - index)

                candidates += value to score
            }
        }

        if (candidates.isNotEmpty()) {
            return candidates.maxByOrNull { it.second }?.first.orEmpty()
        }

        return lines
            .flatMap { line ->
                amountRegex.findAll(line).map { it.groupValues.getOrNull(1).orEmpty() }.toList()
            }
            .filter { candidate ->
                // Require at least R$ 1,00 (100 centavos) to avoid capturing noise
                val centavos = candidate.replace(Regex("[.,]"), "").toLongOrNull() ?: 0L
                centavos >= 100
            }
            .maxByOrNull { candidate ->
                candidate.replace(Regex("[.,]"), "").toLongOrNull() ?: 0L
            }
            .orEmpty()
    }

    private fun extractDate(lines: List<String>, upperLines: List<String>): String {
        val candidates = mutableListOf<Pair<String, Int>>()

        upperLines.forEachIndexed { index, upper ->
            val window = buildString {
                appendLine(lines.getOrNull(index).orEmpty())
                appendLine(lines.getOrNull(index + 1).orEmpty())
                appendLine(lines.getOrNull(index + 2).orEmpty())
            }

            profile.datePatterns.forEach { regex ->
                regex.findAll(window).forEach { match ->
                    val date = match.groupValues.getOrNull(1).orEmpty()
                    if (date.isBlank()) return@forEach

                    var score = 0
                    if (profile.dueDateKeywords.any { upper.contains(it) }) score += 8
                    if (upper.contains("VENC") || upper.contains("DUE") || upper.contains("PRAZO")) score += 4
                    score += maxOf(0, 10 - index)

                    candidates += date to score
                }
            }
        }

        if (candidates.isNotEmpty()) {
            return candidates.maxByOrNull { it.second }?.first.orEmpty()
        }

        return lines.firstNotNullOfOrNull { line ->
            profile.datePatterns.firstNotNullOfOrNull { regex ->
                regex.find(line)?.groupValues?.getOrNull(1)
            }
        }.orEmpty()
    }

    private fun extractProvider(
        upperLines: List<String>,
        upperText: String
    ): String {
        profile.knownProviders.firstOrNull { upperText.contains(it) }?.let { return it }

        return upperLines
            .mapIndexed { index, line ->
                var score = 0
                if (index <= 3) score += 5
                if (line.any(Char::isLetter)) score += 2
                if (line.none(Char::isDigit)) score += 2
                if (!amountRegex.containsMatchIn(line)) score += 2
                if (profile.billStrongKeywords.any { line.contains(it) }) score += 1
                line to score
            }
            .filter { it.second >= 6 }
            .maxByOrNull { it.second }
            ?.first
            .orEmpty()
    }

    private fun buildConfidence(
        provider: String,
        amount: String,
        dueDate: String,
        upperText: String
    ): Float {
        var confidence = 0f
        if (provider.isNotBlank()) confidence += 0.20f
        if (amount.isNotBlank()) confidence += 0.40f
        if (dueDate.isNotBlank()) confidence += 0.30f
        if (profile.billStrongKeywords.any { upperText.contains(it) }) confidence += 0.10f
        return confidence.coerceIn(0f, 1f)
    }
}