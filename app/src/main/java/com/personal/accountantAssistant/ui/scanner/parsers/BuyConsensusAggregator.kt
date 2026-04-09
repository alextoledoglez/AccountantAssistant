package com.personal.accountantAssistant.ui.scanner.parsers

import com.personal.accountantAssistant.ui.scanner.ScanResult

internal class BuyConsensusAggregator(private val requiredHits: Int = 3) {
    private data class Bucket(
        var hits: Int = 0,
        var barcode: String = "",
        var name: String = "",
        var price: String = "",
        var rawText: String = "",
        var confidence: Float = 0f
    )

    private val buckets = linkedMapOf<String, Bucket>()

    private fun buildKey(candidate: ScanResult.Buy): String {
        val barcode = candidate.barcode.takeIf { it.isNotBlank() } ?: "_"
        val price = candidate.price.takeIf { it.isNotBlank() } ?: "_"
        val name = candidate.name.takeIf { it.isNotBlank() } ?: "_"
        return "$barcode|$price|$name"
    }

    private fun pickBest(current: String, incoming: String): String = when {
        current.isBlank() -> incoming
        incoming.isBlank() -> current
        incoming.length > current.length -> incoming
        else -> current
    }

    fun offer(candidate: ScanResult.Buy): ScanResult.Buy? {
        val key = buildKey(candidate)
        val updated = (buckets[key] ?: Bucket()).apply {
            hits++
            barcode = pickBest(current = barcode, incoming = candidate.barcode)
            name = pickBest(current = name, incoming = candidate.name)
            price = pickBest(current = price, incoming = candidate.price)
            rawText = candidate.rawText.ifBlank { rawText }
            confidence = maxOf(a = confidence, b = candidate.confidence)
        }
        buckets[key] = updated

        return if (updated.hits >= requiredHits) {
            ScanResult.Buy(
                barcode = updated.barcode,
                name = updated.name,
                price = updated.price,
                confidence = updated.confidence,
                rawText = updated.rawText
            )
        } else {
            null
        }
    }
}