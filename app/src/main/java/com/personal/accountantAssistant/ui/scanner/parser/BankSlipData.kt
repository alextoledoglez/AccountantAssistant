package com.personal.accountantAssistant.ui.scanner.parser

sealed class BankSlipData {
    data class BankSlip(
        val barcode: String,
        val bankName: String,
        val currencyCode: String,
        val amount: String?,
        val dueDateFactor: String?,
        val freeField: String
    ) : BankSlipData()

    data class CollectionSlip(
        val barcode: String,
        val providerName: String?,
        val segment: String,
        val amount: String?,
        val companyField: String,
        val reference: String
    ) : BankSlipData()

    data class Unknown(val barcode: String) : BankSlipData()
}
