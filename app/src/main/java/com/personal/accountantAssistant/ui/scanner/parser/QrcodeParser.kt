package com.personal.accountantAssistant.ui.scanner.parser

import com.google.mlkit.vision.barcode.common.Barcode

/**
 * Parses QR code barcodes into [ScannedCodeData].
 *
 * Handles two QR payloads:
 *  - **PIX / Bank Slip QR** (EMV TLV format, identified by [PIX_PREFIX]): extracts merchant name
 *    and transaction amount from EMV tags [EMV_TAG_MERCHANT_NAME] and [EMV_TAG_TRANSACTION_AMOUNT].
 *  - **Free-text / URL QR**: uses [displayValue] as the name and attempts to extract a monetary
 *    value from the raw string via [MONETARY_VALUE_PATTERN].
 *
 * EMV TLV encoding: each field is `[tag 2 chars][length 2 chars][value N chars]`.
 */
object QrcodeParser {

    // ── EMV TLV ───────────────────────────────────────────────────────────────
    private const val EMV_TLV_HEADER_SIZE = 4       // 2 (tag) + 2 (length)
    private const val EMV_TAG_MERCHANT_NAME = "59"
    private const val EMV_TAG_TRANSACTION_AMOUNT = "54"

    // ── Free-text QR ──────────────────────────────────────────────────────────
    private const val MAX_DISPLAY_NAME_LENGTH = 80

    // ── Public constants (used by BarcodeParser to route QR codes here) ───────
    const val PIX_PREFIX = "000201"
    const val MONETARY_VALUE_PATTERN = """(\d{1,3}(?:[.,]\d{3})*[.,]\d{2})"""

    private fun parsePixEmv(raw: String): Triple<String, String, String> {
        val fields = mutableMapOf<String, String>()
        var pos = 0
        while (pos + EMV_TLV_HEADER_SIZE <= raw.length) {
            val tag = raw.substring(pos, pos + 2)
            val len = raw.substring(pos + 2, pos + 4).toIntOrNull() ?: break
            val end = minOf(pos + EMV_TLV_HEADER_SIZE + len, raw.length)
            fields[tag] = raw.substring(pos + EMV_TLV_HEADER_SIZE, end)
            pos += EMV_TLV_HEADER_SIZE + len
        }
        return Triple(
            fields[EMV_TAG_MERCHANT_NAME].orEmpty(),
            fields[EMV_TAG_TRANSACTION_AMOUNT].orEmpty(),
            "" // PIX QR codes do not carry a due date
        )
    }

    fun parse(barcode: Barcode): ScannedCodeData {
        val rawValue     = barcode.rawValue.orEmpty()
        val displayValue = barcode.displayValue.orEmpty()
        val base         = ScannedCodeData(barcode = rawValue, confidence = 1f, rawText = rawValue)
        return if (rawValue.startsWith(PIX_PREFIX)) {
            val (name, value, date) = parsePixEmv(rawValue)
            base.copy(name = name, value = value, date = date)
        } else {
            val name  = displayValue.ifBlank { rawValue }.take(MAX_DISPLAY_NAME_LENGTH)
            val value = Regex(MONETARY_VALUE_PATTERN).find(rawValue)?.groupValues?.getOrNull(1)
            base.copy(name = name, value = value.orEmpty())
        }
    }
}