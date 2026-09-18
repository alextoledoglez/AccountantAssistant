package com.personal.accountantAssistant.ui.scanner.parser

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

    private const val BANK_CODE_START = 0
    private const val BANK_CODE_END = 3
    private const val CURRENCY_CODE_START = 3
    private const val CURRENCY_CODE_END = 4
    private const val DUE_DATE_FACTOR_START = 5
    private const val DUE_DATE_FACTOR_END = 9
    private const val AMOUNT_START = 9
    private const val AMOUNT_END = 19
    private const val FREE_FIELD_START = 19

    private const val COLLECTION_SEGMENT_START = 1
    private const val COLLECTION_SEGMENT_END = 2
    private const val COLLECTION_REFERENCE_START = 2
    private const val COLLECTION_REFERENCE_END = 3
    private const val COLLECTION_AMOUNT_START = 4
    private const val COLLECTION_AMOUNT_END = 15
    private const val COLLECTION_COMPANY_FIELD_START = 15
    private const val COLLECTION_COMPANY_FIELD_END = 19

    private const val BARCODE_LENGTH = 44
    private const val BANK_SLIP_LINE_LENGTH = 47
    private const val COLLECTION_LINE_LENGTH = 48

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

    private const val COLLECTION_SEG1_END = 11
    private const val COLLECTION_SEG2_START = 12
    private const val COLLECTION_SEG2_END = 23
    private const val COLLECTION_SEG3_START = 24
    private const val COLLECTION_SEG3_END = 35
    private const val COLLECTION_SEG4_START = 36
    private const val COLLECTION_SEG4_END = 47

    private const val COLLECTION_SLIP_PREFIX = "8"
    private const val AMOUNT_FORMAT = "%.2f"

    private fun parseBankSlip(barcode: String): BankSlipData.BankSlip = BankSlipData.BankSlip(
        barcode = barcode,
        bankName = BankCodeMapper.map(bankCode = barcode.substring(BANK_CODE_START, BANK_CODE_END)),
        currencyCode = barcode.substring(CURRENCY_CODE_START, CURRENCY_CODE_END),
        dueDateFactor = barcode.substring(DUE_DATE_FACTOR_START, DUE_DATE_FACTOR_END),
        amount = parseAmount(barcode.substring(AMOUNT_START, AMOUNT_END)),
        freeField = barcode.substring(FREE_FIELD_START)
    )

    private fun parseCollectionSlip(barcode: String): BankSlipData = BankSlipData.CollectionSlip(
        barcode = barcode,
        providerName = "",
        segment = barcode.substring(COLLECTION_SEGMENT_START, COLLECTION_SEGMENT_END),
        amount = parseAmountForCollection(barcode),
        companyField = barcode.substring(
            COLLECTION_COMPANY_FIELD_START,
            COLLECTION_COMPANY_FIELD_END
        ),
        reference = barcode.substring(COLLECTION_REFERENCE_START, COLLECTION_REFERENCE_END)
    )

    private fun parseBarcode(barcode: String): BankSlipData = when {
        barcode.startsWith(COLLECTION_SLIP_PREFIX) -> parseCollectionSlip(barcode)
        else -> parseBankSlip(barcode)
    }

    private fun parseAmount(barcode: String): String? {
        if (barcode.all { it == '0' }) return null
        val cents = barcode.toLongOrNull() ?: return null
        return AMOUNT_FORMAT.format(Locale.US, cents / 100.0)
    }

    private fun parseAmountForCollection(barcode: String): String? {
        val valueBlock = barcode.substring(COLLECTION_AMOUNT_START, COLLECTION_AMOUNT_END)
        if (valueBlock.all { it == '0' }) return null
        val cents = valueBlock.toLongOrNull() ?: return null
        return AMOUNT_FORMAT.format(Locale.US, cents / 100.0)
    }

    private fun bankSlipLineToBarcode(barcode: String): String {
        val field1 = barcode.substring(0, BANK_LINE_FIELD1_END)
        val field2 = barcode.substring(BANK_LINE_FIELD2_START, BANK_LINE_FIELD2_END)
        val field3 = barcode.substring(BANK_LINE_FIELD3_START, BANK_LINE_FIELD3_END)
        val dv = barcode.substring(BANK_LINE_DV_START, BANK_LINE_DV_END)
        val factorAndValue =
            barcode.substring(BANK_LINE_FACTOR_VALUE_START, BANK_LINE_FACTOR_VALUE_END)
        return buildString {
            append(field1.substring(0, BANK_LINE_BANK_CURRENCY_END))
            append(dv)
            append(factorAndValue)
            append(field1.substring(BANK_LINE_FREE_PART_START, BANK_LINE_FIELD1_END))
            append(field2)
            append(field3)
        }
    }

    private fun String.toCollectionLineBarcode(): String = this
        .substring(0, COLLECTION_SEG1_END)
        .substring(COLLECTION_SEG2_START, COLLECTION_SEG2_END)
        .substring(COLLECTION_SEG3_START, COLLECTION_SEG3_END)
        .substring(COLLECTION_SEG4_START, COLLECTION_SEG4_END)

    private fun getBankSlipData(barcode: String) = when (barcode.length) {
        BANK_SLIP_LINE_LENGTH -> parseBankSlip(barcode = bankSlipLineToBarcode(barcode))
        COLLECTION_LINE_LENGTH -> parseCollectionSlip(barcode.toCollectionLineBarcode())
        BARCODE_LENGTH -> parseBarcode(barcode)
        else -> BankSlipData.Unknown(barcode)
    }

    fun parse(data: ScannedCodeData): ScannedCodeData? {
        val bankSlipData = getBankSlipData(data.barcode)
        return when (bankSlipData) {
            is BankSlipData.BankSlip -> data.copy(
                name = bankSlipData.bankName,
                amount = bankSlipData.amount.orEmpty(),
                date = bankSlipData.dueDateFactor.orEmpty()
            )

            is BankSlipData.CollectionSlip -> data.copy(
                segment = bankSlipData.segment,
                company = bankSlipData.companyField,
                name = bankSlipData.providerName.orEmpty(),
                amount = bankSlipData.amount.orEmpty()
            )

            else -> null
        }
    }
}
