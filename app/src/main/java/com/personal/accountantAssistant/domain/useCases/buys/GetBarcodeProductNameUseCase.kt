package com.personal.accountantAssistant.domain.useCases.buys

interface GetBarcodeProductNameUseCase {
    suspend operator fun invoke(barcode: String): String
}