package com.personal.accountantAssistant.ui.scanner.parser

import android.util.Log
import com.personal.accountantAssistant.extensions.toDigitsStr
import com.personal.accountantAssistant.ui.scanner.mappers.BankCodeMapper
import java.util.Locale

/**
 * Parses FEBRABAN-compliant bank slip barcodes and typeable lines into [ScannedCodeData].
 *
 * Accepts three input formats (digit count after stripping non-digits):
 *  - **44 digits** — raw barcode (both bank Slip and collection/utility slip)
 *  - **47 digits** — bank Slip typeable line
 *  - **48 digits** — collection/utility slip typeable line
 *
 * Bank Slip barcode layout (44 digits):
 *   `[bank 3][currency 1][check 1][due-date-factor 4][amount 10][free-field 25]`
 *
 * Collection slip barcode layout (44 digits, starts with "8"):
 *   `[product 1][segment 1][real-value-id 1][check 1][amount 11][company 4][free-field 25]`
 */
object BankSlipBarcodeParser {

    // ── 44-digit barcode field ranges (0-based, exclusive end) ────────────────
    private const val BANK_CODE_START = 0
    private const val BANK_CODE_END = 3
    private const val CURRENCY_CODE_START = 3
    private const val CURRENCY_CODE_END = 4
    private const val DUE_DATE_FACTOR_START = 5
    private const val DUE_DATE_FACTOR_END = 9
    private const val AMOUNT_START = 9
    private const val AMOUNT_END = 19
    private const val FREE_FIELD_START = 19

    // ── Collection slip field ranges (within the 44-digit barcode) ────────────
    private const val COLLECTION_SEGMENT_START = 1
    private const val COLLECTION_SEGMENT_END = 2
    private const val COLLECTION_REFERENCE_START = 2
    private const val COLLECTION_REFERENCE_END = 3
    private const val COLLECTION_AMOUNT_START = 4
    private const val COLLECTION_AMOUNT_END = 15
    private const val COLLECTION_COMPANY_FIELD_START = 15
    private const val COLLECTION_COMPANY_FIELD_END = 19

    // ── Input length discriminators ───────────────────────────────────────────
    private const val BARCODE_LENGTH = 44
    private const val BANK_SLIP_LINE_LENGTH = 47
    private const val COLLECTION_LINE_LENGTH = 48

    // ── Bank Slip typeable line (47 digits) → barcode reconstruction offsets ─
    private const val BANK_LINE_FIELD1_END = 9
    private const val BANK_LINE_FIELD2_START = 10
    private const val BANK_LINE_FIELD2_END = 20
    private const val BANK_LINE_FIELD3_START = 21
    private const val BANK_LINE_FIELD3_END = 31
    private const val BANK_LINE_DV_START = 32
    private const val BANK_LINE_DV_END = 33
    private const val BANK_LINE_FACTOR_VALUE_START = 33
    private const val BANK_LINE_FACTOR_VALUE_END = 47
    private const val BANK_LINE_BANK_CURRENCY_END = 4
    private const val BANK_LINE_FREE_PART_START = 4

    // ── Collection typeable line (48 digits) → barcode reconstruction offsets ──
    private const val COLLECTION_SEG1_END = 11
    private const val COLLECTION_SEG2_START = 12
    private const val COLLECTION_SEG2_END = 23
    private const val COLLECTION_SEG3_START = 24
    private const val COLLECTION_SEG3_END = 35
    private const val COLLECTION_SEG4_START = 36
    private const val COLLECTION_SEG4_END = 47

    // ── Misc ──────────────────────────────────────────────────────────────────
    private const val COLLECTION_SLIP_PREFIX = "8"
    private const val AMOUNT_FORMAT = "%.2f"

    private fun parseBankSlip(barcode44: String): BankSlipData.BankSlip {
        val bankCode = barcode44.substring(BANK_CODE_START, BANK_CODE_END)
        return BankSlipData.BankSlip(
            barcode44 = barcode44,
            bankName = BankCodeMapper.map(bankCode),
            currencyCode = barcode44.substring(CURRENCY_CODE_START, CURRENCY_CODE_END),
            dueDateFactor = barcode44.substring(DUE_DATE_FACTOR_START, DUE_DATE_FACTOR_END),
            amount = parseAmount(barcode44.substring(AMOUNT_START, AMOUNT_END)),
            freeField = barcode44.substring(FREE_FIELD_START)
        )
    }

