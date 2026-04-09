package com.personal.accountantAssistant.ui.scanner.parsers

import com.personal.accountantAssistant.ui.scanner.ScanResult

internal class BillConsensusAggregator(private val requiredHits: Int = 2) {
    private data class Bucket(
        var hits: Int = 0,
        var name: String = "",
        var value: String = "",
        var date: String = "",
        var rawText: String = "",
        var confidence: Float = 0f
    )

    private val buckets = linkedMapOf<String, Bucket>()

    private fun buildKey(candidate: ScanResult.Bill): String {
        val value = candidate.value.takeIf { it.isNotBlank() } ?: "_"
        val date = candidate.date.takeIf { it.isNotBlank() } ?: "_"
        val name = candidate.name.takeIf { it.isNotBlank() } ?: "_"
        return "$name|$value|$date"
    }

    private fun pickBest(current: String, incoming: String): String = when {
        current.isBlank() -> incoming
        incoming.isBlank() -> current
        incoming.length > current.length -> incoming
        else -> current
    }

    fun offer(candidate: ScanResult.Bill): ScanResult.Bill? {
        val key = buildKey(candidate)
        val updated = (buckets[key] ?: Bucket()).apply {
            hits++
            name = pickBest(current = name, incoming = candidate.name)
            value = pickBest(current = value, incoming = candidate.value)
            date = pickBest(current = date, incoming = candidate.date)
            rawText = candidate.rawText.ifBlank { rawText }
            confidence = maxOf(a = confidence, b = candidate.confidence)
        }
        buckets[key] = updated

        return if (updated.hits >= requiredHits) {
            ScanResult.Bill(
                name = updated.name,
                value = updated.value,
                date = updated.date,
                confidence = updated.confidence,
                rawText = updated.rawText
            )
        } else {
            null
        }
    }
}