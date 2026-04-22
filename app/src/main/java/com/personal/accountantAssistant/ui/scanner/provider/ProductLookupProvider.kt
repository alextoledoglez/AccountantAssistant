package com.personal.accountantAssistant.ui.scanner.provider

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

// Produto EAN/UPC em modo Buy: consulta Open Food Facts
object ProductLookupProvider {

    private fun getSpec(barcode: String): String {
        return "https://world.openfoodfacts.org/api/v0/product/$barcode.json"
    }

    suspend fun getProductName(barcode: String): String = withContext(Dispatchers.IO) {
        runCatching {
            val json = URL(getSpec(barcode)).readText()
            val obj = JSONObject(json)
            if (obj.optInt(STATUS) == 1) {
                obj.getJSONObject(PRODUCT).optString(PRODUCT_NAME, "")
            } else ""
        }.getOrDefault("")
    }

    const val STATUS = "status"
    const val PRODUCT = "product"
    const val PRODUCT_NAME = "product_name"
}