    private fun parseCollectionSlip(barcode44: String): BankSlipData {
        return BankSlipData.CollectionSlip(
            barcode44 = barcode44,
            providerName = "",
            segment = barcode44.substring(COLLECTION_SEGMENT_START, COLLECTION_SEGMENT_END),
            amount = parseAmountForCollection(barcode44),
            companyField = barcode44.substring(
                COLLECTION_COMPANY_FIELD_START,
                COLLECTION_COMPANY_FIELD_END
            ),
            reference = barcode44.substring(COLLECTION_REFERENCE_START, COLLECTION_REFERENCE_END)
        )
    }

    private fun parseBarcode44(barcode44: String): BankSlipData = when {
        barcode44.startsWith(COLLECTION_SLIP_PREFIX) -> parseCollectionSlip(barcode44)
        else -> parseBankSlip(barcode44)
    }

    private fun parseAmount(raw: String): String? {
        if (raw.all { it == '0' }) return null
        val cents = raw.toLongOrNull() ?: return null
        return AMOUNT_FORMAT.format(Locale.US, cents / 100.0)
    }

    private fun parseAmountForCollection(barcode44: String): String? {
        val valueBlock = barcode44.substring(COLLECTION_AMOUNT_START, COLLECTION_AMOUNT_END)
        if (valueBlock.all { it == '0' }) return null
        val cents = valueBlock.toLongOrNull() ?: return null
        return AMOUNT_FORMAT.format(Locale.US, cents / 100.0)
    }

    private fun bankSlipLineToBarcode(line47: String): String {
        val field1 = line47.substring(0, BANK_LINE_FIELD1_END)
        val field2 = line47.substring(BANK_LINE_FIELD2_START, BANK_LINE_FIELD2_END)
        val field3 = line47.substring(BANK_LINE_FIELD3_START, BANK_LINE_FIELD3_END)
        val dv = line47.substring(BANK_LINE_DV_START, BANK_LINE_DV_END)
        val factorAndValue =
            line47.substring(BANK_LINE_FACTOR_VALUE_START, BANK_LINE_FACTOR_VALUE_END)
        return buildString {
            append(field1.substring(0, BANK_LINE_BANK_CURRENCY_END))
            append(dv)
            append(factorAndValue)
            append(field1.substring(BANK_LINE_FREE_PART_START, BANK_LINE_FIELD1_END))
            append(field2)
            append(field3)
        }
    }

    private fun collectionLineToBarcode(line48: String): String = buildString {
        append(line48.substring(0, COLLECTION_SEG1_END))
        append(line48.substring(COLLECTION_SEG2_START, COLLECTION_SEG2_END))
        append(line48.substring(COLLECTION_SEG3_START, COLLECTION_SEG3_END))
        append(line48.substring(COLLECTION_SEG4_START, COLLECTION_SEG4_END))
    }

    fun parse(data: ScannedCodeData): ScannedCodeData? {
        val digits = data.rawText.toDigitsStr()
        val bankSlipData = when (digits.length) {
            BANK_SLIP_LINE_LENGTH -> parseBankSlip(barcode44 = bankSlipLineToBarcode(line47 = digits))
            COLLECTION_LINE_LENGTH -> parseCollectionSlip(barcode44 = collectionLineToBarcode(line48 = digits))
            BARCODE_LENGTH -> parseBarcode44(digits)
            else -> BankSlipData.Unknown(digits)
        }
        Log.i(TAG, "bankSlipData: $bankSlipData")
        return when (bankSlipData) {
            is BankSlipData.BankSlip -> data.copy(
                name = bankSlipData.bankName,
                value = bankSlipData.amount.orEmpty(),
                date = bankSlipData.dueDateFactor.orEmpty(),
                confidence = 1f
            )

            is BankSlipData.CollectionSlip -> data.copy(
                segment = bankSlipData.segment,
                company = bankSlipData.companyField,
                name = bankSlipData.providerName.orEmpty(),
                value = bankSlipData.amount.orEmpty(),
                confidence = 1f
            )

            else -> null
        }
    }

    val TAG: String = BankSlipBarcodeParser.javaClass.simpleName
}