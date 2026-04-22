package com.personal.accountantAssistant.ui.scanner.parser

sealed class BankSlipData {
    data class BankSlip(
        val barcode44: String,
        val line47: String?,
        val bankCode: String,
        val currencyCode: String,
        val amount: String?,
        val dueDateFactor: String?,
        val freeField: String
    ) : BankSlipData()

    data class CollectionSlip(
        val barcode44: String,
        val line48: String?,
        val segment: String,
        val amount: String?,
        val companyField: String,
        val reference: String
    ) : BankSlipData()

    data class Unknown(val raw: String) : BankSlipData()
}
