package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.response.BarcodeProductResponse
import com.personal.accountantAssistant.extensions.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class BarcodeProductRemoteDataSource {
    suspend fun getProduct(barcode: String): BarcodeProductResponse? {
        return runCatching {
            val url = "https://world.openfoodfacts.org/api/v0/product/$barcode.json"
            val jsonString = withContext(Dispatchers.IO) { URL(url).readText() }
            jsonString.fromJson(BarcodeProductResponse::class)
        }.getOrNull()
    }
}