package com.personal.accountantAssistant.data.repository

import com.personal.accountantAssistant.data.mappers.toBarcodeProductModel
import com.personal.accountantAssistant.data.remote.BarcodeProductRemoteDataSource
import com.personal.accountantAssistant.domain.models.BarcodeProductModel
import com.personal.accountantAssistant.domain.repository.BarcodeProductRepository

class BarcodeProductDataRepository(
    private val dataSource: BarcodeProductRemoteDataSource
) : BarcodeProductRepository {
    override suspend fun getProduct(barcode: String): BarcodeProductModel? {
        return dataSource.getProduct(barcode)?.toBarcodeProductModel()
    }
}