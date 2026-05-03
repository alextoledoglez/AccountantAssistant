package com.personal.accountantAssistant.domain.repository

import com.personal.accountantAssistant.domain.models.BarcodeProductModel

interface BarcodeProductRepository {
    suspend fun getProduct(barcode: String): BarcodeProductModel?
}