package com.personal.accountantAssistant.domain.useCases.buys

import com.personal.accountantAssistant.domain.repository.BarcodeProductRepository

class GetBarcodeProductNameUseCaseImpl(
    val repository: BarcodeProductRepository
) : GetBarcodeProductNameUseCase {
    override suspend fun invoke(barcode: String): String {
        return repository.getProduct(barcode)?.name.orEmpty()
    }
}